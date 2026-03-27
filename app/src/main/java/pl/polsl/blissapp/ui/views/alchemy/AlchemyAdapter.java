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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        public final int count;

        public CraftingItem(Object object, MatchStatus status) {
            this(object, status, null, 1);
        }

        public CraftingItem(Object object, MatchStatus status, String label) {
            this(object, status, label, 1);
        }

        public CraftingItem(Object object, MatchStatus status, String label, int count) {
            this.object = object;
            this.status = status;
            this.label = label;
            this.count = count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CraftingItem that = (CraftingItem) o;
            return count == that.count && Objects.equals(object, that.object) && status == that.status && Objects.equals(label, that.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(object, status, label, count);
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

        if (item instanceof Symbol symbol) {
            holder.loadSymbol(symbol, symbolRepository);
        } else if (item instanceof Primitive primitive) {
            String letterLabel = primitive.getLetterLabel();
            if (letterLabel != null) {
                holder.imageView.setVisibility(View.GONE);
                holder.tvLetter.setVisibility(View.VISIBLE);
                holder.tvLetter.setText(letterLabel);
            } else {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.tvLetter.setVisibility(View.GONE);
                holder.loadPrimitive(primitive);
            }
        }

        if (craftingItem.count > 1) {
            holder.tvCounter.setVisibility(View.VISIBLE);
            holder.tvCounter.setText(String.valueOf(craftingItem.count));
        } else {
            holder.tvCounter.setVisibility(View.GONE);
        }

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
        private final TextView tvLetter;
        private final TextView tvCounter;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_symbol);
            cardView = view.findViewById(R.id.filter_key_card);
            tvLabel = view.findViewById(R.id.tv_symbol_label);
            tvLetter = view.findViewById(R.id.tv_letter);
            tvCounter = view.findViewById(R.id.tv_counter);
        }

        void applyStatus(MatchStatus status, Object item) {
            if (cardView == null) return;

            Context context = itemView.getContext();

            // 1. Get the opaque base background color
            int baseBgColor = ContextCompat.getColor(context, R.color.keyboard_key_background);

            // 2. Set Defaults (NO outline, standard opaque background)
            int strokeColor = Color.TRANSPARENT;
            int strokeWidthDp = 0;
            int bgColor = baseBgColor;

            if (item instanceof Primitive primitive) {
                // If it has a parent, it is a variant
                boolean isVariant = primitive.getParent() != null;

                if (status == MatchStatus.INCORRECT) {
                    // Red outline if incorrect
                    strokeColor = Color.parseColor("#F44336");
                    strokeWidthDp = 2; // 3dp thickness
                    // Additional red inside if is a variant
                    if (isVariant) {
                        bgColor = ColorUtils.blendARGB(baseBgColor, Color.parseColor("#F44336"), 0.2f);
                    }

                } else if (status == MatchStatus.PARTIAL) {
                    // Yellow inside if incorrect variant
                    if (isVariant) {
                        bgColor = ColorUtils.blendARGB(baseBgColor, Color.parseColor("#FFEB3B"), 0.2f);
                    }

                } else if (status == MatchStatus.EXACT) {
                    if (isVariant) {
                        // Green inside if it's the correct variant
                        bgColor = ColorUtils.blendARGB(baseBgColor, Color.parseColor("#4CAF50"), 0.2f);
                    }
                    // Base primitives remain default inside, no outline for either
                }

            } else if (item instanceof Symbol) {
                if (status == MatchStatus.EXACT) {
                    // Target: green inside + outline
                    bgColor = ColorUtils.blendARGB(baseBgColor, Color.parseColor("#4CAF50"), 0.2f);
                    strokeColor = Color.parseColor("#4CAF50");
                    strokeWidthDp = 2;

                } else if (status == MatchStatus.PARTIAL) {
                    // Component: blue inside + outline
                    bgColor = ColorUtils.blendARGB(baseBgColor, Color.parseColor("#2196F3"), 0.15f);
                    strokeColor = Color.parseColor("#2196F3");
                    strokeWidthDp = 2;
                }
            }

            // Convert DP to strictly enforced Pixels
            int strokeWidthPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    strokeWidthDp,
                    context.getResources().getDisplayMetrics()
            );

            // Apply the properties using ColorStateList to force the MaterialCardView to update
            cardView.setStrokeColor(android.content.res.ColorStateList.valueOf(strokeColor));
            cardView.setStrokeWidth(strokeWidthPx);
            cardView.setCardBackgroundColor(bgColor);
        }

        void resetView() {
            if (imageView != null) {
                imageView.setImageDrawable(null);
                imageView.setTag(null);
                imageView.setVisibility(View.VISIBLE);
            }
            if (tvLetter != null) {
                tvLetter.setVisibility(View.GONE);
            }
            if (tvCounter != null) {
                tvCounter.setVisibility(View.GONE);
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