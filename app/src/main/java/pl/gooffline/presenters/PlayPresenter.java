package pl.gooffline.presenters;

import android.content.Context;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import pl.gooffline.ServiceConfigManager;
import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.WordbaseDao;
import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;
import pl.gooffline.utils.ConfigUtil;

public class PlayPresenter extends ConfigPresenter {
    /**
     * Interfejs widoku
     */
    public interface View {
        void onViewReady();
        void onLetterKeyPressed(char key , Button button);
        void onSpecialKeyPressed(boolean isButtonNewGame);
        void onComplete();
        void onAttemptsLimitReached();
    }

    public PlayPresenter(Context context) {
        super(context, Arrays.asList(
                ConfigUtil.KnownKeys.KK_PLAY_WORD_ATTEMPTS.getKeyName() ,
                ConfigUtil.KnownKeys.KK_PLAY_WORD_ID.getKeyName() ,
                ConfigUtil.KnownKeys.KK_PLAY_DAY.getKeyName()
        ));
    }

    private int getRand(int min , int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    private int getRandomWordId(Context context) {
        WordbaseDao wordbaseDao = AppDatabase.getInstance(context).wordbaseDAO();
        List<Wordbase> words = wordbaseDao.getAll();
        int largest = words.stream()
                .mapToInt(w -> (int) w.getWordId())
                .max().getAsInt();
        AtomicInteger rand = new AtomicInteger(getRand(1 , largest));

        while (!words.stream().anyMatch(w -> w.getWordId() == rand.get())) {
            rand.set(getRand(1, largest));
        }

        return rand.get();
    }

    public Map<Wordbase , Category> getWordFromId(Context context) {
        int wordId = getRandomWordId(context);

        WordbaseDao wordbaseDao = AppDatabase.getInstance(context).wordbaseDAO();
        Map<Wordbase , Category> result;

        if (wordId == -1) {
            result = wordbaseDao.getWordAndCategoryByWordId(
                    getRandomWordId(context)
            );

        } else {
            result = wordbaseDao.getWordAndCategoryByWordId(wordId);

            if (result == null) {
                result = wordbaseDao.getWordAndCategoryByWordId(
                        getRandomWordId(context)
                );
            }
        }

        ServiceConfigManager.getInstance().setPlayWordId(wordId);
        return result;
    }
}
