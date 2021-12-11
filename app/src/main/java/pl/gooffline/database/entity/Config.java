package pl.gooffline.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Config {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "config_key")
    String configKey;
    @ColumnInfo(name = "config_value")
    String configValue;

    public Config(String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getConfigValue() {
        return configValue;
    }
}
