package pl.polsl.blissapp.data.room.entity;

import androidx.room.*;

@Entity(tableName = "Symbol")
public class SymbolEntity
{
    @PrimaryKey
    @ColumnInfo(name = "symbol_index")
    public int symbolIndex;

    @ColumnInfo(name = "symbol_name")
    public String symbolName;

    @ColumnInfo(name = "resource_uri")
    public String resourceUri;

    @ColumnInfo(name = "description")
    public String description;
}
