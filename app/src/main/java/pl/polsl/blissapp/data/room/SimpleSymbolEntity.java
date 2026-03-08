package pl.polsl.blissapp.data.room;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "SimpleSymbols",
        primaryKeys = {"symbol_index", "primitive_code"},
        foreignKeys =
        {
            @ForeignKey(entity = SymbolEntity.class,
                        parentColumns = "symbol_index",
                        childColumns = "symbol_index",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = PrimitiveEntity.class,
                        parentColumns = "primitive_code",
                        childColumns = "primitive_code",
                        onDelete = ForeignKey.RESTRICT)
        })
public class SimpleSymbolEntity
{
    @ColumnInfo(name = "symbol_index")
    public int symbolIndex;

    @ColumnInfo(name = "primitive_code")
    @NonNull
    public String primitiveCode;

    @ColumnInfo(name = "primitive_count")
    public int primitiveCount;

    public SimpleSymbolEntity(int symbolIndex,
                              @NonNull String primitiveCode,
                              int primitiveCount)
    {
        this.symbolIndex = symbolIndex;
        this.primitiveCode = primitiveCode;
        this.primitiveCount = primitiveCount;
    }
}
