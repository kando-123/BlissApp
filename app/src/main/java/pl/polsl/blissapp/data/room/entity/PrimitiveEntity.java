package pl.polsl.blissapp.data.room.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.*;

@Entity(tableName = "Primitive",
        foreignKeys =
        {
            @ForeignKey(entity = PrimitiveEntity.class,
                        parentColumns = "primitive_code",
                        childColumns = "parent_code",
                        onDelete = ForeignKey.SET_NULL)
        })
public class PrimitiveEntity
{
    @PrimaryKey
    @ColumnInfo(name = "primitive_code")
    @NonNull
    public String primitiveCode;

    @ColumnInfo(name = "parent_code")
    public String parentCode;

    public PrimitiveEntity(@NonNull String primitiveCode)
    {
        this.primitiveCode = primitiveCode;
    }
}
