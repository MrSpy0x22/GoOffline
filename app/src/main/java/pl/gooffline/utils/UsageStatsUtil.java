package pl.gooffline.utils;

import static android.content.Context.USAGE_STATS_SERVICE;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.annotations.NonNull;
import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.UsagesDao;
import pl.gooffline.database.entity.Usages;
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

    public static List<Usages> getDailyUsagesDataPoints(Context context , LocalDateTime usedDate) {
        UsagesDao usagesDao = AppDatabase.getInstance(context).usageDAO();

        return usagesDao.getAllByDay((int) usedDate.toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC));
    }

    public static List<Usages> getWeeklyUsagesDataPoints(Context context , LocalDateTime usedDate) {
        UsagesDao usagesDao = AppDatabase.getInstance(context).usageDAO();

        List<Usages> data = usagesDao.getAllBetween(
                (int) usedDate.toLocalDate().minus(7 , ChronoUnit.DAYS).atStartOfDay().toEpochSecond(ZoneOffset.UTC) ,
                (int) usedDate.toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC));

        return getMergedUsagesList(data);
    }

    public static List<Usages> getMonthlyUsagesDataPoints(Context context , LocalDateTime usedDate) {
        UsagesDao usagesDao = AppDatabase.getInstance(context).usageDAO();

        int start = (int) usedDate.minus(30 , ChronoUnit.DAYS).toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        int end = (int) usedDate.toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        return getMergedUsagesList(usagesDao.getAllBetween(start , end));
    }

    public static List<Usages> getYearUsagesDataPoints(Context context , LocalDateTime usedDate) {
        UsagesDao usagesDao = AppDatabase.getInstance(context).usageDAO();

        int start = (int) usedDate.minus(1 , ChronoUnit.YEARS).toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        int end = (int) usedDate.toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        return getMergedUsagesList(usagesDao.getAllBetween(start , end));
    }

    public static List<Usages> getMergedUsagesList(@NonNull List<Usages> data) {
        return data.stream()
                .collect(Collectors.groupingBy(Usages::getPackageName))
                .values()
                .stream()
                .map(u -> u
                        .stream()
                        .reduce((u1, u2) -> new Usages(u1.getPackageName(), u1.getDayTimestamp(), u1.getTotalSeconds() + u2.getTotalSeconds()))
                        .get())
                .collect(Collectors.toList());
    }
}
