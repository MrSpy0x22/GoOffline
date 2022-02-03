package pl.gooffline.utils;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
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
        CAN_USE_STATS ,
    }

    /**
     * Zapytanie o rysowanie nad innymi oknami.
     * @param context
     */
    public static void askForCanDrawOverlay(Context context) {
        // if (Settings.canDrawOverlays(context))
        Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION ,
                Uri.parse("package:" + context.getPackageName())
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    /**
     * Zapytanie o wyłączenie optymalizacji baterii.
     * @param context
     */
    public static void askForBatteryOptimization(Context context) {
        Map<InnerPermissionName , Boolean> permissions = getRequiredPermissionsStatus(context);

        Intent intent = new Intent(//Settings.ACTION_BATTERY_SAVER_SETTINGS);
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Zapytanie o dostęp do danych.
     * @param context
     */
    public static void askForUsageStats(Context context) {
        Intent intent;
        try {
            intent = new Intent(
                    //Settings.ACTION_DATA_USAGE_SETTINGS,
                    Settings.ACTION_USAGE_ACCESS_SETTINGS,
                    Uri.parse("package:" + context.getPackageName())
            );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            /*
             * Wygląda na to, że nie w każdym telefonie można wystartować
             * Settings.ACTION_USAGE_ACCESS_SETTINGS. W przypadku błędu zostanie
             * pokazany ekran z informacją oraz otwarte uprawnienia.
             */
            new AlertDialog.Builder(context)
                    .setTitle("Dostęp do danych")
                    .setMessage("W ustawieniach aplikacji wybierz specjalne uprawnienia, a następnie dostęp do użytkowania.")
                    .setPositiveButton("OK", (dialogInterface, i) -> context.startActivity(new Intent(Settings.ACTION_SETTINGS)))
                    .create().show();
        }
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

