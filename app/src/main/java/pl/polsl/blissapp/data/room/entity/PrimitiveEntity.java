package pl.polsl.blissapp.data.room.entity;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "Primitive")
public class PrimitiveEntity
{
    @PrimaryKey
    @ColumnInfo(name = "primitive_code")
    @NonNull
    public String primitiveCode;

    public PrimitiveEntity(@NonNull String primitiveCode)
    {
        this.primitiveCode = primitiveCode;
    }
}
