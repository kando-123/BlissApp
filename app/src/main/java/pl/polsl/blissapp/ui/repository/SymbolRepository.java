package pl.polsl.blissapp.ui.repository;

import java.util.List;

import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.common.Radical;
import pl.polsl.blissapp.data.model.MeaningfulSymbol;
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
     * @param filter the radicals that the radicals of the simple symbol,
     *               or the remaining part of the compound symbol, are required to contain
     * @param maxCount the maximum number of symbols to return
     * @param callback the callback that will be called with the results, or failure
     */
    void getMatchingSymbols(Symbol symbol,
                            List<Radical> filter,
                            int maxCount,
                            Callback<List<Symbol>, Exception> callback);

    /**
     * Gets the meanings of the given symbol.
     *
     * @param symbol the symbol whose meanings should be retrieved
     * @param callback the callback that will be called with the results, or failure
     */
    void getMeanings(Symbol symbol,
                     Callback<List<String>, Exception> callback);

    /**
     * Gets the symbols and their meanings for given text input.
     *
     * @param input
     * @param callback
     */
    void getTranslations(String input,
                         Callback<List<MeaningfulSymbol>, Exception> callback);
}
