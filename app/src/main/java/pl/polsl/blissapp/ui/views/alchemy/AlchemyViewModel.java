package pl.polsl.blissapp.ui.views.alchemy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.data.model.Indicator;
import pl.polsl.blissapp.data.model.Radical;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.AlchemyRepository;

@HiltViewModel
public class AlchemyViewModel extends ViewModel
{
    private final AlchemyRepository alchemyRepository;
    private final MutableLiveData<CraftingTable> craftingTable;
    private final MutableLiveData<Symbol> resultingSymbol;
    private final MutableLiveData<Symbol> constructedSymbols;

    @Inject
    public AlchemyViewModel(AlchemyRepository alchemyRepository)
    {
        this.alchemyRepository = alchemyRepository;
        craftingTable = new MutableLiveData<>(new CraftingTable());
        resultingSymbol = new MutableLiveData<>();
        constructedSymbols = new MutableLiveData<>();
    }

    LiveData<CraftingTable> getCraftingTable()
    {
        return craftingTable;
    }

    LiveData<Symbol> getResultingSymbol()
    {
        return resultingSymbol;
    }

    LiveData<Symbol> getConstructedSymbols()
    {
        return constructedSymbols;
    }

    void addSymbol(Symbol symbol)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        craftingTable.setValue(value.addSymbol(symbol));
    }

    void addRadical(Radical radical)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        craftingTable.setValue(value.addRadical(radical));
    }

    void addIndicator(Indicator indicator)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        craftingTable.setValue(value.addIndicator(indicator));
    }

    void removeSymbol(Symbol symbol)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        craftingTable.setValue(value.removeSymbol(symbol));
    }

    void removeRadical(Radical radical)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        craftingTable.setValue(value.removeRadical(radical));
    }

    void removeIndicator(Indicator indicator)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        craftingTable.setValue(value.removeIndicator(indicator));
    }
}
