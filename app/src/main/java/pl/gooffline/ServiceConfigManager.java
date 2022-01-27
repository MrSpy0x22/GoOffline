package pl.gooffline;

import android.content.Context;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.ConfigDao;
import pl.gooffline.database.entity.Config;
import pl.gooffline.presenters.SleeptimePresenter;
import pl.gooffline.presenters.WhitelistPresenter;
import pl.gooffline.utils.ConfigUtil;

public final class ServiceConfigManager {
    /**
     * Obiekt tej klasy.
     */
    private static ServiceConfigManager instance;
    /**
     * Dozwolone pakiety.
     */
    private List<String> allowedPackages;

    //region Wartości konfiguracji
    /**
     * Stan serwisu.
     */
    private boolean serviceEnabled;
    /**
     * Limit czasu
     */
    private int dailyTimeTotal;
    private int dailyTimeLimit;
    private LocalDate dailyLimitDate;
    /**
     * Ustawienia
     */
    private boolean blockSettings;
    private boolean whitelistWhileSleeping;
    /**
     * Czas snu.
     */
    private boolean sleepTimeEnabled;
    private int sleepTimeStart;
    private int sleepTimeEnd;
    /**
     * Dodatkowy czas.
     */
    private boolean gameEnabled;
    private int gameBonusTime;
    private int gameAttempts;
    //endregion

    //region Singleton
    /**
     * Konstruktor.
     */
    private ServiceConfigManager() {

    }

    /**
     * Dostęp do instancji klasy..
     */
    public static ServiceConfigManager getInstance() {
        if (instance == null) {
            instance = new ServiceConfigManager();
        }

        return instance;
    }
    //endregion

    //region Getter-y
    public List<String> getAllowedPackages() {
        return allowedPackages;
    }

    public boolean isServiceEnabled() {
        return serviceEnabled;
    }

    public int getDailyTimeTotal() {
        return dailyTimeTotal;
    }

    public int getDailyTimeLimit() {
        return dailyTimeLimit;
    }

    public LocalDate getDailyLimitDate() {
        return dailyLimitDate;
    }

    public boolean isBlockSettings() {
        return blockSettings;
    }

    public boolean isWhitelistWhileSleeping() {
        return whitelistWhileSleeping;
    }

    public boolean isSleepTimeEnabled() {
        return sleepTimeEnabled;
    }

    public int getSleepTimeStart() {
        return sleepTimeStart;
    }

    public int getSleepTimeEnd() {
        return sleepTimeEnd;
    }

    public boolean isGameEnabled() {
        return gameEnabled;
    }

    public int getGameBonusTime() {
        return gameBonusTime;
    }

    public int getGameAttempts() {
        return gameAttempts;
    }

    //endregion

    //region Setter-y

    public void setAllowedPackages(List<String> allowedPackages) {
        this.allowedPackages = allowedPackages;
    }

    public void setServiceEnabled(boolean serviceEnabled) {
        this.serviceEnabled = serviceEnabled;
    }

    public void setDailyTimeTotal(int dailyTimeTotal) {
        this.dailyTimeTotal = dailyTimeTotal;
    }

    public void setDailyTimeLimit(int dailyTimeLimit) {
        this.dailyTimeLimit = dailyTimeLimit;
    }

    public void setDailyLimitDate(LocalDate dailyLimitDate) {
        this.dailyLimitDate = dailyLimitDate;
    }

    public void setBlockSettings(boolean blockSettings) {
        this.blockSettings = blockSettings;
    }

    public void setWhitelistWhileSleeping(boolean whitelistWhileSleeping) {
        this.whitelistWhileSleeping = whitelistWhileSleeping;
    }

    public void setSleepTimeEnabled(boolean sleepTimeEnabled) {
        this.sleepTimeEnabled = sleepTimeEnabled;
    }

    public void setSleepTimeStart(int sleepTimeStart) {
        this.sleepTimeStart = sleepTimeStart;
    }

    public void setSleepTimeEnd(int sleepTimeEnd) {
        this.sleepTimeEnd = sleepTimeEnd;
    }

    public void setGameEnabled(boolean gameEnabled) {
        this.gameEnabled = gameEnabled;
    }

    public void setGameBonusTime(int gameBonusTime) {
        this.gameBonusTime = gameBonusTime;
    }

    public void setGameAttempts(int gameAttempts) {
        this.gameAttempts = gameAttempts;
    }


    //endregion

    /**
     * Przetwarza listę danych konfiguracji i przetwarza je na dane z odpowiednimi typami.
     */
    public void updateServiceConfig(Context context , List<Config> configList) {

        for (Config c : configList) {
            // Stan serwisu
            if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_ACTIVE.getKeyName())) {
                    ServiceConfigManager.getInstance().setServiceEnabled(
                            c.getConfigValue().equals("1")
                    );
            }
            // Limit czasu użycia (max)
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_TIME_LIMIT_TOTAL.getKeyName())) {
                ServiceConfigManager.getInstance().setDailyTimeTotal(
                        Integer.parseInt(c.getConfigValue())
                );
            }
            // Limit czasu użycia
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_TIME_LIMIT_VALUE.getKeyName())) {
                ServiceConfigManager.getInstance().setDailyTimeLimit(
                        Integer.parseInt(c.getConfigValue())
                );
            }
            // Limit czasu użycia (data pomiaru)
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_TIME_LIMIT_TIMESTAMP.getKeyName())) {
                ServiceConfigManager.getInstance().setDailyLimitDate(
                        LocalDate.ofEpochDay(Long.parseLong(c.getConfigValue()))
                );
            }
            // Lista wyjątków aktywna w trybie snu
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_WHITELIST_WHILE_SLEEPING.getKeyName())) {
                ServiceConfigManager.getInstance().setWhitelistWhileSleeping(
                        c.getConfigValue().equals("1")
                );
            }
            // Blokowanie ustawień systemu
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_BLOCK_SETTINGS.getKeyName())) {
                ServiceConfigManager.getInstance().setBlockSettings(
                        c.getConfigValue().equals("1")
                );
            }
            // Czas snu
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_SLEEPTIME_ENABLE.getKeyName())) {
                ServiceConfigManager.getInstance().setServiceEnabled(
                        c.getConfigValue().equals("1")
                );
            }
            // Czas snu - start
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_SLEEPTIME_START.getKeyName())) {
                ServiceConfigManager.getInstance().setSleepTimeStart(
                        Integer.parseInt(c.getConfigValue())
                );
            }
            // Czas snu - stop
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_SLEEPTIME_STOP.getKeyName())) {
                ServiceConfigManager.getInstance().setSleepTimeEnd(
                        Integer.parseInt(c.getConfigValue())
                );
            }
            // Gra
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_GAME_ENABLE.getKeyName())) {
                ServiceConfigManager.getInstance().setGameEnabled(
                        c.getConfigValue().equals("1")
                );
            }
            // Gra - czas
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_GAME_BONUS_TIME.getKeyName())) {
                ServiceConfigManager.getInstance().setGameBonusTime(
                        Integer.parseInt(c.getConfigValue())
                );
            }
            // Gra - próby
            else if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_GAME_ATTEMPTS.getKeyName())) {
                ServiceConfigManager.getInstance().setGameBonusTime(
                        Integer.parseInt(c.getConfigValue())
                );
            }
        }
    }

    /**
     * Generuje listę obiektów typu <c>Config</c> na podstawie danych zawartych w tej klasie.
     * @return Lista typu <c>Config<c/>
     */
    public List<Config> getConfigList() {
        List<Config> result = new ArrayList<>();

        //region Stan serwisu
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_ACTIVE.getKeyName(),
                serviceEnabled ? "1" : "0"
        ));
        //endregion

        //region Limit czasu (limit)
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_TIME_LIMIT_TOTAL.getKeyName(),
                String.valueOf(dailyTimeTotal)
        ));
        //endregion

        //region Limit czasu (wartość)
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_TIME_LIMIT_VALUE.getKeyName(),
                String.valueOf(dailyTimeLimit)
        ));
        //endregion

        //region Limit czasu (dzień)
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_TIME_LIMIT_TIMESTAMP.getKeyName(),
                String.valueOf(dailyLimitDate.toEpochDay())
        ));
        //endregion

        //region Blokowanie ustawień
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_BLOCK_SETTINGS.getKeyName(),
                isBlockSettings() ? "1" : "0"
        ));
        //endregion

        //region Lista wyjątków w trybie snu
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_WHITELIST_WHILE_SLEEPING.getKeyName(),
                isWhitelistWhileSleeping() ? "1" : "0"
        ));
        //endregion

        //region Czas snu
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_SLEEPTIME_ENABLE.getKeyName(),
                isSleepTimeEnabled() ? "1" : "0"
        ));
        //endregion

        //region Czas snu - start
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_SLEEPTIME_START.getKeyName(),
                String.valueOf(getSleepTimeStart())
        ));
        //endregion

        //region Czas snu - stop
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_SLEEPTIME_STOP.getKeyName(),
                String.valueOf(getSleepTimeEnd())
        ));
        //endregion

        //region Gra
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_GAME_ENABLE.getKeyName(),
                isGameEnabled() ? "1" : "0"
        ));
        //endregion

        //region Gra - próby
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_GAME_ATTEMPTS.getKeyName(),
                String.valueOf(getGameAttempts())
        ));
        //endregion

        //region Gra - czas
        result.add(new Config(
                ConfigUtil.KnownKeys.KK_GAME_BONUS_TIME.getKeyName(),
                String.valueOf(getGameBonusTime())
        ));
        //endregion

        return result;
    }
}
