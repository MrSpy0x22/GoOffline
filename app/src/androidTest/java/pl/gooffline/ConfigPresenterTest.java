package pl.gooffline;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.entity.Config;
import pl.gooffline.presenters.AppsPresenter;
import pl.gooffline.presenters.SleeptimePresenter;
import pl.gooffline.utils.ConfigUtil;

@RunWith(AndroidJUnit4.class)
public class ConfigPresenterTest {
    private AppsPresenter appsPresenter;
    private SleeptimePresenter sleepPresenter;
    private Context context;

    @Before
    public void prepareTest() {
        context = ApplicationProvider.getApplicationContext();
        appsPresenter = new AppsPresenter(context);
        appsPresenter.pushData();
        sleepPresenter = new SleeptimePresenter(context);
        sleepPresenter.pushData();
    }

    @After
    public void finishTest() {
        AppDatabase appDB = AppDatabase.getInstance(context);
        if (appDB != null) {
            appDB.close();
        }
    }

    @Test
    public void testIfConfigPresenterHasOnlyOwnKnownKeysInList() {
        List<Config> configList = sleepPresenter.getList();

        int num = 0;
        for (Config c : configList) {
            if (c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_SLEEPTIME_ENABLE.getKeyName()) ||
                    c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_SLEEPTIME_START.getKeyName()) ||
                    c.getConfigKey().equals(ConfigUtil.KnownKeys.KK_SLEEPTIME_STOP.getKeyName())) {
                num++;
            }
        }

        Assert.assertEquals(3 , num);
    }
}
