package pl.polsl.blissapp.ui.views.natlangwriter;

import android.content.Context;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.List;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Translation;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

public class TranslationAdapter extends ListAdapter<Translation, TranslationAdapter.ViewHolder>
{
    private final SymbolRepository mSymbolRepository;

    public TranslationAdapter(SymbolRepository symbolRepository)
    {
        super(new DiffUtil.ItemCallback<>()
        {
            @Override
            public boolean areItemsTheSame(@NonNull Translation oldItem, @NonNull Translation newItem)
            {
                return oldItem.symbol().index() == newItem.symbol().index();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Translation oldItem, @NonNull Translation newItem)
            {
                return oldItem.equals(newItem);
            }
        });
        this.mSymbolRepository = symbolRepository;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_translation_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView ivSymbol;
        private final TextView tvMeanings;
        private final int colorOnPrimary;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            ivSymbol = itemView.findViewById(R.id.iv_symbol);
            tvMeanings = itemView.findViewById(R.id.tv_meanings);

            Context context = itemView.getContext();
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
            colorOnPrimary = typedValue.data;
        }

        public void bind(Translation translation)
        {
            tvMeanings.setText(TextUtils.join(", ", translation.meanings()));

            mSymbolRepository.getSvg(translation.symbol(), new Callback<String, Exception>()
            {
                @Override
                public void onSuccess(String svgData)
                {
                    ivSymbol.post(() -> {
                        try {
                            SVG svg = SVG.getFromString(svgData);
                            Picture picture = svg.renderToPicture();
                            PictureDrawable drawable = new PictureDrawable(picture);
                            ivSymbol.setImageDrawable(drawable);
                            ivSymbol.setColorFilter(colorOnPrimary);
                        } catch (SVGParseException e) {
                            ivSymbol.setImageResource(R.drawable.ic_launcher_foreground);
                        }
                    });
                }

                @Override
                public void onFailure(Exception data)
                {
                    ivSymbol.post(() -> ivSymbol.setImageResource(R.drawable.ic_launcher_foreground));
                }
            });
        }
    }
}
