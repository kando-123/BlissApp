package pl.polsl.blissapp.ui.views.blisswriter;

import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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

public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.ViewHolder> {

    private final List<Symbol> mSymbols = new ArrayList<>();
    private final SymbolRepository mSymbolRepository;
    private final OnSymbolClickListener mListener;
    private final int mLayoutResId;

    public interface OnSymbolClickListener {
        void onSymbolClick(Symbol symbol);
    }

    public SymbolAdapter(SymbolRepository symbolRepository, int layoutResId, OnSymbolClickListener listener) {
        mSymbolRepository = symbolRepository;
        mLayoutResId = layoutResId;
        mListener = listener;
    }

    public void update(List<Symbol> newSymbols) {
        if (newSymbols == null) {
            newSymbols = new ArrayList<>();
        }

        final List<Symbol> oldSymbols = new ArrayList<>(mSymbols);
        final List<Symbol> finalNewSymbols = newSymbols;

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldSymbols.size();
            }

            @Override
            public int getNewListSize() {
                return finalNewSymbols.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldSymbols.get(oldItemPosition).index() == finalNewSymbols.get(newItemPosition).index();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(oldSymbols.get(oldItemPosition), finalNewSymbols.get(newItemPosition));
            }
        });

        mSymbols.clear();
        mSymbols.addAll(newSymbols);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Symbol symbol = mSymbols.get(position);
        holder.bind(symbol, mSymbolRepository);
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSymbolClick(symbol);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSymbols.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImageView;

        ViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.img_symbol);
        }

        void bind(Symbol symbol, SymbolRepository repository) {
            // Check if we are already displaying this symbol to avoid flickering
            if (Objects.equals(mImageView.getTag(), symbol.index())) {
                return;
            }

            mImageView.setTag(symbol.index());
            mImageView.setImageDrawable(null); // Clear previous image while loading

            repository.getSvg(symbol, new Callback<String, Exception>() {
                @Override
                public void onSuccess(String svgString) {
                    // Verify that the view is still intended for this symbol
                    if (!Objects.equals(mImageView.getTag(), symbol.index())) {
                        return;
                    }

                    try {
                        SVG svg = SVG.getFromString(svgString);
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                        mImageView.post(() -> {
                            // Final check before setting drawable
                            if (Objects.equals(mImageView.getTag(), symbol.index())) {
                                mImageView.setImageDrawable(drawable);
                            }
                        });
                    } catch (SVGParseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Exception data) {
                    if (Objects.equals(mImageView.getTag(), symbol.index())) {
                        // Optional: set error icon
                    }
                }
            });
        }
    }
}
