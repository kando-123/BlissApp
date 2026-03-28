package pl.polsl.blissapp.data.room.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

import pl.polsl.blissapp.data.room.entity.AlchemyProgressEntity;

@Dao
public interface AlchemyDao
{
    @Query("""
        SELECT
            "is_discovered"
        FROM
            "AlchemyProgress"
        WHERE
            "symbol_index" = :symbolIndex;
        """)
    Integer isDiscovered(int symbolIndex);

    @Query("""
        SELECT
            S."symbol_index"
        FROM
            "Symbol" S
        LEFT JOIN
            "AlchemyProgress" P ON S."symbol_index" = P."symbol_index"
        WHERE
            P."is_discovered" IS NULL OR P."is_discovered" = 0;
        """)
    List<Integer> getUndiscovered();

    @Query("""
        SELECT
            "symbol_index"
        FROM
            "AlchemyProgress"
        WHERE
            "is_discovered" = 1;
        """)
    List<Integer> getDiscovered();

    @Query("""
        SELECT
            S."symbol_index"
        FROM
            "Symbol" S
        LEFT JOIN
            "AlchemyProgress" P ON S."symbol_index" = P."symbol_index"
        WHERE
            P."is_discovered" IS NULL OR P."is_discovered" = 0
        ORDER BY RANDOM()
        LIMIT 1;
        """)
    Integer getRandomUndiscovered();

    @Upsert
    void upsertProgress(AlchemyProgressEntity progress);

    @Query("""
        UPDATE
        	"AlchemyProgress"
        SET
        	"is_discovered" = 1
        WHERE
        	"symbol_index" = :symbolIndex;
        """)
    void setDiscovered(int symbolIndex);

    @Query("""
        UPDATE
        	"AlchemyProgress"
        SET
        	"is_discovered" = 0
        WHERE
        	"symbol_index" = :symbolIndex;
        """)
    void setUndiscovered(int symbolIndex);
}
