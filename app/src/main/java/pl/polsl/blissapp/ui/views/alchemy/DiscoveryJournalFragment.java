package pl.polsl.blissapp.ui.views.alchemy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.R;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

@AndroidEntryPoint
public class DiscoveryJournalFragment extends Fragment {

    private JournalAdapter adapter;
    private TextView tvSymbolCount;
    private TextView tvPageInfo;
    private Button btnPrevious;
    private Button btnNext;
    private ProgressBar progressBar;
    private DiscoveryJournalViewModel viewModel;

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
        tvSymbolCount = view.findViewById(R.id.tv_symbol_count);
        progressBar = view.findViewById(R.id.progress_bar);
        tvPageInfo = view.findViewById(R.id.tv_page_info);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnNext = view.findViewById(R.id.btn_next);

        viewModel = new ViewModelProvider(this).get(DiscoveryJournalViewModel.class);

        rvJournal.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new JournalAdapter(symbolRepository);
        rvJournal.setAdapter(adapter);

        // Observe journal items
        viewModel.getJournalItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
            updateEmptyState(items);
            updateCounter(items.size());
            updateNavigationButtons();
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            updateNavigationButtons();
        });

        // Observe total discovered count
        viewModel.getTotalDiscovered().observe(getViewLifecycleOwner(), total -> {
            List<DiscoveryJournalViewModel.JournalItem> items = viewModel.getJournalItems().getValue();
            updateCounter(items != null ? items.size() : 0);
            updateNavigationButtons();
        });

        // Observe current page and total pages to update page info
        viewModel.getCurrentPage().observe(getViewLifecycleOwner(), page -> {
            updatePageInfo();
            updateNavigationButtons();
        });

        viewModel.getTotalPages().observe(getViewLifecycleOwner(), totalPages -> {
            updatePageInfo();
            updateNavigationButtons();
        });

        // Button click listeners
        btnPrevious.setOnClickListener(v -> viewModel.previousPage());
        btnNext.setOnClickListener(v -> viewModel.nextPage());
    }

    private void updatePageInfo() {
        Integer page = viewModel.getCurrentPage().getValue();
        Integer total = viewModel.getTotalPages().getValue();
        if (total != null && page != null) {
            tvPageInfo.setText(getString(R.string.page_info, page + 1, total));
        }
    }

    private void updateNavigationButtons() {
        boolean isLoading = Boolean.TRUE.equals(viewModel.getIsLoading().getValue());
        Integer page = viewModel.getCurrentPage().getValue();
        Integer total = viewModel.getTotalPages().getValue();

        if (isLoading || page == null || total == null) {
            btnPrevious.setEnabled(false);
            btnNext.setEnabled(false);
        } else {
            btnPrevious.setEnabled(page > 0);
            btnNext.setEnabled(page < total - 1);
        }
    }

    private void updateEmptyState(List<DiscoveryJournalViewModel.JournalItem> items) {
        View view = getView();
        if (view == null) return;
        
        View rvJournal = view.findViewById(R.id.rv_journal);
        TextView tvEmptyState = view.findViewById(R.id.tv_empty_state);
        View bottomBar = view.findViewById(R.id.bottom_bar);
        
        if (items.isEmpty()) {
            boolean isLoading = Boolean.TRUE.equals(viewModel.getIsLoading().getValue());
            if (isLoading) {
                // Keep things as they are while loading to avoid flickering
                return;
            }
            tvEmptyState.setVisibility(View.VISIBLE);
            rvJournal.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvJournal.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }
    }

    private void updateCounter(int loadedCount) {
        Integer total = viewModel.getTotalDiscovered().getValue();
        if (total != null && total > 0) {
            tvSymbolCount.setText(getString(R.string.discovered_symbols_count, total));
            tvSymbolCount.setVisibility(View.VISIBLE);
        } else if (loadedCount > 0) {
            tvSymbolCount.setText(getString(R.string.discovered_symbols_count, loadedCount));
            tvSymbolCount.setVisibility(View.VISIBLE);
        } else {
            tvSymbolCount.setVisibility(View.GONE);
        }
    }
}
