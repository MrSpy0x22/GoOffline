package pl.gooffline.presenters;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.WhitelistDao;
import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.lists.AppList;
import pl.gooffline.utils.UsageStatsUtil;

public class WhitelistPresenter implements EntityDataFlow<Whitelist> {

    /**
     * Interfejs dla widoku.
     */
    public interface View {
        void onClickButtonUpdate();
        void onDataUpdated();
    }

    private final Context context;
    private final WhitelistDao whitelistDao;
    private List<Whitelist> whitelistApps;

    public WhitelistPresenter(Context context) {
        this.context = context;
        whitelistDao = AppDatabase.getInstance(context).whitelistDAO();

        pullData();
    }

    @Override
    public void pullData() {
        whitelistApps = whitelistDao.getAll();
    }

    @Override
    public void pushData() {
        if (this.whitelistApps != null) {
            whitelistDao.insertAll(this.whitelistApps);
        }
    }

    @Override
    public void pushData(List<Whitelist> dataList) {
        if (dataList != null) {
            whitelistApps = dataList;
            pushData();
        }
    }

    public List<String> getPackageNames() {
        List<String> result = new ArrayList<>();

        for (Whitelist whitelist : whitelistApps) {
            result.add(whitelist.getPackageName());
        }

        return result;
    }

    public List<AppList.AppData> getAsAppData() throws PackageManager.NameNotFoundException {
        return UsageStatsUtil.getAppDataList(context , whitelistDao.getAll());
    }
}
