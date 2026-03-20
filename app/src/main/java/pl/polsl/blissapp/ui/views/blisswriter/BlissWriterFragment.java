package pl.polsl.blissapp.ui.views.blisswriter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import pl.polsl.blissapp.R;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;
import pl.polsl.blissapp.ui.views.keyboard.BlissKeyboardViewModel;
import pl.polsl.blissapp.ui.views.keyboard.ControlKey;

@AndroidEntryPoint
public class BlissWriterFragment extends Fragment
{
    private BlissKeyboardViewModel mKeyboardViewModel;
    private BlissWriterViewModel mWriterViewModel;
    private FilterAdapter mFilterAdapter;
    private SymbolAdapter mHintsAdapter;
    private SymbolAdapter mMessageAdapter;

    @Inject
    SymbolRepository mSymbolRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_bliss_writer, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mKeyboardViewModel = new ViewModelProvider(this).get(BlissKeyboardViewModel.class);
        mWriterViewModel = new ViewModelProvider(this).get(BlissWriterViewModel.class);

        setupFilterView(view);
        setupHintsView(view);
        setupMessageView(view);

        mKeyboardViewModel.clearInputs();

        /* Pass the writer VM the information that a radical was input from the keyboard. */
        LiveData<Primitive> kbdPrimitiveInput = mKeyboardViewModel.getPrimitiveInput();
        kbdPrimitiveInput.observe(getViewLifecycleOwner(), mWriterViewModel::putPrimitive);

        /* Pass the writer VM the information that a control key was input from the keyboard. */
        LiveData<ControlKey> kbdControlInput = mKeyboardViewModel.getControlInput();
        kbdControlInput.observe(getViewLifecycleOwner(), controlKey ->
        {
            if (controlKey == null) { return; }
            switch (controlKey)
            {
                case POP_SYMBOL -> mWriterViewModel.popSymbol();
                case PUSH_SYMBOL -> mWriterViewModel.confirmSymbol();
            }
        });

        /* Set the callbacks to changes in the writer VM. */

        LiveData<List<BlissWriterViewModel.MessageItem>> message = mWriterViewModel.getMessage();
        message.observe(getViewLifecycleOwner(), items ->
        {
            List<Symbol> symbols = items.stream()
                    .filter(item -> item instanceof BlissWriterViewModel.MessageItem.SymbolItem)
                    .map(item -> ((BlissWriterViewModel.MessageItem.SymbolItem) item).symbol)
                    .collect(Collectors.toList());
            mMessageAdapter.update(symbols);
        });

        LiveData<List<Symbol>> hints = mWriterViewModel.getHints();
        hints.observe(getViewLifecycleOwner(), symbols ->
        {
            mHintsAdapter.update(symbols);
        });

        LiveData<Map<Primitive, Integer>> filter = mWriterViewModel.getFilter();
        filter.observe(getViewLifecycleOwner(), mFilterAdapter::update);

        LiveData<Exception> failure = mWriterViewModel.getFailure();
        failure.observe(getViewLifecycleOwner(), exception ->
        {
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupFilterView(View root)
    {
        RecyclerView filterView = root.findViewById(R.id.rv_filters);
        mFilterAdapter = new FilterAdapter(mWriterViewModel);
        filterView.setAdapter(mFilterAdapter);
        filterView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupHintsView(View root)
    {
        RecyclerView hintsView = root.findViewById(R.id.rv_hints);
        mHintsAdapter = new SymbolAdapter(mSymbolRepository, R.layout.item_bliss_symbol, symbol -> mWriterViewModel.selectHint(symbol));
        hintsView.setAdapter(mHintsAdapter);
        hintsView.setLayoutManager(new GridLayoutManager(getContext(), 4));
    }

    private void setupMessageView(View root)
    {
        RecyclerView messageView = root.findViewById(R.id.rv_message);
        mMessageAdapter = new SymbolAdapter(mSymbolRepository, R.layout.item_bliss_message_symbol, null);
        messageView.setAdapter(mMessageAdapter);
        messageView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }
}
