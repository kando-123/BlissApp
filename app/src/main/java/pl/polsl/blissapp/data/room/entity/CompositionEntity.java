package pl.polsl.blissapp.data.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "CompoundSymbols",
        primaryKeys = {"symbol_index", "component_position"},
        foreignKeys =
        {
            @ForeignKey(entity = SymbolEntity.class,
                        parentColumns = "symbol_index",
                        childColumns = "symbol_index",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = ComponentEntity.class,
                        parentColumns = "component_index",
                        childColumns = "component_index",
                        onDelete = ForeignKey.RESTRICT)
        })
public class CompositionEntity
{
    @ColumnInfo(name = "symbol_index")
    public int symbolIndex;

    @ColumnInfo(name = "component_index")
    public int componentIndex;

    @ColumnInfo(name = "component_position")
    public int componentPosition;

    public CompositionEntity(int symbolIndex,
                             int componentIndex,
                             int componentPosition)
    {
        this.symbolIndex = symbolIndex;
        this.componentIndex = componentIndex;
        this.componentPosition = componentPosition;
    }
}
