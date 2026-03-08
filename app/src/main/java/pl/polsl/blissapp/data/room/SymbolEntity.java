package pl.polsl.blissapp.data.room;

import androidx.annotation.Nullable;
import androidx.room.*;

@Entity(tableName = "Symbols")
public class SymbolEntity
{
    @PrimaryKey
    @ColumnInfo(name = "symbol_index")
    public int symbolIndex;

    @ColumnInfo(name = "resource_uri")
    public String resourceUri;

    public SymbolEntity(int symbolIndex, String resourceUri)
    {
        this.symbolIndex = symbolIndex;
        this.resourceUri = resourceUri;
    }
}
