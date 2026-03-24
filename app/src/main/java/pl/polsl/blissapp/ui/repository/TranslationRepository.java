package pl.polsl.blissapp.ui.repository;

import java.util.List;

import pl.polsl.blissapp.common.Callback;
import pl.polsl.blissapp.data.model.Symbol;
import pl.polsl.blissapp.data.model.Translation;

public interface TranslationRepository
{
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
                         Callback<List<Translation>, Exception> callback);
}
