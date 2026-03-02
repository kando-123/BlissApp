package pl.polsl.blissapp.ui.views.blisswriter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

@HiltViewModel
public class BlissWriterViewModel extends ViewModel
{
    private final SymbolRepository symbolRepository;
    private final MutableLiveData<List<Symbol>> message = new MutableLiveData<>();
    private final MutableLiveData<List<Symbol>> hints = new MutableLiveData<>();
    private final MutableLiveData<SearchFilter> filter = new MutableLiveData<>();
    private final MutableLiveData<Exception> failure = new MutableLiveData<>();

    private static final int MAX_HINT_COUNT = 20; // Change if more or less is needed

    @Inject
    public BlissWriterViewModel(SymbolRepository symbolRepository)
    {
        this.symbolRepository = symbolRepository;

        // Initially, the message contains an element, but the element is empty (no symbol).
        // Graphically, it is an empty tile.
        List<Symbol> list = new ArrayList<>();
        list.add(null);
        message.setValue(list);
        filter.setValue(new SearchFilter());
    }

    public LiveData<List<Symbol>> getMessage()
    {
        return message;
    }

    public LiveData<List<Symbol>> getHints()
    {
        return hints;
    }

    public LiveData<SearchFilter> getFilter()
    {
        return filter;
    }

    public LiveData<Exception> getFailure()
    {
        return failure;
    }

    public void putRadical(Primitive primitive)
    {
        SearchFilter value = filter.getValue();
        assert value != null;
        value.addPrimitive(primitive);
        filter.setValue(value);
        updateHints();
    }

    public void removeRadical(Primitive primitive)
    {
        SearchFilter value = filter.getValue();
        assert value != null;
        value.removePrimitive(primitive);
        filter.setValue(value);
        updateHints();
    }

    public void selectHint(Symbol symbol)
    {
        List<Symbol> list = message.getValue();
        assert list != null;
        // The list is always non-empty, because when it does not end
        // with an actual symbol, it ends with a null.
        assert !list.isEmpty();

        /* Substitute the last element (null or an actual symbol) with the new one. */
        list.set(list.size() - 1, symbol);
        message.setValue(list);

        // Clear the filter
        filter.setValue(new SearchFilter());
        updateHints();
    }

    private void updateHints()
    {
        List<Symbol> symbols = message.getValue();
        assert symbols != null;
        Symbol symbol = symbols.get(symbols.size() - 1);

        SearchFilter sf = filter.getValue();
        assert sf != null;

        List<Primitive> primitives = sf.getPrimitives();
        assert primitives != null;

        var callback = new Callback<List<Symbol>, Exception>()
        {
            @Override
            public void onSuccess(List<Symbol> data)
            {
                hints.setValue(data);
            }

            @Override
            public void onFailure(Exception data)
            {
                hints.setValue(Collections.emptyList());
                failure.setValue(data);
            }
        };
        if (symbolRepository != null) {
            symbolRepository.getMatchingSymbols(symbol, primitives, MAX_HINT_COUNT, callback);
        }
    }

    public void popSymbol()
    {
        List<Symbol> list = message.getValue();
        assert list != null;
        if (!list.isEmpty())
        {
            list.remove(list.size() - 1);
        }
        if (list.isEmpty())
        {
            list.add(null);
        }
        message.setValue(list);
        updateHints();
    }

    public void confirmSymbol()
    {
        List<Symbol> list = message.getValue();
        assert list != null;
        if (list.get(list.size() - 1) != null)
        {
            list.add(null);
            hints.setValue(Collections.emptyList());
            filter.setValue(new SearchFilter());
            message.setValue(list);
        }
    }
}
