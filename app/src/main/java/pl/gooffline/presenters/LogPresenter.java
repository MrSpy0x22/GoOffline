package pl.gooffline.presenters;

import android.content.Context;

import java.time.LocalDateTime;
import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.LogDao;
import pl.gooffline.database.entity.Config;
import pl.gooffline.database.entity.Log;
import pl.gooffline.utils.ConfigUtil;

public class LogPresenter {
    public interface View {
        void onViewReady();
        void onLoggingEnabled();
        void onLoadingLogs();
        void onClickPreviousPage();
        void onClickPreviousnext();
        void onClickCalendar(LocalDateTime dateTime);
        void onClickDelete();
        void onClickOptions();
    }

    private LogDao logDao;

    public LogPresenter(Context context) {
        this.logDao = AppDatabase.getInstance(context).logDAO();
    }

    public List<Log> getAllBetween(long start , long stop) {
        return logDao.getBetween(start , stop);
    }

    public void deleteOlderThan(long timestamp) {
        logDao.deleteOlderThan(timestamp);
    }

    public void updateConfigEnabledInDatabase(Context context , boolean state) {
        AppDatabase.getInstance(context).configDAO().insertOrUpdateOne(
                new Config(
                        ConfigUtil.KnownKeys.KK_LOG_ENABLED.getKeyName(),
                        state ? "1" : "0"
                )
        );
    }

    public void updateConfigInDatabase(Context context , String key , String value) {
        AppDatabase.getInstance(context).configDAO().insertOrUpdateOne(
                new Config(key , value)
        );
    }

    public void deleteALl() {
        logDao.deleteAll();
    }
}
