package pl.polsl.blissapp.data.repository;

import java.util.List;

import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.MeaningfulSymbol;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

public class SymbolRepositoryImpl implements SymbolRepository
{
    @Override
    public void getMatchingSymbols(Symbol symbol,
                                   List<Primitive> primitives,
                                   int maxCount,
                                   Callback<List<Symbol>, Exception> callback)
    {

    }

    @Override
    public void getMeanings(Symbol symbol,
                            Callback<List<String>, Exception> callback)
    {

    }

    @Override
    public void getTranslations(String input,
                                Callback<List<MeaningfulSymbol>, Exception> callback)
    {

    }
}
