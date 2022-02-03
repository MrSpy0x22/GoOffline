package pl.gooffline.presenters;

import android.content.Context;

import java.util.Arrays;
import java.util.Map;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.CategoryDao;
import pl.gooffline.database.dao.WordbaseDao;
import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;
import pl.gooffline.utils.ConfigUtil;
import pl.gooffline.utils.DataUtil;

public class GamePresenter extends ConfigPresenter {
    /**
     * Interfejs widoku.
     */
    public interface View {
        void onViewReady();
        void onGameEnabled(boolean newState);
        void onWordsOptionClick();
        void onAttemptsSliderUpdated();
        void onBonusTimeSliderUpdated();
    }

    public GamePresenter(Context context) {
        super(context, Arrays.asList(
                ConfigUtil.KnownKeys.KK_GAME_ENABLE.getKeyName() ,
                ConfigUtil.KnownKeys.KK_GAME_ATTEMPTS.getKeyName() ,
                ConfigUtil.KnownKeys.KK_GAME_BONUS_TIME.getKeyName()
        ));
    }

    public void addPreparedData(Context context) {
        WordbaseDao wordbaseDao = AppDatabase.getInstance(context).wordbaseDAO();
        CategoryDao categoryDao = AppDatabase.getInstance(context).categoryDAO();

        for (Map.Entry<String , String> wc : DataUtil.getDefaultWordsWithCategory().entrySet()) {
            Category c = new Category(wc.getValue());
            Wordbase w = new Wordbase(wc.getKey(), 0);

            categoryDao.insert(c);
            c = categoryDao.getByName(wc.getValue());

            w.setWordCategoryId(c.getCategoryId());
            wordbaseDao.insert(w);
        }
    }
}
