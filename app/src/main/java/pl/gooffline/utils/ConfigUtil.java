package pl.gooffline.utils;

import android.content.Context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.ConfigDao;
import pl.gooffline.database.entity.Config;

public class ConfigUtil {
    /**
     * Zdefiniowane nazwy kluczy dla ustawień.
     */
    public enum KnownKeys {
        KK_ACTIVE("service_enabled") ,
        KK_TIME_LIMIT_TOTAL("time_limit_total") ,
        KK_TIME_LIMIT_VALUE("time_limit_value") ,
        KK_TIME_LIMIT_TIMESTAMP("time_limit_timestamp") ,
        KK_WHITELIST_WHILE_SLEEPING("allow_whitelist_while_sleeping") ,
        KK_BLOCK_SETTINGS("app_block_settings") ,
        KK_SLEEPTIME_ENABLE("sleeptime_enabled") ,
        KK_SLEEPTIME_START("sleeptime_start") ,
        KK_SLEEPTIME_STOP("sleeptime_stop") ,
        KK_GAME_ENABLE("game_enable") ,
        KK_GAME_ATTEMPTS("game_attempts") ,
        KK_GAME_BONUS_TIME("game_bonus_time") ,
        KK_SEC_ADMIN_PASSWD("sec_admin_passwd") ,
        KK_SEC_ADMIN_CONTACT("sec_admin_contact");

        private final String keyName;

        KnownKeys(String keyName) {
            this.keyName = keyName;
        }

        public String getKeyName() {
            return this.keyName;
        }

        public KnownKeys getKnownKey() {
            return getKnownKey(this.keyName);
        }

        public static KnownKeys getKnownKey(String keyName) {
            KnownKeys result = null;

            if (keyName.equals(KK_TIME_LIMIT_TOTAL.getKeyName())) {
                result = KnownKeys.KK_TIME_LIMIT_TOTAL;
            } else if (keyName.equals(KK_TIME_LIMIT_VALUE.getKeyName())) {
                result = KnownKeys.KK_TIME_LIMIT_VALUE;
            } else if (keyName.equals(KK_TIME_LIMIT_TIMESTAMP.getKeyName())) {
                result = KnownKeys.KK_TIME_LIMIT_TIMESTAMP;
            } else if (keyName.equals(KK_WHITELIST_WHILE_SLEEPING.getKeyName())) {
                result = KnownKeys.KK_WHITELIST_WHILE_SLEEPING;
            } else if (keyName.equals(KK_BLOCK_SETTINGS.getKeyName())) {
                result = KnownKeys.KK_BLOCK_SETTINGS;
            } else if (keyName.equals(KK_SLEEPTIME_ENABLE.getKeyName())) {
                result = KnownKeys.KK_SLEEPTIME_ENABLE;
            } else if (keyName.equals(KK_SLEEPTIME_START.getKeyName())) {
                result = KnownKeys.KK_SLEEPTIME_START;
            } else if (keyName.equals(KK_SLEEPTIME_STOP.getKeyName())) {
                result = KnownKeys.KK_SLEEPTIME_STOP;
            } else if (keyName.equals(KK_GAME_ENABLE.getKeyName())) {
                result = KnownKeys.KK_GAME_ENABLE;
            } else if (keyName.equals(KK_GAME_ATTEMPTS.getKeyName())) {
                result = KnownKeys.KK_GAME_ATTEMPTS;
            } else if (keyName.equals(KK_GAME_BONUS_TIME.getKeyName())) {
                result = KnownKeys.KK_GAME_BONUS_TIME;
            } else if (keyName.equals(KK_SEC_ADMIN_PASSWD.getKeyName())) {
                result = KnownKeys.KK_SEC_ADMIN_PASSWD;
            } else if (keyName.equals(KK_SEC_ADMIN_CONTACT.getKeyName())) {
                result = KnownKeys.KK_SEC_ADMIN_CONTACT;
            }

            return result;
        }

        public String getDefaultValue() {
            String defaultVal = "";

            if (keyName.equals(KK_ACTIVE.getKeyName())) {
                defaultVal = "true";
            } else if (keyName.equals(KK_TIME_LIMIT_TOTAL.getKeyName())) {
                defaultVal = "50";
            } else if (keyName.equals(KK_TIME_LIMIT_VALUE.getKeyName())) {
                defaultVal = "50";
            } else if (keyName.equals(KK_TIME_LIMIT_TIMESTAMP.getKeyName())) {
                defaultVal = String.valueOf(LocalDate.now().toEpochDay());
            } else if (keyName.equals(KK_WHITELIST_WHILE_SLEEPING.getKeyName())) {
                defaultVal = "true";
            } else if (keyName.equals(KK_BLOCK_SETTINGS.getKeyName())) {
                defaultVal = "false";
            } else if (keyName.equals(KK_SLEEPTIME_ENABLE.getKeyName())) {
                defaultVal = "false";
            } else if (keyName.equals(KK_SLEEPTIME_START.getKeyName())) {
                defaultVal = "7";
            } else if (keyName.equals(KK_SLEEPTIME_STOP.getKeyName())) {
                defaultVal = "21";
            } else if (keyName.equals(KK_GAME_ENABLE.getKeyName())) {
                defaultVal = "false";
            } else if (keyName.equals(KK_GAME_ATTEMPTS.getKeyName())) {
                defaultVal = "2";
            } else if (keyName.equals(KK_GAME_BONUS_TIME.getKeyName())) {
                defaultVal = "5";
            }

            return defaultVal;
        }
    }

    private ConfigUtil configInstance;
    private Map<String , String> configMap;

    /**
     * Singleton - konstruktor jest prywatny.
     */
    private ConfigUtil() {

    }

    /**
     * Tworzy i/lub pobiera obiekt klasy.
     * @return Instancję tej klasy.
     */
    private ConfigUtil getInstance() {
        if (configInstance == null) {
            configInstance = new ConfigUtil();
        }

        return configInstance;
    }

    /**
     * Zapisuje konfigurację do bazy danych.
     * @param context Kontekst.
     * @param purge Flaga oznaczająca potrzebę wyczyszczenia bazy przed zapisaniem.
     * @return <c>TRUE</c> jeżeli fnkcja została wykonana, <c>FALSE</c> w przypadku błędu.
     */
    private boolean pushConfig(Context context , boolean purge) {
        if (configMap == null) {
            return false;
        }

        ConfigDao configDao = AppDatabase.getInstance(context).configDAO();

        if (purge) {
            configDao.deleteAll();
        }

        // Mapowanie do obiektu encji
        List<Config> configList = new ArrayList<>();
        configMap.forEach((k , v) -> {
            configList.add(new Config(k , v));
        });

        configDao.insertAll(configList);
        return true;
    }

    /**
     * Pobiera aktualną konfigurację z bazy danych.
     * @param context Kontekst.
     * @return <c>TRUE</c> jeżeli fnkcja została wykonana, <c>FALSE</c> w przypadku błędu.
     */
    private boolean pullConfig(Context context) {
        if (configMap == null) {
            return false;
        }

        ConfigDao configDao = AppDatabase.getInstance(context).configDAO();

        // Mapowanie do obiektu encji
        List<Config> configList = configDao.getAll();

        if (configList == null || configList.isEmpty()) {
            configMap = new HashMap<>();
            return false;
        } else {
            configMap = configList.stream().collect(Collectors.toMap(Config::getConfigKey , Config::getConfigValue));
        }

        return true;
    }

    /**
     * Sprawdza wartość klucza.
     * @param knownKeyName Nazwa klucza.
     * @return Wartość tekstowa klucza.
     */
    public String valueOf(KnownKeys knownKeyName) {
        if (configMap != null) {
            if (configMap.containsKey(knownKeyName.getKeyName())) {
                return configMap.get(knownKeyName.getKeyName());
            }
        }
        return null;
    }
}
