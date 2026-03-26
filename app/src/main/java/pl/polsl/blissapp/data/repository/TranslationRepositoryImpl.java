package pl.polsl.blissapp.data.repository;

import java.util.ArrayList;
import java.util.List;

import pl.polsl.blissapp.BlissApplication;
import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.common.exception.NoResultsException;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.data.model.Translation;
import pl.polsl.blissapp.data.room.BlissDatabase;
import pl.polsl.blissapp.data.room.dto.TranslationDto;
import pl.polsl.blissapp.ui.repository.TranslationRepository;

public class TranslationRepositoryImpl implements TranslationRepository
{
    private final BlissDatabase database = BlissApplication.getDatabase();

    @Override
    public void getMeanings(Symbol symbol,
                            String language,
                            Callback<List<String>, Exception> callback)
    {
        Thread worker = new Thread(() ->
        {
            List<String> meanings = database.translationDao().getMeanings(symbol.index(), language);
            if (meanings != null && !meanings.isEmpty())
            {
                callback.onSuccess(meanings);
            }
            else
            {
                callback.onFailure(new NoResultsException());
            }
        });
        worker.start();
    }

    @Override
    public void getTranslations(String input,
                                String language,
                                Callback<List<Translation>, Exception> callback)
    {
        Thread worker = new Thread(() ->
        {
            String pattern = "%" + input + "%";
            List<TranslationDto> dtos = database.translationDao().getTranslations(pattern, language);

            if (dtos != null && !dtos.isEmpty())
            {
                List<Translation> result = new ArrayList<>();

                int symbolIndex = dtos.get(0).symbolIndex;
                List<String> translations = new ArrayList<>();
                translations.add(dtos.get(0).translation);
                for (int i = 1; i < dtos.size(); ++i)
                {
                    int index = dtos.get(i).symbolIndex;
                    String translation = dtos.get(i).translation;

                    if (index == symbolIndex)
                    {
                        translations.add(translation);
                    }
                    else
                    {
                        result.add(new Translation(new Symbol(symbolIndex), translations));
                        symbolIndex = index;
                        translations = new ArrayList<>();
                        translations.add(translation);
                    }
                }
                result.add(new Translation(new Symbol(symbolIndex), translations));

                callback.onSuccess(result);
            }
            else
            {
                callback.onFailure(new NoResultsException());
            }
        });
        worker.start();
    }

}
