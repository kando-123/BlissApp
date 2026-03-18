package pl.polsl.blissapp.ui.views.blisswriter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    public static sealed class MessageItem {
        public static final class SymbolItem extends MessageItem {
            public final Symbol symbol;
            public SymbolItem(Symbol symbol) { this.symbol = symbol; }
        }
        public static final class EmptySlot extends MessageItem {
            public static final EmptySlot INSTANCE = new EmptySlot();
            private EmptySlot() {}
        }
    }

    private final SymbolRepository mSymbolRepository;
    private final MutableLiveData<List<MessageItem>> mMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Symbol>> mHints = new MutableLiveData<>();
    private final MutableLiveData<List<Primitive>> mFilter = new MutableLiveData<>();
    private final MutableLiveData<Exception> mFailure = new MutableLiveData<>();

    private static final int MAX_HINT_COUNT = 20;

    @Inject
    public BlissWriterViewModel(SymbolRepository symbolRepository)
    {
        mSymbolRepository = symbolRepository;

        List<MessageItem> list = new ArrayList<>();
        list.add(MessageItem.EmptySlot.INSTANCE);
        mMessage.setValue(list);
        mFilter.setValue(new ArrayList<>());
    }

    // Return type changed to LiveData<List<MessageItem>>
    LiveData<List<MessageItem>> getMessage()
    {
        return mMessage;
    }

    LiveData<List<Symbol>> getHints()
    {
        return mHints;
    }

    LiveData<List<Primitive>> getFilter()
    {
        return mFilter;
    }

    LiveData<Exception> getFailure()
    {
        return mFailure;
    }

    public void putPrimitive(@Nullable Primitive primitive)
    {
        if (primitive == null) { return; }

        Log.d("BlissWriterViewModel", "Putting primitive: " + primitive.name());

        List<Primitive> value = mFilter.getValue();
        assert value != null;
        value.add(primitive);
        mFilter.setValue(value);
        updateHints();
    }

    public void removePrimitive(@NonNull Primitive primitive)
    {
        Log.d("BlissWriterViewModel", "Removing primitive: " + primitive.name());

        List<Primitive> value = mFilter.getValue();
        assert value != null;

        value.remove(primitive);
        mFilter.setValue(value);
        updateHints();
    }

    public void selectHint(Symbol symbol)
    {
        List<MessageItem> list = mMessage.getValue();
        assert list != null;
        assert !list.isEmpty();

        // replace the last item (which must be EmptySlot) with a SymbolItem
        list.set(list.size() - 1, new MessageItem.SymbolItem(symbol));
        mMessage.setValue(list);

        mFilter.setValue(new ArrayList<>());
        updateHints();
    }

    private void updateHints()
    {
        Log.d("BlissWriterViewModel", "Updating hints");

        List<MessageItem> items = mMessage.getValue();
        assert items != null;

        // Determine the last symbol (if any)
        Symbol lastSymbol = null;
        if (!items.isEmpty() && items.get(items.size() - 1) instanceof MessageItem.SymbolItem) {
            lastSymbol = ((MessageItem.SymbolItem) items.get(items.size() - 1)).symbol;
        }

        List<Primitive> primitives = mFilter.getValue();
        assert primitives != null;

        var callback = new Callback<List<Symbol>, Exception>()
        {
            @Override
            public void onSuccess(List<Symbol> data)
            {
                mHints.postValue(data);
            }

            @Override
            public void onFailure(Exception data)
            {
                mHints.postValue(Collections.emptyList());
                mFailure.postValue(data);
            }
        };
        assert mSymbolRepository != null;
        mSymbolRepository.getMatchingSymbols(lastSymbol, primitives, MAX_HINT_COUNT, callback);
    }

    public void popSymbol()
    {
        List<MessageItem> list = mMessage.getValue();
        assert list != null;
        if (!list.isEmpty())
        {
            list.remove(list.size() - 1);
        }
        if (list.isEmpty())
        {
            list.add(MessageItem.EmptySlot.INSTANCE);
        }
        mMessage.setValue(list);
        updateHints();
    }

    public void confirmSymbol()
    {
        List<MessageItem> list = mMessage.getValue();
        assert list != null;
        if (list.get(list.size() - 1) instanceof MessageItem.SymbolItem)
        {
            list.add(MessageItem.EmptySlot.INSTANCE);
            mHints.setValue(Collections.emptyList());
            mFilter.setValue(new ArrayList<>());
            mMessage.setValue(list);
        }
    }
}
