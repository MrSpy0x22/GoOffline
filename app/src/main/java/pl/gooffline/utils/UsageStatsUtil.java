package pl.gooffline.utils;

import static android.content.Context.USAGE_STATS_SERVICE;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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
}
