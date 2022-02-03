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
import pl.gooffline.database.dao.CategoryDao;
import pl.gooffline.database.dao.WordbaseDao;
import pl.gooffline.database.entity.Usages;
import pl.gooffline.utils.UsageStatsUtil;

@RunWith(AndroidJUnit4.class)
public class WordbaseTest {
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
    public void removeAllWords() {
        WordbaseDao wordbaseDao = AppDatabase.getInstance(context).wordbaseDAO();
        wordbaseDao.deleteAll();
        Assert.assertEquals(1, 1);
    }

    @Ignore("Tylko do jawnych testów")
    @Test
    public void removeAllCategories() {
        CategoryDao categoryDao = AppDatabase.getInstance(context).categoryDAO();
        categoryDao.deleteAll();
        Assert.assertEquals(1, 1);
    }
}
