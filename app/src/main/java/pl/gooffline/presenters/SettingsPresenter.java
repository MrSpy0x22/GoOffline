package pl.gooffline.presenters;

import android.content.Context;

import java.util.Collections;

import pl.gooffline.utils.ConfigUtil;

public class SettingsPresenter extends ConfigPresenter {
    public interface View {
        void onViewReady();
        void onServiceStateChanged(boolean state);
    }

    public SettingsPresenter(Context context) {
        super(context , Collections.singletonList(ConfigUtil.KnownKeys.KK_ACTIVE.getKeyName()));
    }
}
