package pl.polsl.blissapp.ui.views.blisswriter;

import android.util.Log;

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
    private final SymbolRepository mSymbolRepository;
    private final MutableLiveData<List<Symbol>> mMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Symbol>> mHints = new MutableLiveData<>();
    private final MutableLiveData<SearchFilter> mFilter = new MutableLiveData<>();
    private final MutableLiveData<Exception> mFailure = new MutableLiveData<>();

    private static final int MAX_HINT_COUNT = 20; // Change if more or less is needed

    @Inject
    public BlissWriterViewModel(SymbolRepository symbolRepository)
    {
        mSymbolRepository = symbolRepository;

        // Initially, the message contains an element, but the element is empty (no symbol).
        // Graphically, it is an empty tile.
        List<Symbol> list = new ArrayList<>();
        list.add(null);
        mMessage.setValue(list);
        mFilter.setValue(new SearchFilter());
    }

    LiveData<List<Symbol>> getMessage()
    {
        return mMessage;
    }

    LiveData<List<Symbol>> getHints()
    {
        return mHints;
    }

    LiveData<SearchFilter> getFilter()
    {
        return mFilter;
    }

    LiveData<Exception> getFailure()
    {
        return mFailure;
    }

    public void putPrimitive(Primitive primitive)
    {
        Log.d("BlissWriterViewModel", "Putting primitive: " + primitive.name());

        SearchFilter value = mFilter.getValue();
        assert value != null;
        value.addPrimitive(primitive);
        mFilter.setValue(value);
        updateHints();
    }

    public void removePrimitive(Primitive primitive)
    {
        Log.d("BlissWriterViewModel", "Removing primitive: " + primitive.name());

        SearchFilter value = mFilter.getValue();
        assert value != null;
        value.removePrimitive(primitive);
        mFilter.setValue(value);
        updateHints();
    }

    public void selectHint(Symbol symbol)
    {
        List<Symbol> list = mMessage.getValue();
        assert list != null;
        // The list is always non-empty, because when it does not end
        // with an actual symbol, it ends with a null.
        assert !list.isEmpty();

        /* Substitute the last element (null or an actual symbol) with the new one. */
        list.set(list.size() - 1, symbol);
        mMessage.setValue(list);

        // Clear the filter
        mFilter.setValue(new SearchFilter());
        updateHints();
    }

    private void updateHints()
    {
        Log.d("BlissWriterViewModel", "Updating hints");

        List<Symbol> symbols = mMessage.getValue();
        assert symbols != null;
        Symbol symbol = symbols.get(symbols.size() - 1);

        SearchFilter sf = mFilter.getValue();
        assert sf != null;

        List<Primitive> primitives = sf.getPrimitives();
        assert primitives != null;

        var callback = new Callback<List<Symbol>, Exception>()
        {
            @Override
            public void onSuccess(List<Symbol> data)
            {
                mHints.setValue(data);
            }

            @Override
            public void onFailure(Exception data)
            {
                mHints.setValue(Collections.emptyList());
                mFailure.setValue(data);
            }
        };
        assert mSymbolRepository != null : "SymbolRepository was called but it had not been injected (it was null)!";
        mSymbolRepository.getMatchingSymbols(symbol, primitives, MAX_HINT_COUNT, callback);
    }

    public void popSymbol()
    {
        List<Symbol> list = mMessage.getValue();
        assert list != null;
        if (!list.isEmpty())
        {
            list.remove(list.size() - 1);
        }
        if (list.isEmpty())
        {
            list.add(null);
        }
        mMessage.setValue(list);
        updateHints();
    }

    public void confirmSymbol()
    {
        List<Symbol> list = mMessage.getValue();
        assert list != null;
        if (list.get(list.size() - 1) != null)
        {
            list.add(null);
            mHints.setValue(Collections.emptyList());
            mFilter.setValue(new SearchFilter());
            mMessage.setValue(list);
        }
    }
}
