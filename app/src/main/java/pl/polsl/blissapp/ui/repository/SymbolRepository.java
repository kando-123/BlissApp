package pl.polsl.blissapp.ui.repository;

import java.util.List;

import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.common.Radical;
import pl.polsl.blissapp.data.model.Symbol;

public interface SymbolRepository
{
    void getMatchingSymbols(Symbol symbol, List<Radical> filter, int maxCount,
                            Callback<List<Symbol>, Exception> callback);

    void getTranslations(String input, Callback<List<Symbol>, Exception> callback);
}
