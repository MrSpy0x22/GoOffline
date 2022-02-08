package pl.gooffline;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;

import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.LogDao;
import pl.gooffline.database.dao.UsagesDao;
import pl.gooffline.database.entity.Log;
import pl.gooffline.database.entity.Usages;
import pl.gooffline.services.BroadcastActivity;
import pl.gooffline.services.BroadcastLogger;

public class Receiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // --- Autostart ---------------------------------------------------------------------------
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startActivity(new Intent(context , MainActivity.class));
            return;
        }
        // -----------------------------------------------------------------------------------------

        String broadcastName = intent.getStringExtra("broadcastName");
        String jsonData = intent.getStringExtra("jsonData");

        android.util.Log.d("Receiver" , broadcastName);

        if (broadcastName.equals(BroadcastLogger.class.toString())) {
            BroadcastLogger broadcastLogger;

            try {
                broadcastLogger = new BroadcastLogger(jsonData);

                Log log = new Log(broadcastLogger.getType() , broadcastLogger.getMessage(), broadcastLogger.getTime().getEpochSecond());
                logEvent(context , log);
            } catch (JSONException e) {
                android.util.Log.d(getClass().toString() , String.format("Błąd serializacji do [%s] danych: %s" ,
                        BroadcastLogger.class , jsonData));
            }
        } else if (broadcastName.equals(BroadcastActivity.class.toString())) {
            BroadcastActivity broadcastActivity;

            try {
                broadcastActivity = new BroadcastActivity(jsonData);
                Usages usage = saveUsageRecord(context, broadcastActivity);
            } catch (JSONException e) {
                android.util.Log.d(getClass().toString() , String.format("Błąd serializacji do [%s] danych: %s" ,
                        BroadcastLogger.class , jsonData));
            }
        }
    }

    private void logEvent(Context context , Log event) {
        // Logowanie tylko z włączoną opcją
        if (ServiceConfigManager.getInstance().isLogEnabled()) {
            BroadcastLogger.LogType type = BroadcastLogger.LogType.getByNumber(event.getLog_type());
            LogDao logDao = AppDatabase.getInstance(context).logDAO();


            ServiceConfigManager sc = ServiceConfigManager.getInstance();

            if (type == BroadcastLogger.LogType.LT_AUTH && ServiceConfigManager.getInstance().isLogEventAuths()) {
                logDao.insertEvent(event);
            } else if (type == BroadcastLogger.LogType.LT_LOCK && ServiceConfigManager.getInstance().isLogEventBlocks()) {
                logDao.insertEvent(event);
            } else if (type == BroadcastLogger.LogType.LT_LIMIT && ServiceConfigManager.getInstance().isLogEventLimits()) {
                logDao.insertEvent(event);
            }
        }
    }

    private Usages saveUsageRecord(Context context , BroadcastActivity broadcastedActivity) {
        UsagesDao usagesDao = AppDatabase.getInstance(context).usageDAO();
        String packageName = broadcastedActivity.getPackageName();
        int dateStamp = (int) broadcastedActivity.getMeasureTimeStart().toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        // Obliczanie otrzymanego czasu użycia
        long amount = ChronoUnit.SECONDS.between(
                 broadcastedActivity.getMeasureTimeStart() , broadcastedActivity.getMeasureTimeStop()
        );

        ServiceConfigManager.getInstance().updateTimeLimit((int) amount);

        // Pobieranie istniejącego obiektu z bazy
        Usages dailyPackageUsage = usagesDao.getSpecific(
                broadcastedActivity.getPackageName(),
                dateStamp
        );

        // Tworzenie obiektu jeśli nie istnieje lub aktualizacja
        if (dailyPackageUsage == null) {
            dailyPackageUsage = new Usages(packageName , dateStamp , (int) amount);
            usagesDao.insertNew(dailyPackageUsage);
        } else {
            dailyPackageUsage.setTotalSeconds(dailyPackageUsage.getTotalSeconds() + ((int) amount));
            usagesDao.updateExisting(dailyPackageUsage);
        }

        return dailyPackageUsage;
    }
}
