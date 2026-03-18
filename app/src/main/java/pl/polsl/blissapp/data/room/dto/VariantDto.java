package pl.polsl.blissapp.data.room.dto;

import androidx.room.ColumnInfo;

public class VariantDto
{
    @ColumnInfo(name = "variant")
    public int variant;

    @ColumnInfo(name = "primitive_code")
    public String primitiveCode;

    @ColumnInfo(name = "primitive_count")
    public int primitiveCount;
}
