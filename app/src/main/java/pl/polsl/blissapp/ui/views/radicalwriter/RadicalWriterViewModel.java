package pl.polsl.blissapp.ui.views.radicalwriter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.common.Radical;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

@HiltViewModel
public class RadicalWriterViewModel extends ViewModel
{
    private final SymbolRepository symbolRepository;
    private final MutableLiveData<List<Symbol>> message = new MutableLiveData<>();
    private final MutableLiveData<List<Symbol>> hints = new MutableLiveData<>();
    private final MutableLiveData<List<Radical>> filters = new MutableLiveData<>();
    private final MutableLiveData<Exception> failure = new MutableLiveData<>();

    private static final int MAX_HINT_COUNT = 20; // Change if more or less is needed

    @Inject
    public RadicalWriterViewModel(SymbolRepository symbolRepository)
    {
        this.symbolRepository = symbolRepository;

        // Initially, the message contains an element, but the element is empty (no symbol).
        // Graphically, it is an empty tile.
        List<Symbol> list = new ArrayList<>();
        list.add(null);
        message.setValue(list);
    }

    LiveData<List<Symbol>> getMessage()
    {
        return message;
    }

    LiveData<List<Symbol>> getHints()
    {
        return hints;
    }

    LiveData<List<Radical>> getFilters()
    {
        return filters;
    }

    LiveData<Exception> getFailure()
    {
        return failure;
    }

    void putRadical(Radical radical)
    {
        List<Radical> radicals = filters.getValue();
        assert radicals != null;
        radicals.add(radical);
        filters.setValue(radicals);
        updateHints();
    }

    void removeRadical(Radical radical)
    {
        List<Radical> radicals = filters.getValue();
        assert radicals != null;
        radicals.remove(radical);
        filters.setValue(radicals);
        updateHints();
    }

    void selectHint(Symbol symbol)
    {
        List<Symbol> list = message.getValue();
        assert list != null;
        // The list is always non-empty, because when it does not end
        // with an actual symbol, it ends with a null.
        assert !list.isEmpty();

        /* Substitute the last element (null or an actual symbol) with the new one. */
        list.set(list.size() - 1, symbol);
        message.setValue(list);

        filters.setValue(Collections.emptyList());
        updateHints();
    }

    private void updateHints()
    {
        List<Symbol> symbols = message.getValue();
        assert symbols != null;
        Symbol symbol = symbols.getLast();

        List<Radical> radicals = filters.getValue();
        assert radicals != null;

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

        symbolRepository.getMatchingSymbols(symbol, radicals, MAX_HINT_COUNT, callback);
    }

    void popSymbol()
    {
        List<Symbol> list = message.getValue();
        assert list != null;
        if (!list.isEmpty())
        {
            list.removeLast();
        }
        if (list.isEmpty())
        {
            list.add(null);
        }
        message.setValue(list);
        updateHints();
    }

    void confirmSymbol()
    {
        List<Symbol> list = message.getValue();
        assert list != null;
        if (list.getLast() != null)
        {
            list.add(null);
            hints.setValue(Collections.emptyList());
            filters.setValue(Collections.emptyList());
            message.setValue(list);
        }
    }
}
