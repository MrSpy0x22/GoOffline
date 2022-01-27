package pl.gooffline;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.yabu.livechart.model.DataPoint;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.entity.Config;
import pl.gooffline.presenters.SleeptimePresenter;
import pl.gooffline.utils.ConfigUtil;
import pl.gooffline.utils.UsageStatsUtil;

@RunWith(AndroidJUnit4.class)
public class UsageStatsTest {
    private Context context;
    private UsageStatsManager statsManager;
    private Map<Integer , List<UsageStats>> dataDaily;

    @Before
    public void prepareTest() {
        context = ApplicationProvider.getApplicationContext();
        statsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        dataDaily = UsageStatsUtil.getDailyStats(context);
    }

    @After
    public void finishTest() {
    }


    @Test
    public void testQueryAndAgregateDailyStats() {
    }

    @Test
    public void testDailyUsages() {
        Assert.assertNotNull(dataDaily);
    }

    @Test
    public void testDataPoints() {
        List<DataPoint> dataPoints = UsageStatsUtil.convertStatsMapToDataPoints(dataDaily);
        Assert.assertNotNull(dataPoints);
    }
}
