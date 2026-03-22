package pl.polsl.blissapp.ui.views.alchemy;

import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

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

    private final List<Object> items = new ArrayList<>();
    private final SymbolRepository symbolRepository;
    private final int layoutResId;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    public AlchemyAdapter(SymbolRepository symbolRepository, int layoutResId, OnItemClickListener listener) {
        this.symbolRepository = symbolRepository;
        this.layoutResId = layoutResId;
        this.listener = listener;
    }

    public void update(List<?> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = items.get(position);
        holder.clearImage();
        
        if (item instanceof Symbol symbol) {
            holder.loadSymbol(symbol, symbolRepository);
        } else if (item instanceof Primitive primitive) {
            holder.loadPrimitive(primitive);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_symbol);
        }

        void clearImage() {
            if (imageView != null) {
                imageView.setImageDrawable(null);
                imageView.setTag(null);
            }
        }

        void loadPrimitive(Primitive primitive) {
            if (imageView != null) {
                imageView.setImageResource(DrawableMapper.getDrawableRes(primitive));
            }
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
                            if (Objects.equals(imageView.getTag(), symbol.index())) {
                                imageView.setImageDrawable(drawable);
                            }
                        });
                    } catch (SVGParseException ignored) {}
                }
                @Override
                public void onFailure(Exception data) {}
            });
        }
    }
}
