package pl.gooffline.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.ConfigDao;
import pl.gooffline.database.entity.Config;

public class ConfigUtil {
    static final char TYPE_FLAG_INT        = 'i';
    static final char TYPE_FLAG_BOOL       = 'b';
    static final char TYPE_FLAG_FLOAT      = 'f';
    static final char TYPE_FLAG_STRING     = 's';

    /**
     * Zdefiniowane nazwy kluczy dla ustawień.
     */
    enum KnownKeys {
        // Ogólne
        KK_ACTIVE("service_enabled") ,

        // Aplikacje
        KK_BLOCK_SETTINGS("app_block_settings") ,
        KK_BLOCK_APP_SETTINGS("app_block_app_settings") ,
        KK_BLOCK_SHOPS("app_block_shops");

        private final String keyName;
        KnownKeys(String keyName) {
            this.keyName = keyName;
        }

        public String keyName() {
            return this.keyName;
        }

        public char getKeyValueType(String keyName) {
            if (keyName.equals(KK_ACTIVE.keyName)) { return TYPE_FLAG_INT; }
            else if (keyName.equals(KK_BLOCK_SETTINGS.keyName)) { return TYPE_FLAG_BOOL; }
            else if (keyName.equals(KK_BLOCK_APP_SETTINGS.keyName)) { return TYPE_FLAG_BOOL; }
            else if (keyName.equals(KK_BLOCK_SHOPS.keyName)) { return TYPE_FLAG_BOOL; }
            else { return 0; }
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
            if (configMap.containsKey(knownKeyName.keyName())) {
                return configMap.get(knownKeyName.keyName());
            }
        }
        return null;
    }
}
