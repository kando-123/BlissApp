package pl.polsl.blissapp.data.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

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
            "symbol_index"
        FROM
            "AlchemyProgress"
        WHERE
            "is_discovered" = 0;
        """)
    List<Integer> getUndiscovered();

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
