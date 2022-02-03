package pl.gooffline.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "whitelist")
public class Whitelist {
    @PrimaryKey(autoGenerate = false) // dane są resetowane przed zapisem
    public int id;
    @ColumnInfo(name = "package_name")
    public String packageName;
    @ColumnInfo(name = "ignore")
    public boolean ignored;

    public Whitelist(int id , String packageName , boolean ignored) {
        this.id = id;
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
