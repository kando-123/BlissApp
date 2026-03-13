package pl.polsl.blissapp.data.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ComponentEntity
{
    @PrimaryKey
    @ColumnInfo(name = "component_index")
    public int componentIndex;
}
