package pl.polsl.blissapp.data.room.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @Nullable
    @ColumnInfo(name = "width")
    public String width;

    @Nullable
    @ColumnInfo(name = "view_box_width")
    public String view_box_width;

    @Nullable
    @ColumnInfo(name = "content")
    public String content;

    public SymbolImageEntity(int symbolIndex,
                             @Nullable String width,
                             @Nullable String view_box_width,
                             @Nullable String content)
    {
        this.symbolIndex = symbolIndex;
        this.width = width;
        this.view_box_width = view_box_width;
        this.content = content;
    }
}
