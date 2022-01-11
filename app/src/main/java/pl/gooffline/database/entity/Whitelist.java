package pl.gooffline.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "whitelist")
public class Whitelist {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "package_name")
    public String packageName;
    @ColumnInfo(name = "ignore")
    public boolean ignored;

    public Whitelist(String packageName , boolean ignored) {
        this.packageName = packageName;
        this.ignored = ignored;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isIgnored() {
        return ignored;
    }
}
