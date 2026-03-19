package pl.polsl.blissapp.data.room.dto;

import androidx.room.ColumnInfo;

public class SymbolDto
{
    @ColumnInfo(name = "symbol_index")
    public int index;

    @ColumnInfo(name = "resource_uri")
    public String resourceUri;

    @ColumnInfo(name = "min_size")
    public int minSize;
}
