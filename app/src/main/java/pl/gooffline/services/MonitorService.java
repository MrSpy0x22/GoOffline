package pl.gooffline.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.SortedMap;

import pl.gooffline.MainActivity;
import pl.gooffline.R;
import pl.gooffline.ServiceConfigManager;
import pl.gooffline.utils.UsageStatsUtil;

public class MonitorService extends Service {
    /**
     * Wartości stanu blokady ekranu.
     */
    private enum MonitorState {
        MS_NOT_DETECTED , MS_DETECTED
    }
    /**
     * Stan blokady ekranu
     */
    private MonitorState serviceMonitorState = MonitorState.MS_NOT_DETECTED;
    /**
     * Obiekty potrzebne dla ekranu blokady.
     */
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View overlay;
    private String detectedPackage = "";

    private LocalDateTime activitiMeasureTime = null;
    private BroadcastActivity broadcastActivity = null;

    // Pomiar aktywności


    private Handler handler;

    private void updateScreenReasonText(boolean sleepTimeEnabled) {
        if (overlay != null) {
            TextView caption = overlay.findViewById(R.id.overlay_caption);
            TextView text = overlay.findViewById(R.id.overlay_text);

            if (sleepTimeEnabled) {
                caption.setText("Czas snu");
                text.setText("Czas snu jest aktywny od "
                        + ServiceConfigManager.getInstance().getSleepTimeStart()
                        + ":00 do "
                        + ServiceConfigManager.getInstance().getSleepTimeEnd()
                        + ":00. W tym czasie nie możesz używać tej aplikacji.");
            } else {
                caption.setText("Blokada aplikacji");
                text.setText("Zgodnie z polityką administratora urządzenia używanie tej aplikacji nie jest już dzisiaj możliwe.");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                runMonitor();
                handler.postDelayed(this , 500);
            }
        };
        handler.post(r);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // use your custom view here
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);

        overlay = LayoutInflater.from(this).inflate(R.layout.unlock_screen , null);
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = dm.widthPixels;
        layoutParams.height = dm.heightPixels;
        overlay.setFitsSystemWindows(false);

        // add the view to window manger
        //windowManager.addView(overlay, layoutParams);

//        overlay.setOnClickListener(v -> {
//            windowManager.removeView(overlay);
//            isLocked = false;
//            overlay = null;
//        });

        startInForegroundWithNotification();

        return START_STICKY;
    }

    private void startInForegroundWithNotification() {
        Intent notificationIntent = new Intent(this , MonitorService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0 ,
                notificationIntent , 0);

        NotificationChannel channel = new NotificationChannel(
                getString(R.string.services_channel_id) ,
                getString(R.string.services_channel_name) ,
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager notifyMan = getSystemService(NotificationManager.class);
        notifyMan.createNotificationChannel(channel);

        Notification notify = new Notification.Builder(this , MainActivity.NOTIFICATION_CHANNEL_SERVICE)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_monitor_notification)
                .setContentTitle(getString(R.string.monitor_notification_header))
                .setContentText(getString(R.string.monitor_notification_text))
                .build();

        startForeground(500 , notify);
    }

    private void runMonitor() {
        if (!ServiceConfigManager.getInstance().isServiceEnabled()) {
            if (broadcastActivity != null) {
                broadcastActivity = null;
            }

            if (!detectedPackage.isEmpty()) {
                detectedPackage = "";
            }
            return;
        }

        SortedMap<Long , String> usedPackages = UsageStatsUtil.getAllUsedActivities(this);
        String currentPackage = usedPackages.isEmpty() ? null : usedPackages.get(usedPackages.lastKey());

        if (currentPackage == null) {
            return;
        }

        if (!currentPackage.equals(detectedPackage)) {
            Log.d("PackageDetector", currentPackage);
        }

        String previousPackage = detectedPackage;
        List<String> whitelistedPackages = ServiceConfigManager.getInstance().getAllowedPackages();
        LocalDateTime time = LocalDateTime.now();

        //region Pomiar aktywności

        // Broadcast activity
        if (activitiMeasureTime == null) {
            activitiMeasureTime = time;
        }

        // Jeżeli obiekt dla pomiarów nie jest jeszcze utworzony
        if (broadcastActivity == null) {
            broadcastActivity = new BroadcastActivity(currentPackage, activitiMeasureTime, activitiMeasureTime);
        }
        // Resetowanie obiektu pomiaru i wysyłanie broadcast-u
        else if (!broadcastActivity.getPackageName().equals(currentPackage)) {
            broadcastActivity.setMeasureTimeStop(LocalDateTime.now());

            // Nadawanie wiadomości tylko jeżeli pakiet nie jest spacjalnym wyjątkiem
            if (!detectedPackage.equals(ServiceConfigManager.getInstance().getLauncherPackageName()) &&
                !detectedPackage.equals(ServiceConfigManager.getInstance().getThisPackageName())) {
                BroadcastActivity.broadcastEvent(this ,
                        broadcastActivity.getPackageName() ,
                        broadcastActivity.getMeasureTimeStart() ,
                        time
                );
            }

            activitiMeasureTime = null;
            broadcastActivity = null;
        }
        // Jeżeli ta sama aplikacja używana jest przez 60 sekund
        else if (ChronoUnit.SECONDS.between(broadcastActivity.getMeasureTimeStart(), time) >= 10) {
            // Nadawanie wiadomości tylko jeżeli pakiet nie jest spacjalnym wyjątkiem
            if (!detectedPackage.equals(ServiceConfigManager.getInstance().getLauncherPackageName()) &&
                    !detectedPackage.equals(ServiceConfigManager.getInstance().getThisPackageName())) {
                BroadcastActivity.broadcastEvent(this,
                        broadcastActivity.getPackageName(),
                        broadcastActivity.getMeasureTimeStart(),
                        time
                );
            }

            broadcastActivity.setMeasureTimeStart(time);
            broadcastActivity.setMeasureTimeStop(null);
        }
        //endregion

        detectedPackage = currentPackage;

        // jeżeli któraś z kolekcji jest nullem to serwis nie powinien wejść w pętlę.
        if (whitelistedPackages != null) {
            boolean isInActiveTimeRange = time.getHour() >= ServiceConfigManager.getInstance().getSleepTimeStart()
                    &&  time.getHour() <= ServiceConfigManager.getInstance().getSleepTimeEnd();
            boolean isSleepTimeActive = ServiceConfigManager.getInstance().isSleepTimeEnabled() && !isInActiveTimeRange;
            boolean isWhitelistActiveWhileSleeping = ServiceConfigManager.getInstance().isWhitelistWhileSleeping();
            boolean isCurrentPackageWhitelisted = whitelistedPackages.contains(currentPackage);
            boolean isCurrentPackageNameException = currentPackage.equals(ServiceConfigManager.getInstance().getThisPackageName())
                    || currentPackage.equals(ServiceConfigManager.getInstance().getLauncherPackageName())
                    || currentPackage.equals("android") || currentPackage.equals("com.android.systemui");
            boolean isTimeLimitExceeded = ServiceConfigManager.getInstance().getDailyTimeTotal() -
                    ServiceConfigManager.getInstance().getDailyTimeLimit() <= 0;

            if (!isCurrentPackageNameException) {
                if (isSleepTimeActive && (!isCurrentPackageWhitelisted ||
                        (isCurrentPackageWhitelisted && !isWhitelistActiveWhileSleeping))) {
                    if (!previousPackage.equals(currentPackage)) {
                        Log.d(this.getClass().toString(), "runMonitor() -> " + currentPackage);

                        BroadcastLogger.broadcastEvent(this, BroadcastLogger.LogType.LT_LIMIT,
                                "Czas snu dla '" + currentPackage + "'", Instant.now()
                        );

                        showOverlay(isSleepTimeActive);
                    }
                } else if (!isSleepTimeActive && !isCurrentPackageWhitelisted && isTimeLimitExceeded) {
                    if (!previousPackage.equals(currentPackage)) {
                        Log.d(this.getClass().toString(), "runMonitor() -> " + currentPackage);

                        BroadcastLogger.broadcastEvent(this, BroadcastLogger.LogType.LT_LOCK,
                                "Blokada '" + currentPackage + "'", Instant.now()
                        );

                        showOverlay(isSleepTimeActive);
                    }
                } else {
                    hideOverlay();
                }
            } else {
                hideOverlay();
            }

//            List<String> packages = usedPackages.entrySet().stream()
//                    .map(Map.Entry::getValue)
//                    .collect(Collectors.toList());
//
//            // Porównywanie używanych aplikacji z listą wyjątku
//            for (String p : packages) {
//                // Jeżeli pakiet nie jest wyjątkiem...
//                if (!whitelistedPackages.contains(p)) {
//                    // jeżeli pakiet został już wykryty...
//                    if (!detectedPackage.equals(p)) {
//                        Log.d(this.getClass().toString() , "runMonitor() -> " + currentPackage);
//
//                        BroadcastLogger.broadcastEvent(this , BroadcastLogger.LogType.LT_LOCK ,
//                                "Blokada '" + detected  + "'" , Instant.now()
//                        );
//                    }
//                    detectedPackage = detected = p;
//                    break;
//                }
//            }

            // Pokazywanie lub ukrywanie ekranu
//            if (!detected.isEmpty()) {
//                showOverlay();
//            } else {
//                hideOverlay();
//            }hideOverlay
        }
    }

    /**
     * Pokazuje ekran blokady i ustawia odpowiednią flagę.
     */
    public void showOverlay(boolean sleepTimeEnabled) {
        if (serviceMonitorState == MonitorState.MS_DETECTED) {
            return;
        }

        updateScreenReasonText(sleepTimeEnabled);

        try {
            ((WindowManager) MonitorService.this.getSystemService(WINDOW_SERVICE)).addView(overlay, layoutParams);
            serviceMonitorState = MonitorState.MS_DETECTED;
        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName() , "Złapano wyjątek podczas próby pokazania ekranu blokady:\n" + e.getMessage());
        }
    }

    /**
     * Ukrywa ekran blokady (jeżeli jest widoczny) i ustawia odpowiednią flagę stanu.
     */
    private void hideOverlay() {
        if (serviceMonitorState == MonitorState.MS_NOT_DETECTED) {
            return;
        }

        try {
            ((WindowManager) MonitorService.this.getSystemService(WINDOW_SERVICE)).removeView(overlay);
            serviceMonitorState = MonitorState.MS_NOT_DETECTED;
        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName() , "Złapano wyjątek podczas próby ukrycia ekranu blokady:\n" + e.getMessage());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(getClass().toString() , "onDestroy()");

        try {
            windowManager.removeView(overlay);
        } catch (Exception e) {
            Log.d(this.getClass().toString() , "WindowManager nie mógł usunąć widoku.");
        }
    }
}
