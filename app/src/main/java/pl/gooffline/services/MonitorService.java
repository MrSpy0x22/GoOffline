package pl.gooffline.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pl.gooffline.R;
import pl.gooffline.utils.UsageStatsUtil;

public class MonitorService extends Service {
    private boolean isStarted = false;
    private boolean isLocked = false;

    private List<String> monitoredPackageList = Collections.singletonList("com.google.android.apps.maps");;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View overlay;
    private Handler handler;

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
                checkCurrentActivityAndBlockIfNotAllowed();
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

        overlay.setOnClickListener(v -> {
            windowManager.removeView(overlay);
            isLocked = false;
            overlay = null;
        });

        if (!Settings.canDrawOverlays(this)) {
            Log.d("" , "You cannot draw!");
        } else {
            Log.d("" , "Well... you can draw! But do you?");
        }

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

        Notification notify = new Notification.Builder(this , channel.getId())
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_monitor_notification)
                .setContentTitle(getString(R.string.monitor_notification_header))
                .setContentText(getString(R.string.monitor_notification_text))
                .build();

        startForeground(500 , notify);
    }

    private void checkCurrentActivityAndBlockIfNotAllowed() {
        SortedMap<Long , String> usedPackages = UsageStatsUtil.getAllUsedActivities(this);
        String currentPackage = usedPackages.isEmpty() ? null : usedPackages.get(usedPackages.lastKey());
        boolean detected = false;

        if (currentPackage != null) {
            for (String packageToCheck : monitoredPackageList) {
                if (currentPackage.equals(packageToCheck)) {
                    detected = true;
                    break;
                }
            }

            if (detected && !isLocked) {
                isLocked = true;
                try {
                    ((WindowManager) MonitorService.this.getSystemService(WINDOW_SERVICE)).addView(overlay, layoutParams);
                } catch (Exception e) {
                    Log.d(this.getClass().getSimpleName() , "Złapano wyjątek podczas próby pokazania ekranu blokady.");
                }
            } else if (!detected && isLocked) {
                isLocked = false;
                try {
                    ((WindowManager) MonitorService.this.getSystemService(WINDOW_SERVICE)).removeView(overlay);
                } catch (Exception e) {
                    Log.d(this.getClass().getSimpleName() , "Złapano wyjątek podczas próby ukrycia ekranu blokady.");
                }

            }
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

        try {
            windowManager.removeView(overlay);
        } catch (Exception e) {
            Log.d(this.getClass().toString() , "WindowManager nie mógł usunąć widoku.");
        }
    }
}
