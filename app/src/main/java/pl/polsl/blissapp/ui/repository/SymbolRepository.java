package pl.polsl.blissapp.ui.repository;

import java.util.List;
import java.util.Map;

import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Translation;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;

public interface SymbolRepository
{
    /**
     * Gets the symbols that begin with the given symbol and contain all the radicals from the filter.
     *
     * <p>If {@code symbol} is {@code null}, the matching {@code SimpleSymbol}s should be returned.
     * A simple symbol matches if method {@code matches} called with the filter returns a non-negative
     * number. The symbols should be sorted increasingly with respect to the returned result.</p>
     *
     * <p>Otherwise, if {@code symbol} is not {@code null}, the matching {@code CompoundSymbol}s
     * should be returned. A compound symbol matches if method {@code matches} called with the symbol
     * and the filter returns a non-negative number. The symbols should be sorted increasingly
     * with respect to the returned result.</p>
     *
     * @param symbol the symbol that the results are expected to begin with, or null
     * @param primitives the radicals that the radicals of the simple symbol,
     *                 or the remaining part of the compound symbol, are required to contain
     * @param maxCount the maximum number of symbols to return
     * @param callback the callback that will be called with the results, or failure
     */
    void getMatchingSymbols(Symbol symbol,
                            Map<Primitive, Integer> primitives,
                            int maxCount,
                            Callback<List<Symbol>, Exception> callback);

    void getSvg(Symbol symbol,
                Callback<String, Exception> callback);

    /**
     * Gets all possible primitive variants (decompositions) for the given symbol.
     * Each map in the list represents one valid set of primitives that make up the symbol.
     */
    void getPrimitiveVariants(Symbol symbol,
                              Callback<List<Map<Primitive, Integer>>, Exception> callback);
                       
    /**
     * Gets the list of symbols that compose the given symbol.
     */
    void getComponents(Symbol symbol,
                       Callback<List<Symbol>, Exception> callback);
}
