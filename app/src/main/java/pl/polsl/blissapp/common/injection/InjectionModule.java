package pl.polsl.blissapp.common.injection;

import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.data.repository.SymbolRepositoryImpl;
import pl.polsl.blissapp.ui.repository.AlchemyRepository;
import pl.polsl.blissapp.ui.repository.SymbolRepository;

@Module
@InstallIn(SingletonComponent.class)
public class InjectionModule
{
    @Provides
    @Singleton
    public SymbolRepository provideSymbolRepository()
    {
        // Replace with a real implementation.
        return new SymbolRepositoryImpl();
    }

    @Provides
    @Singleton
    public AlchemyRepository provideAlchemyRepository()
    {
        return new AlchemyRepository()
        {
            @Override
            public void getGameState(Callback<Set<Symbol>, Exception> callback)
            {
                callback.onFailure(new Exception("Not implemented yet"));
            }

            @Override
            public void setGameState(Set<Symbol> newGameState)
            {
            }

            @Override
            public void getConstructibleSymbol(List<Primitive> r,
                                               List<Symbol> s,
                                               Callback<List<Symbol>, Exception> callback)
            {
                callback.onFailure(new Exception("Not implemented yet"));
            }

            @Override
            public void getDerivedSymbols(Symbol symbol, Callback<List<Symbol>, Exception> callback)
            {
                callback.onFailure(new Exception("Not implemented yet"));
            }

            @Override
            public void getFrontier(Set<Symbol> discoveredSymbols, Callback<List<Symbol>, Exception> callback)
            {
                callback.onFailure(new Exception("Not implemented yet"));
            }

            @Override
            public void getFrontierIncrease(Set<Symbol> currentlyDiscoveredSymbols, Symbol newlyDiscoveredSymbol, Callback<List<Symbol>, Exception> callback)
            {
                callback.onFailure(new Exception("Not implemented yet"));
            }
        };
    }
}
