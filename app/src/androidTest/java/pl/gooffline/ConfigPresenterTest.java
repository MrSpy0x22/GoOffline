package pl.gooffline;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.ConfigDao;
import pl.gooffline.database.entity.Config;
import pl.gooffline.lists.UsagesList;
import pl.gooffline.presenters.SleeptimePresenter;
import pl.gooffline.utils.ConfigUtil;

@RunWith(AndroidJUnit4.class)
public class ConfigPresenterTest {
    private SleeptimePresenter sleepPresenter;
    private Context context;

    /**
     * Pobieranie zawartości konfiguracji dla wybranego prezentera.
     */
    @Before
    public void prepareTest() {
        context = ApplicationProvider.getApplicationContext();
        sleepPresenter = new SleeptimePresenter(context);
    }

    /**
     * Zamykanie połączenia z bazą danych.
     */
    @After
    public void finishTest() {
        AppDatabase appDB = AppDatabase.getInstance(context);
        if (appDB != null) {
            appDB.close();
        }
    }

    /**
     * Przechodzenie przez listę ze znanymi kluczami konfiguracji. Sprawdzenie kilku wybranych
     *  oraz porównanie liczby z oczekiwaniami.
     */
    @Test
    public void testIfConfigPresenterHasOnlyOwnKnownKeysInList() {
        final int expected = 3;
        List<Config> configList = sleepPresenter.getList();

        int num = 0;
        for (Config c : configList) {
            if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_SLEEPTIME_ENABLE.getKeyName()) ||
                    c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_SLEEPTIME_START.getKeyName()) ||
                    c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_SLEEPTIME_STOP.getKeyName())) {
                num++;
            }
        }

        Assert.assertEquals(expected , num);
    }

    /**
     * Reset wybranych kluczy w bazie danych.
     */
    @Ignore("Tylko do jawnych testów!")
    @Test
    public void resetConfigKeysTest() {
        ConfigDao configDao = AppDatabase.getInstance(context).configDAO();

        List<ConfigUtil.KnownKeys> keys = Arrays.asList(
                ConfigUtil.KnownKeys.KK_TIME_LIMIT_TOTAL ,
                ConfigUtil.KnownKeys.KK_PLAY_WORD_ATTEMPTS
        );

        // Ustawianie domyślnej wartości
        for (ConfigUtil.KnownKeys key : keys) {
            configDao.insertOrUpdateOne(new Config(
                    key.getKeyName() , key.getDefaultValue()
            ));
        }
    }
}
