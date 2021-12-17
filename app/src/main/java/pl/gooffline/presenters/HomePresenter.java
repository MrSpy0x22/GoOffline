package pl.gooffline.presenters;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.ConfigDao;

public class HomePresenter {

    public interface View {
        void onClickPolicyButton();
        void onClickGameButton();
        void onClickStatsButton();
        void onViewReady();
        void onViewUpdated();
    }

    private ConfigDao configDao;

    public HomePresenter(Context context) {
        this.configDao = AppDatabase.getInstance(context).configDAO();
    }

    public Map<String , String> pullConfigKeys(List<String> configKeys) {
        Map<String , String> configMap = new HashMap<>();

        for (String key : configKeys) {
            configMap.put(key , configDao.getConfigByKey(key).getConfigValue());
        }

        return configMap;
    }
}
