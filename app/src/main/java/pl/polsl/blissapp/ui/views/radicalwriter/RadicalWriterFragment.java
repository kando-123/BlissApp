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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.views.keyboard.BlissKeyboardViewModel;
import pl.polsl.blissapp.ui.views.keyboard.ControlKey;

@AndroidEntryPoint
public class RadicalWriterFragment extends Fragment {

    private BlissKeyboardViewModel keyboardViewModel;
    private RadicalWriterViewModel writerViewModel;
    private FilterAdapter filterAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_radical_writer, container, false);
        // return inflater.inflate(R.layout.hello_world, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        keyboardViewModel = new ViewModelProvider(this).get(BlissKeyboardViewModel.class);
        writerViewModel = new ViewModelProvider(this).get(RadicalWriterViewModel.class);

        setupFilterView(view);

        /* Pass the writer VM the information that a radical was input from the keyboard. */
        LiveData<Radical> kbdRadicalInput = keyboardViewModel.getRadicalInput();
        kbdRadicalInput.observe(getViewLifecycleOwner(), writerViewModel::putRadical);

        /* Pass the writer VM the information that an indicator was input from the keyboard. */
        LiveData<Indicator> kbdIndicatorInput = keyboardViewModel.getIndicatorInput();
        kbdIndicatorInput.observe(getViewLifecycleOwner(), writerViewModel::putIndicator);

        /* Pass the writer VM the information that a control key was input from the keyboard. */
        LiveData<ControlKey> kbdControlInput = keyboardViewModel.getControlInput();
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

        LiveData<SearchFilter> filter = writerViewModel.getFilter();
        filter.observe(getViewLifecycleOwner(), filterAdapter::update);

        LiveData<Exception> failure = writerViewModel.getFailure();
        failure.observe(getViewLifecycleOwner(), exception ->
        {
            /* Render the failure, e.g. use a toast. */
        });
    }

    private void setupFilterView(View root)
    {
        RecyclerView filterView = root.findViewById(R.id.rv_filters);
        filterAdapter = new FilterAdapter(writerViewModel);
        filterView.setAdapter(filterAdapter);
        filterView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }
}
