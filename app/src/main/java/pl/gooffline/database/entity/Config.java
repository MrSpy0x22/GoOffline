package pl.gooffline.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import pl.gooffline.utils.ConfigUtil;

@Entity(tableName = "config")
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

    public void setConfigKey(String value) {
        this.configValue = value;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getConfigValue() {
        if (configValue == null) {
            ConfigUtil.KnownKeys knownKey = ConfigUtil.KnownKeys.getKnownKey(configKey);
            return knownKey.getDefaultValue();
        }

        return configValue;
    }
}
