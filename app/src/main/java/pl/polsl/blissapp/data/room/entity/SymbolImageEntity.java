package pl.polsl.blissapp.data.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "SymbolImage",
        foreignKeys =
        {
            @ForeignKey(entity = SymbolEntity.class,
                        parentColumns = "symbol_index",
                        childColumns = "symbol_index",
                        onDelete = ForeignKey.CASCADE)
        })
public class SymbolImageEntity
{
    @PrimaryKey
    @ColumnInfo(name = "symbol_index")
    public int symbolIndex;

    @NonNull
    @ColumnInfo(name = "svg_value")
    public String svgValue;

    public SymbolImageEntity(int symbolIndex, @NonNull String svgValue)
    {
        this.symbolIndex = symbolIndex;
        this.svgValue = svgValue;
    }
}
