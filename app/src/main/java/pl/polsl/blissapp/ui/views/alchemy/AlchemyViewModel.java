package pl.polsl.blissapp.ui.views.alchemy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.AlchemyRepository;

@HiltViewModel
public class AlchemyViewModel extends ViewModel
{
    private final AlchemyRepository mAlchemyRepository;
    private final MutableLiveData<CraftingTable> mCraftingTable;
    private final MutableLiveData<Symbol> mResultingSymbol;
    private final MutableLiveData<Symbol> mConstructedSymbols;

    @Inject
    public AlchemyViewModel(AlchemyRepository alchemyRepository)
    {
        this.mAlchemyRepository = alchemyRepository;
        mCraftingTable = new MutableLiveData<>(new CraftingTable());
        mResultingSymbol = new MutableLiveData<>();
        mConstructedSymbols = new MutableLiveData<>();
    }

    LiveData<CraftingTable> getCraftingTable()
    {
        return mCraftingTable;
    }

    LiveData<Symbol> getResultingSymbol()
    {
        return mResultingSymbol;
    }

    LiveData<Symbol> getConstructedSymbols()
    {
        return mConstructedSymbols;
    }

    void addSymbol(Symbol symbol)
    {
        CraftingTable value = mCraftingTable.getValue();
        assert value != null;
        mCraftingTable.setValue(value.addSymbol(symbol));
    }

    void addRadical(Primitive primitive)
    {
        CraftingTable value = mCraftingTable.getValue();
        assert value != null;
        mCraftingTable.setValue(value.addRadical(primitive));
    }

    void removeSymbol(Symbol symbol)
    {
        CraftingTable value = mCraftingTable.getValue();
        assert value != null;
        mCraftingTable.setValue(value.removeSymbol(symbol));
    }

    void removeRadical(Primitive primitive)
    {
        CraftingTable value = mCraftingTable.getValue();
        assert value != null;
        mCraftingTable.setValue(value.removeRadical(primitive));
    }
}
