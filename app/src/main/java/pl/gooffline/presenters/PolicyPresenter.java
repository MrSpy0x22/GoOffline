package pl.gooffline.presenters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.WhitelistDao;
import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.lists.AppList;
import pl.gooffline.utils.UsageStatsUtil;

public class PolicyPresenter {
    private WhitelistDao whitelistDao;

    public PolicyPresenter(Context context) {
        this.whitelistDao = AppDatabase.getInstance(context).whitelistDAO();
    }

    public List<AppList.AppData> getAsAppData(Context context) throws PackageManager.NameNotFoundException {
        List<AppList.AppData> dataList = new ArrayList<>();
        List<Whitelist> whitelists = whitelistDao.getAll();
        PackageManager packageManager = context.getPackageManager();

        for (Whitelist w : whitelists) {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(w.getPackageName() , 0);

            dataList.add(new AppList.AppData(
                    appInfo.loadIcon(packageManager),
                    packageManager.getApplicationLabel(appInfo).toString() ,
                    w.getPackageName() ,
                    false));
        }

        return dataList;
    }
}
