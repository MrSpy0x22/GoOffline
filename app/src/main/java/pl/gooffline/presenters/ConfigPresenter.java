package pl.gooffline.presenters;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.ConfigDao;
import pl.gooffline.database.entity.Config;

public abstract class ConfigPresenter implements EntityDataFlow<Config> {
    private final ConfigDao configDao;
    private List<Config> configList;

    public ConfigPresenter(Context context) {
        this.configDao = AppDatabase.getInstance(context).configDAO();
    }

    @Override
    public void pullData() {
        configList = configDao.getAll();
    }

    @Override
    public void pushData() {
        if (this.configList != null) {
            configDao.updateAll(this.configList);
        }
    }

    @Override
    public void pushData(List<Config> dataList) {
        if (dataList != null) {
            configList = dataList;
            pushData();
        }
    }

    public String getConfigValue(String keyName) {
        if (configList != null) {
            for (Config config : configList) {
                if (config.getConfigKey().equals(keyName)) {
                    return config.getConfigValue();
                }
            }
        } else {
            Log.d(this.getClass().toString() , "list = null");
        }

        return null;
    }

    public void setConfigValue(String key , String value) {
        if (key != null & value != null) {
            Config config = configList.stream()
                    .filter(c -> c.getConfigKey().equals(key))
                    .findFirst()
                    .orElse(new Config(key , value));
            configDao.insertOrUpdate(config);
        }
    }

    public ConfigDao getDao() {
        return this.configDao;
    }

    public List<Config> getList() {
        return this.configList;
    }
}
