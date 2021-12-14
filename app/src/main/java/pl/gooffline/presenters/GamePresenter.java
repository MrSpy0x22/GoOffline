package pl.gooffline.presenters;

import android.content.Context;

import java.util.Arrays;

import pl.gooffline.utils.ConfigUtil;

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
}
