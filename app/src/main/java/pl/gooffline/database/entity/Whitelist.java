package pl.gooffline.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Whitelist {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "package_name")
    public String packageName;

    public Whitelist(String packageName) {
        this.packageName = packageName;
    }
}
