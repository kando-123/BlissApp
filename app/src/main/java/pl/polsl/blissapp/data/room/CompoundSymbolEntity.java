package pl.polsl.blissapp.data.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "CompoundSymbols",
        primaryKeys = {"symbol_index", "component_index", "component_ordinal"},
        foreignKeys =
        {
            @ForeignKey(entity = SymbolEntity.class,
                        parentColumns = "symbol_index",
                        childColumns = "symbol_index",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = SymbolEntity.class,
                        parentColumns = "symbol_index",
                        childColumns = "component_index",
                        onDelete = ForeignKey.CASCADE)
        })
public class CompoundSymbolEntity
{
    @ColumnInfo(name = "symbol_index")
    public int symbolIndex;

    @ColumnInfo(name = "component_index")
    public int componentIndex;

    @ColumnInfo(name = "component_ordinal")
    public int componentOrdinal;

    public CompoundSymbolEntity(int symbolIndex,
                                int componentIndex,
                                int componentOrdinal)
    {
        this.symbolIndex = symbolIndex;
        this.componentIndex = componentIndex;
        this.componentOrdinal = componentOrdinal;
    }
}
