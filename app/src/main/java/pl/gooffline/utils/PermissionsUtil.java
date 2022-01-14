package pl.gooffline.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.Map;

public class PermissionsUtil {

    public enum InnerPermissionName {
        CAN_DRAW_OVERALL ,
        CAN_IGNORE_BATTERY_OPTIMIZATION ,
        CAN_USE_STATS ,
    }

    public static void askForCanDrawOverlay(Context context) {
        // if (Settings.canDrawOverlays(context))
        Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION ,
                Uri.parse("package:" + context.getPackageName())
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    public static void askForBatteryOptimization(Context context) {
        Map<InnerPermissionName , Boolean> permissions = getRequiredPermissionsStatus(context);

        Intent intent = new Intent(//Settings.ACTION_BATTERY_SAVER_SETTINGS);
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void askForUsageStats(Context context) {
        Intent intent = new Intent(
                //Settings.ACTION_DATA_USAGE_SETTINGS,
                Settings.ACTION_USAGE_ACCESS_SETTINGS,
                Uri.parse("package:" + context.getPackageName())
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    /**
     * Sprawdza czy aplikacja posiada wymagane uprawnienia.
     * @param context Kontekst.
     * @return Mapę uprawnień (nazwy używane wewnątrz kodu) oraz ich stan.
     * @see InnerPermissionName
     */
    public static Map<InnerPermissionName , Boolean>  getRequiredPermissionsStatus(Context context) {
        Map<InnerPermissionName , Boolean> result = new HashMap<>();

        // Rysowanie nad innym iaplikacjami
        result.put(InnerPermissionName.CAN_DRAW_OVERALL , Settings.canDrawOverlays(context));

        // Ignorowanie oszczędzania baterii
        PowerManager powerMan = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        result.put(InnerPermissionName.CAN_IGNORE_BATTERY_OPTIMIZATION ,
                powerMan.isIgnoringBatteryOptimizations(context.getPackageName()));

        // Sprawdzanie danych dotyczących używanai aplikacji
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int appOpResult = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS ,
                android.os.Process.myUid() , context.getPackageName());
        result.put(InnerPermissionName.CAN_USE_STATS, appOpResult == AppOpsManager.MODE_ALLOWED);

        return result;
    }

}

