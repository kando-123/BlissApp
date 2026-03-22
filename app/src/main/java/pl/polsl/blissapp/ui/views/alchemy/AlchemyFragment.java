package pl.polsl.blissapp.ui.views.alchemy;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;
import pl.polsl.blissapp.ui.views.keyboard.BlissKeyboardViewModel;

import javax.inject.Inject;

@AndroidEntryPoint
public class AlchemyFragment extends Fragment
{
    private AlchemyViewModel viewModel;
    private BlissKeyboardViewModel keyboardViewModel;
    
    private RecyclerView rvConstructedSymbols;
    private RecyclerView rvCraftingTable;
    private FrameLayout compositionArea;
    private TextView tvCheering;
    
    private AlchemyAdapter libraryAdapter;
    private AlchemyAdapter craftingAdapter;

    @Inject
    SymbolRepository symbolRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alchemy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        rvConstructedSymbols = view.findViewById(R.id.rv_constructed_symbols);
        rvCraftingTable = view.findViewById(R.id.rv_crafting_table);
        compositionArea = view.findViewById(R.id.composition_area);
        tvCheering = view.findViewById(R.id.tv_cheering);

        // Scope ViewModels to the Fragment
        viewModel = new ViewModelProvider(this).get(AlchemyViewModel.class);
        keyboardViewModel = new ViewModelProvider(this).get(BlissKeyboardViewModel.class);

        setupRecyclerViews();
        observeViewModel();
        setupKeyboardInteractions();
    }

    private void setupRecyclerViews() {
        // Library: use item_bliss_symbol for grid
        libraryAdapter = new AlchemyAdapter(symbolRepository, R.layout.item_bliss_symbol, item -> {
            // Symbols in library are already found
        });
        rvConstructedSymbols.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvConstructedSymbols.setAdapter(libraryAdapter);
        
        // Crafting Table: use item_alchemy_crafting for horizontal list
        craftingAdapter = new AlchemyAdapter(symbolRepository, R.layout.item_alchemy_crafting, item -> {
            // Optional: click to remove from table
        });
        rvCraftingTable.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCraftingTable.setAdapter(craftingAdapter);
    }

    private void observeViewModel() {
        viewModel.getCraftingTable().observe(getViewLifecycleOwner(), craftingTable -> {
            List<Object> items = craftingTable.getAllItems();
            craftingAdapter.update(items);
            if (!items.isEmpty()) {
                rvCraftingTable.smoothScrollToPosition(items.size() - 1);
            }
        });

        viewModel.getSymbolFolders().observe(getViewLifecycleOwner(), folders -> {
            List<Object> allSymbols = new ArrayList<>();
            for (Map.Entry<String, List<Symbol>> entry : folders.entrySet()) {
                allSymbols.addAll(entry.getValue());
            }
            libraryAdapter.update(allSymbols);
        });

        viewModel.getResultingSymbol().observe(getViewLifecycleOwner(), symbol -> {
            if (symbol != null) {
                playSuccessAnimation();
            }
        });
        
        viewModel.getShowCheering().observe(getViewLifecycleOwner(), show -> {
            tvCheering.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) {
                tvCheering.animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).withEndAction(() -> {
                    tvCheering.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                }).start();
                tvCheering.postDelayed(() -> viewModel.dismissCheering(), 2000);
            }
        });

        viewModel.getIsMatchDiscovered().observe(getViewLifecycleOwner(), isMatch -> {
            if (isMatch) {
                compositionArea.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
            } else {
                compositionArea.setBackgroundResource(R.drawable.writer_surface_background);
            }
        });
    }

    private void setupKeyboardInteractions() {
        keyboardViewModel.getPrimitiveInput().observe(getViewLifecycleOwner(), primitive -> {
            if (primitive != null) {
                viewModel.addRadical(primitive);
                keyboardViewModel.clearInputs();
            }
        });

        keyboardViewModel.getControlInput().observe(getViewLifecycleOwner(), controlKey -> {
            if (controlKey != null) {
                switch (controlKey) {
                    case ENTER:
                        viewModel.onEnterPressed();
                        break;
                    case POP_SYMBOL:
                        viewModel.onPopPressed();
                        break;
                }
                keyboardViewModel.clearInputs();
            }
        });
    }

    private void playSuccessAnimation() {
        rvCraftingTable.animate()
                .scaleX(0.2f)
                .scaleY(0.2f)
                .alpha(0f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    rvCraftingTable.setScaleX(1f);
                    rvCraftingTable.setScaleY(1f);
                    rvCraftingTable.setAlpha(1f);
                })
                .start();
    }
}
