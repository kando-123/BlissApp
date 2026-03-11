package pl.polsl.blissapp.ui.views.blisswriter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.ui.mapping.DrawableMapper;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private final List<CountedItem> mItems;
    private final BlissWriterViewModel mViewModel;

    public FilterAdapter(BlissWriterViewModel viewModel)
    {
        mViewModel = viewModel;
        mItems = new ArrayList<>();
    }

    public void update(List<Primitive> filter)
    {
        mItems.clear();
        if (filter != null)
        {
            mItems.addAll(countItems(filter));
        }

        notifyDataSetChanged();
    }

    private List<CountedItem> countItems(List<Primitive> list)
    {
        Map<Primitive, Integer> counts = new LinkedHashMap<>();
        for (Primitive p : list) {
            counts.put(p, counts.getOrDefault(p, 0) + 1);
        }

        List<CountedItem> result = new ArrayList<>();
        for (Map.Entry<Primitive, Integer> entry : counts.entrySet()) {
            result.add(new CountedItem(entry.getKey(), entry.getValue()));
        }
        return result;
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
        CountedItem countedItem = mItems.get(position);
        Primitive primitive = countedItem.item;

        String letterLabel = primitive.getLetterLabel();
        if (letterLabel != null) {
            holder.imageView.setVisibility(View.GONE);
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(letterLabel);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textView.setVisibility(View.GONE);
            holder.imageView.setImageResource(DrawableMapper.getDrawableRes(primitive));
        }

        if (countedItem.count > 1)
        {
            String countText = String.valueOf(countedItem.count);
            holder.counterView.setText(countText);
            holder.counterView.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.counterView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            mViewModel.removePrimitive(primitive);
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
        TextView textView;
        TextView counterView;

        ViewHolder(View view)
        {
            super(view);
            imageView = view.findViewById(R.id.filter_key_image);
            textView = view.findViewById(R.id.filter_key_text);
            counterView = view.findViewById(R.id.filter_key_counter);
        }
    }

    static class CountedItem
    {
        Primitive item;
        int count;

        CountedItem(Primitive item, int count) {
            this.item = item;
            this.count = count;
        }
    }
}
