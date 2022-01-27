package pl.gooffline.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "logging")
public class Log {
    @PrimaryKey(autoGenerate = true)
    long log_id;
    int log_type;
    String log_message;
    long log_date;

    public Log(int log_type, String log_message, long log_date) {
        this.log_type = log_type;
        this.log_message = log_message;
        this.log_date = log_date;
    }

    public long getLog_id() {
        return log_id;
    }

    public int getLog_type() {
        return log_type;
    }

    public String getLog_message() {
        return log_message;
    }

    public long getLog_date() {
        return log_date;
    }
}
