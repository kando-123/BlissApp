package pl.polsl.blissapp.data.repository;

import java.util.List;

import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.MeaningfulSymbol;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

public class SymbolRepositoryImpl implements SymbolRepository
{
    /**
     * Retrieves the symbols that match given filter.
     * <p>
     * If {@code symbol} is {@code null}, the result contains {@code SimpleSymbol}s that contain
     * all given {@code primitives} (and possibly some else).
     * <p>
     * If {@code symbol} is not {@code null}, the result contains {@code CompoundSymbol}s that
     * begin with the given {@code symbol}, and whose remaining part contains all given {@code
     * primitives} (and possibly some else).
     * <p>
     * The returned list contains at most {@code maxCount} elements. They are sorted according to
     * how exact the match is, i.e. increasingly with respect to the value of {@code matches} method.
     *
     * @param symbol the symbol that the results are expected to begin with, or null
     * @param primitives the primitives that the set of primitives of the simple symbol,
     *                   or the set of remaining primitives of the compound symbol,
     *                   are required to contain
     * @param maxCount the maximum number of symbols to return
     * @param callback the callback that will be called with the results, or failure
     */
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
