package pl.polsl.blissapp.ui.views.alchemy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.mapping.DrawableMapper;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

public class AlchemyAdapter extends RecyclerView.Adapter<AlchemyAdapter.ViewHolder> {

    public enum MatchStatus { NONE, INCORRECT, PARTIAL, EXACT }

    public static class CraftingItem {
        public final Object object;
        public final MatchStatus status;
        public final String label;

        public CraftingItem(Object object, MatchStatus status) {
            this(object, status, null);
        }

        public CraftingItem(Object object, MatchStatus status, String label) {
            this.object = object;
            this.status = status;
            this.label = label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CraftingItem that = (CraftingItem) o;
            return Objects.equals(object, that.object) && status == that.status && Objects.equals(label, that.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(object, status, label);
        }
    }

    private final List<CraftingItem> items = new ArrayList<>();
    private final SymbolRepository symbolRepository;
    private final int layoutResId;
    private final OnItemClickListener clickListener;
    private int itemWidth = -1;

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    public AlchemyAdapter(SymbolRepository symbolRepository, int layoutResId, OnItemClickListener clickListener) {
        this.symbolRepository = symbolRepository;
        this.layoutResId = layoutResId;
        this.clickListener = clickListener;
    }

    public void setItems(List<CraftingItem> newItems) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CraftingItemDiffCallback(items, newItems));
        items.clear();
        items.addAll(newItems);
        diffResult.dispatchUpdatesTo(this);
    }

    public void setItemWidth(int width) {
        this.itemWidth = width;
        notifyDataSetChanged();
    }

    private static class CraftingItemDiffCallback extends DiffUtil.Callback {
        private final List<CraftingItem> oldList;
        private final List<CraftingItem> newList;

        CraftingItemDiffCallback(List<CraftingItem> oldList, List<CraftingItem> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override public int getOldListSize() { return oldList.size(); }
        @Override public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldObj = oldList.get(oldItemPosition).object;
            Object newObj = newList.get(newItemPosition).object;
            if (oldObj instanceof Primitive && newObj instanceof Primitive) return oldObj == newObj;
            if (oldObj instanceof Symbol && newObj instanceof Symbol) return ((Symbol) oldObj).index() == ((Symbol) newObj).index();
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CraftingItem craftingItem = items.get(position);
        Object item = craftingItem.object;

        if (itemWidth > 0) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            lp.width = itemWidth;
            holder.itemView.setLayoutParams(lp);
        }

        holder.resetView();

        if (item instanceof Symbol symbol) holder.loadSymbol(symbol, symbolRepository);
        else if (item instanceof Primitive primitive) holder.loadPrimitive(primitive);

        holder.tvLabel.setText(craftingItem.label != null ? craftingItem.label : "");
        holder.tvLabel.setVisibility(craftingItem.label != null && !craftingItem.label.isEmpty() ? View.VISIBLE : View.GONE);
        holder.applyStatus(craftingItem.status, item);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final MaterialCardView cardView;
        private final TextView tvLabel;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_symbol);
            cardView = view.findViewById(R.id.filter_key_card);
            tvLabel = view.findViewById(R.id.tv_symbol_label);
        }

        void applyStatus(MatchStatus status, Object item) {
            if (cardView == null) return;

            Context context = itemView.getContext();
            float density = context.getResources().getDisplayMetrics().density;

            // 1. Get the opaque base background color
            int baseBgColor = ContextCompat.getColor(context, R.color.keyboard_key_background);

            // 2. Set Defaults (NO outline, standard opaque background)
            int strokeColor = Color.TRANSPARENT;
            int strokeWidth = 0;
            int bgColor = baseBgColor;

            if (item instanceof Primitive primitive) {
                boolean isChild = primitive.getParent() != null;

                if (status == MatchStatus.INCORRECT) {
                    // Red outline for ALL incorrect primitives
                    strokeColor = Color.parseColor("#F44336");
                    strokeWidth = (int) (2 * density);

                    if (isChild) {
                        bgColor = ColorUtils.blendARGB(baseBgColor, Color.parseColor("#F44336"), 0.2f);
                    }

                } else if (status == MatchStatus.EXACT) {
                    if (isChild) {
                        bgColor = ColorUtils.blendARGB(baseBgColor, Color.parseColor("#4CAF50"), 0.2f);
                    }

                } else if (status == MatchStatus.PARTIAL) {
                    if (isChild) {
                        bgColor = ColorUtils.blendARGB(baseBgColor, Color.parseColor("#FFEB3B"), 0.2f);
                    }
                }

            } else if (item instanceof Symbol) {
                if (status == MatchStatus.EXACT) {
                    bgColor = ColorUtils.blendARGB(baseBgColor, Color.parseColor("#4CAF50"), 0.2f);
                    strokeColor = Color.parseColor("#4CAF50");
                    strokeWidth = (int) (2 * density);
                }
            }

            cardView.setStrokeColor(strokeColor);
            cardView.setStrokeWidth(strokeWidth);
            cardView.setCardBackgroundColor(bgColor);
        }

        void resetView() {
            if (imageView != null) {
                imageView.setImageDrawable(null);
                imageView.setTag(null);
            }
            itemView.clearAnimation();
            itemView.setRotation(0f);
            itemView.setScaleX(1f);
            itemView.setScaleY(1f);
            itemView.setAlpha(1f);
        }

        void loadPrimitive(Primitive primitive) {
            if (imageView != null) imageView.setImageResource(DrawableMapper.getDrawableRes(primitive));
        }

        void loadSymbol(Symbol symbol, SymbolRepository repository) {
            if (imageView == null) return;
            imageView.setTag(symbol.index());
            repository.getSvg(symbol, new Callback<String, Exception>() {
                @Override
                public void onSuccess(String svgString) {
                    if (!Objects.equals(imageView.getTag(), symbol.index())) return;
                    try {
                        SVG svg = SVG.getFromString(svgString);
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                        imageView.post(() -> {
                            if (Objects.equals(imageView.getTag(), symbol.index())) imageView.setImageDrawable(drawable);
                        });
                    } catch (SVGParseException ignored) {}
                }
                @Override public void onFailure(Exception data) {}
            });
        }
    }
}