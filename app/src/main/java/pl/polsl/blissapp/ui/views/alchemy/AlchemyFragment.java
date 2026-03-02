package pl.polsl.blissapp.ui.views.alchemy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlchemyFragment extends Fragment
{
    private AlchemyViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AlchemyViewModel.class);

        viewModel.getCraftingTable().observe(this, craftingTable ->
        {
            /* Render the crafting table. */
        });

        viewModel.getResultingSymbol().observe(this, resultingSymbol ->
        {
            /* Render the resulting symbol. */
        });

        viewModel.getConstructedSymbols().observe(this, constructedSymbols ->
        {
            /* Render the constructed symbols. */
        });
    }
}
