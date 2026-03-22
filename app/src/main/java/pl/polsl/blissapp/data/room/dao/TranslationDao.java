package pl.polsl.blissapp.data.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import pl.polsl.blissapp.data.model.Translation;
import pl.polsl.blissapp.data.room.dto.TranslationDto;

@Dao
public interface TranslationDao
{
    /**
     * Returns the meanings of the given symbol in the given language.
     *
     * @param symbolIdx
     * @return
     */
    @Query("""
        SELECT
            "translation"
        FROM
            "Translation"
        WHERE
            "symbol_index" = :symbolIdx AND "language_code" = :language;
        """)
    List<String> getMeanings(int symbolIdx, String language);

    /**
     * Returns symbols with their meanings, with the meanings relating to the given input.
     *
     * @param input
     * @param language
     * @return
     */
    @Query("""
        SELECT
            "symbol_index", "translation"
        FROM
            "Translation"
        WHERE
            "translation" LIKE '%' || :input || '%' AND "language_code" = :language
        ORDER BY
            "symbol_index";
        """)
    List<TranslationDto> getTranslations(String input, String language);
}
