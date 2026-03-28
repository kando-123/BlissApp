package pl.polsl.blissapp.ui.views.alchemy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.R;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

@AndroidEntryPoint
public class DiscoveryJournalFragment extends Fragment {

    private JournalAdapter adapter;
    private TextView tvSymbolCount;

    @Inject
    SymbolRepository symbolRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvJournal = view.findViewById(R.id.rv_journal);
        TextView tvEmptyState = view.findViewById(R.id.tv_empty_state);
        tvSymbolCount = view.findViewById(R.id.tv_symbol_count);

        DiscoveryJournalViewModel viewModel = new ViewModelProvider(this).get(DiscoveryJournalViewModel.class);

        // Use a Grid of 3 columns for the journal
        rvJournal.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new JournalAdapter(symbolRepository);
        rvJournal.setAdapter(adapter);

        viewModel.getJournalItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
            int count = items.size();

            // Update the counter
            if (count > 0) {
                tvSymbolCount.setText(getString(R.string.discovered_symbols_count, count));
                tvSymbolCount.setVisibility(View.VISIBLE);
            } else {
                tvSymbolCount.setVisibility(View.GONE);
            }

            // Show/hide empty state
            if (items.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                rvJournal.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                rvJournal.setVisibility(View.VISIBLE);
            }
        });
    }
}