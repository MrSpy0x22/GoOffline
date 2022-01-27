package pl.gooffline.presenters;

import android.content.Context;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.LogDao;
import pl.gooffline.database.entity.Log;

public class LogPresenter {
    private LogDao logDao;

    public LogPresenter(Context context) {
        this.logDao = AppDatabase.getInstance(context).logDAO();
    }

    // TODO: sprawdzić ustawienia eventów
    public void addEvent(Log log) {
        logDao.insertEvent(log);
    }
}
