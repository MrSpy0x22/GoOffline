package pl.gooffline.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usages")
public class Usages {
    @PrimaryKey(autoGenerate = true)
    int id;
    String packageName;
    long totalMilis;

    public Usages(String packageName) {
        this(packageName , 0);
    }

    public Usages(String packageName, long totalMilis) {
        this.packageName = packageName;
        this.totalMilis = totalMilis;
    }
}
