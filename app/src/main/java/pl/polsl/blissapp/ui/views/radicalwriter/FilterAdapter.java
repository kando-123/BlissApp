package pl.polsl.blissapp.ui.views.radicalwriter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;
import pl.polsl.blissapp.ui.mapping.DrawableMapper;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private final List<Object> items = new ArrayList<>();
    private final RadicalWriterViewModel viewModel;

    public FilterAdapter(RadicalWriterViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void update(SearchFilter filter) {
        items.clear();
        if (filter != null) {
            items.addAll(countItems(filter.getRadicals()));
            items.addAll(countItems(filter.getIndicators()));
        }
        notifyDataSetChanged();
    }

    private List<CountedItem> countItems(List<?> list) {
        return list.stream()
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new CountedItem(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter_key, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CountedItem countedItem = (CountedItem) items.get(position);
        Object item = countedItem.item;

        if (item instanceof Radical) {
            holder.imageView.setImageResource(DrawableMapper.getDrawableRes((Radical) item));
        } else if (item instanceof Indicator) {
            holder.imageView.setImageResource(DrawableMapper.getDrawableRes((Indicator) item));
        }

        if (countedItem.count > 1) {
            holder.counterView.setText("x" + countedItem.count);
            holder.counterView.setVisibility(View.VISIBLE);
        } else {
            holder.counterView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (item instanceof Radical) {
                viewModel.removeRadical((Radical) item);
            } else if (item instanceof Indicator) {
                viewModel.removeIndicator((Indicator) item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView counterView;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.filter_key_image);
            counterView = view.findViewById(R.id.filter_key_counter);
        }
    }

    static class CountedItem {
        Object item;
        int count;

        CountedItem(Object item, int count) {
            this.item = item;
            this.count = count;
        }
    }
}
