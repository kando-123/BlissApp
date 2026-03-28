package pl.polsl.blissapp.ui.views.alchemy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.PictureDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.ui.repository.SymbolRepository;


public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder> {

    private final List<DiscoveryJournalViewModel.JournalItem> items = new ArrayList<>();
    private final SymbolRepository symbolRepository;

    public JournalAdapter(SymbolRepository symbolRepository) {
        this.symbolRepository = symbolRepository;
    }

    public void setItems(List<DiscoveryJournalViewModel.JournalItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journal_symbol, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiscoveryJournalViewModel.JournalItem item = items.get(position);
        holder.tvLabel.setText(item.label);

        Context context = holder.itemView.getContext();
        int tintColor = getThemeColor(context, android.R.attr.textColorPrimary);

        holder.imageView.setTag(item.symbol.index());
        holder.imageView.setImageDrawable(null); // Clear previous

        symbolRepository.getSvg(item.symbol, new Callback<String, Exception>() {
            @Override
            public void onSuccess(String svgString) {
                if (!Objects.equals(holder.imageView.getTag(), item.symbol.index())) return;
                try {
                    SVG svg = SVG.getFromString(svgString);
                    PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                    holder.imageView.post(() -> {
                        if (Objects.equals(holder.imageView.getTag(), item.symbol.index())) {
                            holder.imageView.setImageDrawable(drawable);
                            holder.imageView.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP);
                        }
                    });
                } catch (SVGParseException ignored) {}
            }

            @Override
            public void onFailure(Exception data) {}
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int getThemeColor(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attr, typedValue, true)) {
            return typedValue.data;
        }
        return Color.BLACK;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView tvLabel;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_journal_symbol);
            tvLabel = view.findViewById(R.id.tv_journal_label);
        }
    }
}
