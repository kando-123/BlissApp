package pl.polsl.blissapp.data.room.dto;

import androidx.room.ColumnInfo;

public class TranslationDto
{
    @ColumnInfo(name = "symbol_index")
    public int symbolIndex;

    @ColumnInfo(name = "translation")
    public String translation;
}
