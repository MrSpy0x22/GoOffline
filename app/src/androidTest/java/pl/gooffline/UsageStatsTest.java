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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import pl.gooffline.database.entity.Usages;
import pl.gooffline.utils.UsageStatsUtil;

@RunWith(AndroidJUnit4.class)
public class UsageStatsTest {
    private Context context;

    @Before
    public void prepareTest() {
        context = ApplicationProvider.getApplicationContext();
    }

    @After
    public void finishTest() {
    }

    @Ignore("Tylko do jawnych testów")
    @Test
    public void getDailyData() {
        LocalDateTime now = LocalDateTime.now();
        List<Usages> usaged = UsageStatsUtil.getDailyUsagesDataPoints(context , now);
        Assert.assertNotNull(usaged);
    }

    @Ignore("Tylko do jawnych testów")
    @Test
    public void getMonthlyData() {
        LocalDateTime now = LocalDateTime.now();
        List<Usages> usaged = UsageStatsUtil.getMonthlyUsagesDataPoints(context , now);
        Assert.assertNotNull(usaged);
    }

    @Ignore("Tylko do jawnych testów")
    @Test
    public void getWeeklyData() {
        LocalDateTime now = LocalDateTime.now();
        List<Usages> usaged = UsageStatsUtil.getWeeklyUsagesDataPoints(context , now);
        Assert.assertNotNull(usaged);
    }

    @Ignore("Tylko do jawnych testów")
    @Test
    public void getYearlyData() {
        LocalDateTime now = LocalDateTime.now();
        List<Usages> usaged = UsageStatsUtil.getYearUsagesDataPoints(context , now);
        Assert.assertNotNull(usaged);
    }

    /**
     * Tworzenie przykładowej tablicy ze zduplikowanym <c>packageName</c> i porównanie sumy
     * <c>totalSeconds</c>, aby sprawdzić, czy w liście wynikowej dane zostały prawidłowo
     * scalone.
     */
    @Test
    public void getMergedUsagesList() {
        final String PACKAGE = "package1";
        final int USAGE_SEC = 1;
        List<Usages> testData = Arrays.asList(
            new Usages(PACKAGE , 0 , USAGE_SEC) ,
            new Usages("package2" , 0 , 1) ,
            new Usages("package3" , 0 , 1) ,
            new Usages(PACKAGE , 0 , USAGE_SEC)
        );

        testData = UsageStatsUtil.getMergedUsagesList(testData);

        Assert.assertEquals(USAGE_SEC * 2 , testData.stream()
                .filter(u -> u.getPackageName().equals(PACKAGE))
                .findAny()
                .get()
                .getTotalSeconds());
    }
}
