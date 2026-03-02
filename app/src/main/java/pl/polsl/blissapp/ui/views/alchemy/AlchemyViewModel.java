package pl.polsl.blissapp.ui.views.alchemy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import pl.polsl.blissapp.common.Callback;
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
    private final MutableLiveData<List<Symbol>> constructedSymbols;
    private final MutableLiveData<Exception> failure;

    @Inject
    public AlchemyViewModel(AlchemyRepository alchemyRepository)
    {
        this.alchemyRepository = alchemyRepository;
        craftingTable = new MutableLiveData<>(new CraftingTable());
        resultingSymbol = new MutableLiveData<>();
        constructedSymbols = new MutableLiveData<>();
        failure = new MutableLiveData<>();
    }

    LiveData<CraftingTable> getCraftingTable()
    {
        return craftingTable;
    }

    LiveData<Symbol> getResultingSymbol()
    {
        return resultingSymbol;
    }

    LiveData<List<Symbol>> getConstructedSymbols()
    {
        return constructedSymbols;
    }

    private void updateResultingSymbol()
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        var callback = new Callback<List<Symbol>, Exception>()
        {
            @Override
            public void onSuccess(List<Symbol> data)
            {
                // Choose the first of the returned symbols that has not been constructed yet.

                List<Symbol> value =  constructedSymbols.getValue();
                assert value != null;
                Set<Symbol> symbols = new HashSet<>(value);
                for (Symbol symbol : data)
                {
                    if (!symbols.contains(symbol))
                    {
                        resultingSymbol.setValue(symbol);
                    }
                }
            }

            @Override
            public void onFailure(Exception data)
            {
                failure.setValue(data);
            }
        };
        alchemyRepository.getConstructibleSymbol(value.radicals, value.symbols, value.indicators, callback);
    }

    void addSymbol(Symbol symbol)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        value.symbols.add(symbol);
        craftingTable.setValue(value);
        updateResultingSymbol();
    }

    void addRadical(Radical radical)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        value.radicals.add(radical);
        craftingTable.setValue(value);
        updateResultingSymbol();
    }

    void addIndicator(Indicator indicator)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        value.indicators.add(indicator);
        craftingTable.setValue(value);
        updateResultingSymbol();
    }

    void removeSymbol(Symbol symbol)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        value.symbols.remove(symbol);
        craftingTable.setValue(value);
        updateResultingSymbol();
    }

    void removeRadical(Radical radical)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        value.radicals.remove(radical);
        craftingTable.setValue(value);
        updateResultingSymbol();
    }

    void removeIndicator(Indicator indicator)
    {
        CraftingTable value = craftingTable.getValue();
        assert value != null;
        value.indicators.remove(indicator);
        craftingTable.setValue(value);
        updateResultingSymbol();
    }

    void craft()
    {
        Symbol symbol = resultingSymbol.getValue();
        if (symbol != null)
        {
            List<Symbol> symbols = constructedSymbols.getValue();
            assert symbols != null;
            symbols.add(symbol);
            constructedSymbols.setValue(symbols);
            resultingSymbol.setValue(null);
        }
    }
}
