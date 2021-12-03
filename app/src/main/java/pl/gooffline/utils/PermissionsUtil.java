package pl.gooffline.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;

import java.util.HashMap;
import java.util.Map;

public class PermissionsUtil {

    public enum InnerPermissionName {
        CAN_DRAW_OVERALL ,
        CAN_IGNORE_BATTERY_OPTIMIZATION ,
        USAGE_STATS
    }

    public static void askForCanDrawOverlay(Context context) {
        // if (Settings.canDrawOverlays(context))
        Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION ,
                Uri.parse("package:" + context.getPackageName())
        );

        context.startActivity(intent);
    }

    public static void askForBatteryOptimization(Context context) {
        Map<InnerPermissionName , Boolean> permissions = getRequiredPermissionsStatus(context);

        Intent intent = new Intent(//Settings.ACTION_BATTERY_SAVER_SETTINGS);
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    public static void askForUsageStats(Context context) {
        // if (Settings.canDrawOverlays(context))
        Intent intent = new Intent(
                Settings.ACTION_USAGE_ACCESS_SETTINGS ,
                Uri.parse("package:" + context.getPackageName())
        );

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
        result.put(InnerPermissionName.USAGE_STATS , appOpResult == AppOpsManager.MODE_ALLOWED);

        return result;
    }

}

