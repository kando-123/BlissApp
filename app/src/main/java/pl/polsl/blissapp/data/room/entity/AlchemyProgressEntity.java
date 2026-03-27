package pl.polsl.blissapp.data.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "AlchemyProgress",
        foreignKeys =
        {
            @ForeignKey(entity = SymbolEntity.class,
                        parentColumns = "symbol_index",
                        childColumns = "symbol_index",
                        onDelete = ForeignKey.CASCADE)
        })
public class AlchemyProgressEntity
{
    @PrimaryKey
    @ColumnInfo(name = "symbol_index")
    public int symbolIndex;

    @ColumnInfo(name = "is_discovered", defaultValue = "0")
    public int isDiscovered = 0;
}
