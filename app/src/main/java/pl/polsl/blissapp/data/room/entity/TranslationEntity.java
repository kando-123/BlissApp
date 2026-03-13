package pl.polsl.blissapp.data.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Translation",
        foreignKeys =
        {
            @ForeignKey(entity = SymbolEntity.class,
                    parentColumns = "symbol_index",
                    childColumns = "symbol_index",
                    onDelete = ForeignKey.CASCADE)
        })
public class TranslationEntity
{
    @PrimaryKey
    @ColumnInfo(name = "identifier")
    public int identifier;

    @ColumnInfo(name = "symbol_index")
    public int symbolIndex;

    @ColumnInfo(name = "language_code")
    @NonNull
    public String languageCode;

    @ColumnInfo(name = "translation")
    @NonNull
    public String translation;

    public TranslationEntity(int identifier,
                             int symbolIndex,
                             @NonNull String languageCode,
                             @NonNull String translation)
    {
        this.identifier = identifier;
        this.symbolIndex = symbolIndex;
        this.languageCode = languageCode;
        this.translation = translation;
    }
}
