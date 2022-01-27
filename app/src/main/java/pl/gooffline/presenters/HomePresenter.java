package pl.gooffline.presenters;

import android.content.Context;

import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.ConfigDao;
import pl.gooffline.database.entity.Config;

public class HomePresenter {
    /**
     * Interwejs widoku.
     */
    public interface View {
        void onClickPolicyButton();
        void onClickGameButton();
        void onClickStatsButton();
        void onViewReady();
        void onViewUpdated();
    }

    private final ConfigDao configDao;

    public HomePresenter(Context context) {
        this.configDao = AppDatabase.getInstance(context).configDAO();
    }

    public List<Config> pullConfigFrom(List<String> configKeys) {
        return configDao.getFrom(configKeys);
    }
}
