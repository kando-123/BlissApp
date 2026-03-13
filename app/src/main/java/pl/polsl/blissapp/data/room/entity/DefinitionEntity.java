package pl.polsl.blissapp.data.room.entity;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "Definition",
        primaryKeys = {"component_index", "variant", "primitive_code"},
        foreignKeys =
        {
            @ForeignKey(entity = ComponentEntity.class,
                        parentColumns = "component_index",
                        childColumns = "component_index",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = PrimitiveEntity.class,
                        parentColumns = "primitive_code",
                        childColumns = "primitive_code",
                        onDelete = ForeignKey.RESTRICT)
        })
public class DefinitionEntity
{
    @ColumnInfo(name = "component_index")
    public int componentIndex;

    @ColumnInfo(name = "variant")
    public int variant;

    @ColumnInfo(name = "primitive_code")
    @NonNull
    public String primitiveCode;

    @ColumnInfo(name = "primitive_count")
    public int primitiveCount;

    public DefinitionEntity(int componentIndex,
                            int variant,
                            @NonNull String primitiveCode,
                            int primitiveCount)
    {
        this.componentIndex = componentIndex;
        this.variant = variant;
        this.primitiveCode = primitiveCode;
        this.primitiveCount = primitiveCount;
    }
}
