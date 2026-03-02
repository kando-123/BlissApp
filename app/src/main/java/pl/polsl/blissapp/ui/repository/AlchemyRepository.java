package pl.polsl.blissapp.ui.repository;

import java.util.List;
import java.util.Set;

import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;

public interface AlchemyRepository
{
    void getGameState(Callback<Set<Symbol>, Exception> callback);
    void setGameState(Set<Symbol> newGameState);

    /**
     * Gets the symbols that are constructed from the given radicals and symbols.
     * The returned symbols need to comprise <em>all</em> the input elements and <em>none</em> more.
     *
     * <p>If no symbol can be constructed, method {@code onFailure} should be invoked.</p>
     *
     * @param inputPrimitives
     * @param inputSymbols
     * @param callback
     */
    void getConstructibleSymbol(List<Primitive> inputPrimitives,
                                List<Symbol> inputSymbols,
                                Callback<List<Symbol>, Exception> callback);

    /**
     * Gets the symbols that are derived from the given symbol.
     *
     * <p>If {@code symbol} is a {@code SimpleSymbol}, the derived symbols should contain
     * the simple symbols that can be constructed from {@code symbol} by adding radicals,
     * and the compound symbols that contain {@code symbol} and other simple symbols.</p>
     *
     * <p>If {@code symbol} is a {@code CompoundSymbol}, the derived symbols should contain
     * the compound symbols that contain all units of {@code symbol}.</p>
     *
     * @param symbol
     * @param callback
     */
    void getDerivedSymbols(Symbol symbol,
                           Callback<List<Symbol>, Exception> callback);

    /**
     * Gets the symbols for whose construction it is sufficient to use the {@code discoveredSymbols}
     * and radicals. The returned symbols themselves should not belong to {@code discoveredSymbols}.
     *
     * @param discoveredSymbols
     * @param callback
     */
    void getFrontier(Set<Symbol> discoveredSymbols,
                     Callback<List<Symbol>, Exception> callback);

    /**
     * Gets the collection of symbols that are derived from the {@code newlyDiscoveredSymbol} and
     * that are constructible from the {@code currentlyDiscoveredSymbols}.
     *
     * @param currentlyDiscoveredSymbols
     * @param newlyDiscoveredSymbol
     * @param callback
     */
    void getFrontierIncrease(Set<Symbol> currentlyDiscoveredSymbols,
                             Symbol newlyDiscoveredSymbol,
                             Callback<List<Symbol>, Exception> callback);
}
