package pl.gooffline.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usages")
public class Usages {
    @PrimaryKey(autoGenerate = true)
    int id;
    String packageName;
    int dayTimestamp;
    int totalSeconds;

    public Usages(String packageName, int dayTimestamp, int totalSeconds) {
        this.packageName = packageName;
        this.dayTimestamp = dayTimestamp;
        this.totalSeconds = totalSeconds;
    }

    public int getId() {
        return id;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getDayTimestamp() {
        return dayTimestamp;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
}
