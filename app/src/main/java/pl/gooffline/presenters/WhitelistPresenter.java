package pl.gooffline.presenters;

import android.content.Context;

import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.WhitelistDao;
import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.lists.AppList;

public class WhitelistPresenter {
    private Context context;
    private WhitelistDao whitelistDao;
    private List<AppList.AppData> appDataList;

    public WhitelistPresenter(Context context) {
        this.context = context;
        whitelistDao = AppDatabase.getInstance(context).whitelistDAO();
        appDataList = whitelistDao.getAll();
    }

    public void requireReload() {
        WhitelistDao w
    }

    private void updateWhitelist() {

    }

    private List<Whitelist> getWhiteList() {
        return this.appDataList;
    }

    public List<AppList.AppData> getWhitelistAsAppData(List<Whitelist> whitelists) {
        return this.appDataList;
    }

    interface View {
        void setWorkingStatusFlag(boolean statusFlag);
    }
}
