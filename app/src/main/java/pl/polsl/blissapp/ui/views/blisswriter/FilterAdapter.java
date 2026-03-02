package pl.polsl.blissapp.ui.views.blisswriter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.ui.mapping.DrawableMapper;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private final List<Object> mItems;
    private final BlissWriterViewModel mViewModel;

    public FilterAdapter(BlissWriterViewModel viewModel)
    {
        mViewModel = viewModel;
        mItems = new ArrayList<>();
    }

    public void update(SearchFilter filter)
    {
        mItems.clear();
        if (filter != null)
        {
            mItems.addAll(countItems(filter.getPrimitives()));
        }

        notifyDataSetChanged();
    }

    private List<CountedItem> countItems(List<?> list)
    {
        return list.stream()
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new CountedItem(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter_key, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CountedItem countedItem = (CountedItem) mItems.get(position);
        Object item = countedItem.item;

        if (item instanceof Primitive primitive)
        {
            holder.imageView.setImageResource(DrawableMapper.getDrawableRes(primitive));
        }

        if (countedItem.count > 1)
        {
            String countText = holder.itemView.getContext()
                    .getString(R.string.item_count_format, countedItem.count);
            holder.counterView.setText(countText);
            holder.counterView.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.counterView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (item instanceof Primitive) {
                mViewModel.removeRadical((Primitive) item);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView counterView;

        ViewHolder(View view)
        {
            super(view);
            imageView = view.findViewById(R.id.filter_key_image);
            counterView = view.findViewById(R.id.filter_key_counter);
        }
    }

    static class CountedItem
    {
        Object item;
        int count;

        CountedItem(Object item, int count) {
            this.item = item;
            this.count = count;
        }
    }
}
