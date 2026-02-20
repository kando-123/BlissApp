package pl.polsl.blissapp.common.injection;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.common.Radical;
import pl.polsl.blissapp.data.model.MeaningfulSymbol;
import pl.polsl.blissapp.data.model.Symbol;
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
        return new SymbolRepository()
        {
            @Override
            public void getMatchingSymbols(Symbol symbol,
                                           List<Radical> filter,
                                           int maxCount,
                                           Callback<List<Symbol>, Exception> callback)
            {
                callback.onFailure(new Exception("Not implemented yet"));
            }

            @Override
            public void getMeanings(Symbol symbol,
                                    Callback<List<String>, Exception> callback)
            {
                callback.onFailure(new Exception("Not implemented yet"));
            }

            @Override
            public void getTranslations(String input,
                                        Callback<List<MeaningfulSymbol>, Exception> callback)
            {
                callback.onFailure(new Exception("Not implemented yet"));
            }
        };
    }
}
