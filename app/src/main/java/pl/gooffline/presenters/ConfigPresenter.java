package pl.gooffline.presenters;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.ConfigDao;
import pl.gooffline.database.entity.Config;
import pl.gooffline.utils.ConfigUtil;

public abstract class ConfigPresenter implements EntityDataFlow<Config> {
    private final ConfigDao configDao;
    private List<Config> configList;

    public ConfigPresenter(Context context , List<String> handledKnownKeys) {
        this.configDao = AppDatabase.getInstance(context).configDAO();
        this.configList = configDao.getFrom(handledKnownKeys);

        if (this.configList == null) {
            configList = new ArrayList<>();
        }
    }

    @Override
    public void pullData() {
        configList = configDao.getFrom(
                this.configList.stream()
                        .map(Config::getConfigKey)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void pushData() {
        if (this.configList != null) {
            configDao.updateSelected(
                    this.configList.stream()
                            .map(Config::getConfigKey)
                            .collect(Collectors.toList())
            );
        }
    }

    @Override
    public void pushData(List<Config> dataList) {
        if (dataList != null) {
            configList = dataList;
            pushData();
        }
    }

    public String getConfigValue(ConfigUtil.KnownKeys keyName) {
        if (configList != null) {
            for (Config config : configList) {
                if (config.getConfigKey().equals(keyName.keyName())) {
                    return config.getConfigValue();
                }
            }
        } else {
            Log.d(this.getClass().toString() , "list = null");
        }

        return null;
    }

    public void setConfigValue(ConfigUtil.KnownKeys key , String value) {
        if (key != null & value != null) {
            Config config = new Config(key.keyName() , value);

            // Aktualizowanie elementu na liście lub dodanie nowego
            if (configList.contains(config)) {
                configList.get(configList.indexOf(config)).setConfigKey(value);
            } else {
                configList.add(config);
            }

            configDao.insertOrUpdateOne(config);
        }
    }

    public ConfigDao getDao() {
        return this.configDao;
    }

    public List<Config> getList() {
        return this.configList;
    }
}
