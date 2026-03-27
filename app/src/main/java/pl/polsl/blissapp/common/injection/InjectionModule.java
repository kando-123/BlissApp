package pl.polsl.blissapp.common.injection;

import android.content.Context;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Primitive;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.data.repository.SymbolRepositoryImpl;
import pl.polsl.blissapp.data.repository.TranslationRepositoryImpl;
import pl.polsl.blissapp.ui.common.TextToSpeechManager;
import pl.polsl.blissapp.ui.repository.AlchemyRepository;
import pl.polsl.blissapp.ui.repository.SymbolRepository;
import pl.polsl.blissapp.ui.repository.TranslationRepository;

@Module
@InstallIn(SingletonComponent.class)
public class InjectionModule
{
    @Provides
    @Singleton
    public TextToSpeechManager provideTextToSpeechManager(@ApplicationContext Context context)
    {
        return new TextToSpeechManager(context);
    }

    @Provides
    @Singleton
    public SymbolRepository provideSymbolRepository()
    {
        return new SymbolRepositoryImpl();
    }

    @Provides
    @Singleton
    public TranslationRepository provideTranslationRepository()
    {
        return new TranslationRepositoryImpl();
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
        };
    }
}
