package pl.polsl.blissapp.common.injection;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import pl.polsl.blissapp.data.repository.AlchemyRepositoryImpl;
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
        return new AlchemyRepositoryImpl();
    }
}
