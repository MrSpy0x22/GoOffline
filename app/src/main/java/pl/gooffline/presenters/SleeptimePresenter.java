package pl.gooffline.presenters;

import android.content.Context;

import java.util.Arrays;

import pl.gooffline.utils.ConfigUtil;

public class SleeptimePresenter extends ConfigPresenter {
    /**
     * Interfejs widoku.
     */
    public interface View {
        void onSleeptimeStateChanged(boolean state);
        void onSleeptimeRangeChanged(int start , int stop);
    }

    /**
     * @param context Kontekst.
     */
    public SleeptimePresenter(Context context) {
        super(context , Arrays.asList(
                ConfigUtil.KnownKeys.KK_SLEEPTIME_ENABLE.getKeyName() ,
                ConfigUtil.KnownKeys.KK_SLEEPTIME_START.getKeyName() ,
                ConfigUtil.KnownKeys.KK_SLEEPTIME_STOP.getKeyName()
        ));
    }
}
