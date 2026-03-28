package pl.polsl.blissapp.ui.repository;

import java.util.List;

import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Symbol;

public interface AlchemyRepository {
    void getDiscovered(Callback<List<Symbol>, Exception> callback);
    void getDiscoveredPaginated(int limit, int offset, Callback<List<Symbol>, Exception> callback);
    void getDiscoveredCount(Callback<Integer, Exception> callback);
    void getUndiscovered(Callback<List<Symbol>, Exception> callback);
    void getRandomUndiscovered(Callback<Symbol, Exception> callback);
    void setDiscovered(Symbol symbol, Callback<Void, Exception> callback);
}