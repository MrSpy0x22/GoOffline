package pl.gooffline.utils;

import static android.content.Context.USAGE_STATS_SERVICE;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.lists.AppList;

public class UsageStatsUtil {
    /**
     *  Sprawdza statystyki w celu pobrania listy używanych urządzeń.
     * @param context Kontekst.
     * @return Nazwy pakietów posortowaną według ostatniego użycia.
     */
    public static SortedMap<Long , String> getAllUsedActivities(Context context) {
        long time = System.currentTimeMillis();
        SortedMap<Long , String> sortedUsageList = new TreeMap<>();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY , time - 1000 * 1000 ,  time
        );

        if (!usageStatsList.isEmpty()) {
            for (UsageStats u : usageStatsList) {
                sortedUsageList.put(u.getLastTimeUsed() , u.getPackageName());
            }
        }

        return sortedUsageList;
    }
    /**
     * Odpytuje <c>PackageManager</c> i zwraca listę danych z zainstalowanymi aplikacjami.
     * @param context Kontekst.
     * @param whitelists Lista wyjątków.
     * @return Listę typu AppList.AppData.
     * @see pl.gooffline.lists.AppList.AppData
     */
    public static List<AppList.AppData> getAppDataList(Context context , List<Whitelist> whitelists) throws PackageManager.NameNotFoundException {
        List<AppList.AppData> dataList = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN , null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = context.getPackageManager();

        for (ResolveInfo app : packageManager.queryIntentActivities(intent , PackageManager.GET_META_DATA)) {
            if (app.activityInfo.packageName.equals(context.getPackageName())) {
                continue;
            }

            boolean isSelected = false;

            if (whitelists != null) {
                isSelected = whitelists.stream()
                        .filter(Whitelist::isIgnored)
                        .anyMatch(w -> w.getPackageName().equals(app.activityInfo.packageName));
            }

            dataList.add(new AppList.AppData(
                    app.loadIcon(packageManager) ,
                    app.loadLabel(packageManager).toString() ,
                    app.activityInfo.packageName ,
                    isSelected));
        }

        return dataList;
    }
}
