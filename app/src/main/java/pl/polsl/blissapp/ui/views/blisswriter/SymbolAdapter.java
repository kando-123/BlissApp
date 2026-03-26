package pl.polsl.blissapp.ui.views.blisswriter;

import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

/**
 * RecyclerView adapter that can display either symbol items (with SVG) or empty slot items.
 */
public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.ViewHolder> {

    private static final int VIEW_TYPE_SYMBOL = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    private final List<Object> items = new ArrayList<>();
    private final SymbolRepository symbolRepository;
    private final OnItemClickListener listener;
    private final int symbolLayoutResId;
    private final Integer emptyLayoutResId;

    // 1. Interface to notify when an SVG has finished rendering and resizing
    public interface OnImageRenderedListener {
        void onImageRendered(int position);
    }

    private OnImageRenderedListener imageRenderedListener;

    public interface OnItemClickListener {
        void onItemClick(int position, Object item);
    }

    public SymbolAdapter(SymbolRepository symbolRepository, int symbolLayoutResId,
                         OnItemClickListener listener) {
        this(symbolRepository, symbolLayoutResId, null, listener);
    }

    public SymbolAdapter(SymbolRepository symbolRepository,
                         int symbolLayoutResId,
                         @Nullable Integer emptyLayoutResId,
                         OnItemClickListener listener) {
        this.symbolRepository = symbolRepository;
        this.symbolLayoutResId = symbolLayoutResId;
        this.emptyLayoutResId = emptyLayoutResId;
        this.listener = listener;
    }

    // 2. Setter for the render listener
    public void setOnImageRenderedListener(OnImageRenderedListener listener) {
        this.imageRenderedListener = listener;
    }

    // --- Nowe metody pomocnicze do obsługi kliknięcia ostatniego elementu ---
    /**
     * Zwraca ostatni element w liście lub null, jeśli lista jest pusta.
     */
    @Nullable
    public Object getLastItem() {
        if (items.isEmpty()) return null;
        return items.get(items.size() - 1);
    }

    /**
     * Wywołuje listener kliknięcia dla ostatniego elementu, jeśli istnieje.
     * Można tego użyć, gdy kliknięcie nastąpiło w pusty obszar RecyclerView.
     */
    public void triggerLastItemClick() {
        if (listener != null && !items.isEmpty()) {
            int lastPos = items.size() - 1;
            listener.onItemClick(lastPos, items.get(lastPos));
        }
    }
    // ----------------------------------------------------------------

    public void update(List<?> newItems) {
        List<Object> oldItems = new ArrayList<>(items);
        List<Object> finalNewItems = newItems != null ? new ArrayList<>(newItems) : new ArrayList<>();

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return oldItems.size(); }
            @Override
            public int getNewListSize() { return finalNewItems.size(); }

            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                Object o1 = oldItems.get(oldPos);
                Object o2 = finalNewItems.get(newPos);

                if (o1 instanceof Symbol s1 && o2 instanceof Symbol s2) {
                    return s1.index() == s2.index();
                }

                if (o1 instanceof BlissWriterViewModel.MessageItem.SymbolItem s1 &&
                        o2 instanceof BlissWriterViewModel.MessageItem.SymbolItem s2) {
                    return s1 == s2;
                }

                if (o1 instanceof BlissWriterViewModel.MessageItem.EmptySlot &&
                        o2 instanceof BlissWriterViewModel.MessageItem.EmptySlot) {
                    return true;
                }

                return Objects.equals(o1, o2);
            }

            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                return Objects.equals(oldItems.get(oldPos), finalNewItems.get(newPos));
            }
        });

        items.clear();
        items.addAll(finalNewItems);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemViewType(int position) {
        if (emptyLayoutResId != null &&
                items.get(position) instanceof BlissWriterViewModel.MessageItem.EmptySlot) {
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_SYMBOL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (viewType == VIEW_TYPE_EMPTY) ? emptyLayoutResId : symbolLayoutResId;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        // 3. Pass the listener into the ViewHolder
        return new ViewHolder(view, imageRenderedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = items.get(position);
        Symbol symbol = null;
        if (item instanceof Symbol s) {
            symbol = s;
        } else if (item instanceof BlissWriterViewModel.MessageItem.SymbolItem s) {
            symbol = s.symbol;
        }

        holder.clearImage();

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(holder.getBindingAdapterPosition(), item);
            }
        });

        if (symbol != null) {
            holder.loadSymbol(symbol, symbolRepository);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clearImage();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final OnImageRenderedListener renderListener;

        ViewHolder(View view, OnImageRenderedListener renderListener) {
            super(view);
            imageView = view.findViewById(R.id.img_symbol);
            this.renderListener = renderListener;
        }

        void clearImage() {
            if (imageView != null) {
                imageView.setImageDrawable(null);
                imageView.setTag(null);
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

                                // 4. Post once more to allow the view to measure its new dynamic width
                                imageView.post(() -> {
                                    int pos = getBindingAdapterPosition();
                                    if (renderListener != null && pos != RecyclerView.NO_POSITION) {
                                        renderListener.onImageRendered(pos);
                                    }
                                });
                            }
                        });
                    } catch (SVGParseException ignored) {
                        // fallback: keep image cleared
                    }
                }

                @Override
                public void onFailure(Exception data) {
                    // On error, leave the image cleared
                }
            });
        }
    }
}