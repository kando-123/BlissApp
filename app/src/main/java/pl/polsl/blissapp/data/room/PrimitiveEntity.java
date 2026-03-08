package pl.polsl.blissapp.data.room;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "Primitives",
        foreignKeys = @ForeignKey(entity = PrimitiveEntity.class,
                                  parentColumns = "primitive_code",
                                  childColumns = "parent_code",
                                  onDelete = ForeignKey.RESTRICT))
public class PrimitiveEntity
{
    @PrimaryKey
    @ColumnInfo(name = "primitive_code")
    @NonNull
    public String primitiveCode;

    @ColumnInfo(name = "parent_code")
    public String parentCode;

    @ColumnInfo(name = "full_name")
    public String fullName;

    public PrimitiveEntity(@NonNull String primitiveCode,
                           String parentCode,
                           String fullName)
    {
        this.primitiveCode = primitiveCode;
        this.parentCode = parentCode;
        this.fullName = fullName;
    }
}
