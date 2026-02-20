package pl.polsl.blissapp.ui.views.keyboard;

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
public class BlissKeyboardFragment extends Fragment
{
    private BlissKeyboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(BlissKeyboardViewModel.class);

        // Set the click callbacks for the buttons, to invoke setRadical/setControl
        // on the viewModel, to inform it about a key being tapped.

        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
