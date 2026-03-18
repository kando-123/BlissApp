package pl.polsl.blissapp.data.room.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

import pl.polsl.blissapp.data.room.dto.SymbolDto;
import pl.polsl.blissapp.data.room.dto.VariantDto;

@Dao
public interface SymbolDao
{
    @RawQuery
    List<SymbolDto> getSymbols(SupportSQLiteQuery query);

    @Query("""
        SELECT
            "component_index"
        FROM
            "Composition"
        WHERE
            "symbol_index" = :symbolIdx
        ORDER BY
            "component_position" ASC
        """)
    List<Integer> getComponents(int symbolIdx);

    @Query("""
        SELECT
        	"variant", "primitive_code", "primitive_count"
        FROM
        	"Definition"
        WHERE
        	"component_index" = :componentIdx
        ORDER BY
        	"variant" ASC;
        """)
    List<VariantDto> getVariants(int componentIdx);
}
