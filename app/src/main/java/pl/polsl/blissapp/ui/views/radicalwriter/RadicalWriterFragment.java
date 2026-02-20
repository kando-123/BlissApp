package pl.polsl.blissapp.ui.views.radicalwriter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.common.Radical;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.views.keyboard.BlissKeyboardViewModel;
import pl.polsl.blissapp.ui.views.keyboard.ControlKey;

@AndroidEntryPoint
public class RadicalWriterFragment extends Fragment
{
    private BlissKeyboardViewModel keyboardViewModel;
    private RadicalWriterViewModel writerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        keyboardViewModel = new ViewModelProvider(this).get(BlissKeyboardViewModel.class);
        writerViewModel = new ViewModelProvider(this).get(RadicalWriterViewModel.class);

        /* Pass the writer VM the information that a radical was input from the keyboard. */
        LiveData<Radical> kbdRadicalInput = keyboardViewModel.getRadical();
        kbdRadicalInput.observe(getViewLifecycleOwner(), writerViewModel::putRadical);

        /* Pass the writer VM the information that a control key was input from the keyboard. */
        LiveData<ControlKey> kbdControlInput = keyboardViewModel.getControl();
        kbdControlInput.observe(getViewLifecycleOwner(), controlKey ->
        {
            switch (controlKey)
            {
                case POP_SYMBOL -> writerViewModel.popSymbol();
                case PUSH_SYMBOL -> writerViewModel.confirmSymbol();
            }
        });

        /* Set the callbacks to changes in the writer VM. */

        LiveData<List<Symbol>> message = writerViewModel.getMessage();
        message.observe(getViewLifecycleOwner(), symbols ->
        {
            /* Render the message. */
        });

        LiveData<List<Symbol>> hints = writerViewModel.getHints();
        hints.observe(getViewLifecycleOwner(), symbols ->
        {
            /* Render the hints. */
        });

        LiveData<List<Radical>> filters = writerViewModel.getFilters();
        filters.observe(getViewLifecycleOwner(), radicals ->
        {
            /* Render the filters. */
        });

        LiveData<Exception> failure = writerViewModel.getFailure();
        failure.observe(getViewLifecycleOwner(), exception ->
        {
            /* Render the failure, e.g. use a toast. */
        });
    }
}
