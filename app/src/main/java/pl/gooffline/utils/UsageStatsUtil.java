package pl.gooffline.utils;

import static android.content.Context.USAGE_STATS_SERVICE;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.yabu.livechart.model.DataPoint;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
    public static List<AppList.AppData> getAppDataList(Context context , List<Whitelist> whitelists) {
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

    private static Map<String , UsageStats> getStatsAgregated(Context context , long start , long stop) {
        UsageStatsManager statsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

        // Lista pakietów, którą zostanie przefiltrowana mapa statystyk
        List<String> packages = getAppDataList(context , null).stream()
                .map(AppList.AppData::getPackageName)
                .collect(Collectors.toList());

        return statsManager.queryAndAggregateUsageStats(start , stop).entrySet().stream()
                .filter(k -> packages.contains(k.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey , Map.Entry::getValue));
    }

    private static List<UsageStats> getStats(Context context , int intervalType , long start , long stop) {
        UsageStatsManager statsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

        // Lista pakietów, którą zostanie przefiltrowana mapa statystyk
        List<String> packages = getAppDataList(context , null).stream()
                .map(AppList.AppData::getPackageName)
                .collect(Collectors.toList());

        return statsManager.queryUsageStats(intervalType , start , stop).stream()
                .filter(u -> packages.contains(u.getPackageName()))
                .collect(Collectors.toList());
    }

    public static Map<Integer , List<UsageStats>> getDailyStats(Context context) {
        UsageStatsManager statsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);
        LocalDateTime day = LocalDateTime.of(LocalDate.now() , LocalTime.MIDNIGHT);
        Map<Integer , List<UsageStats>> resultMap = new HashMap<>();
        List<String> monitoredPackages = UsageStatsUtil.getAppDataList(context , null).stream().map(ad -> ad.getPackageName()).collect(Collectors.toList());

        for (int i = 1 ; i <= 23 ; i++ ) {
            LocalDateTime stopTime = LocalTime.MIN.atDate(day.toLocalDate()).plusHours(i);
            LocalDateTime startTime = stopTime.minusHours(1);
            List<UsageStats> hourStats = statsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY ,
                    startTime.toInstant(ZoneOffset.UTC).toEpochMilli() ,
                    stopTime.toInstant(ZoneOffset.UTC).toEpochMilli()
            ).stream().filter(us -> monitoredPackages.contains(us.getPackageName())).collect(Collectors.toList());

            resultMap.put(i , hourStats);
        }

        return resultMap;
    }

    public static List<DataPoint> convertStatsMapToDataPoints(Map<Integer , List<UsageStats>> map) {
        List<DataPoint> dataPoints = new ArrayList<>();

        for (Map.Entry<Integer , List<UsageStats>> item : map.entrySet()) {
            long max = item.getValue().stream()
                    .mapToLong(UsageStats::getTotalTimeInForeground)
                    .sum();

            dataPoints.add(new DataPoint((float) item.getKey() , (float) (max / 1000)));
        }

        return dataPoints;
    }
}
