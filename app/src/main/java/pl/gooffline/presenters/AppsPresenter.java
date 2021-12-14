package pl.gooffline.presenters;

import android.content.Context;

import java.util.Arrays;

import pl.gooffline.utils.ConfigUtil;

public class AppsPresenter extends ConfigPresenter {
    /**
     * Interfejs widoku.
     */
    public interface View {
        void onRowOptionSelected(int layoutRowId);
    }

    public AppsPresenter(Context context) {
        super(context , Arrays.asList(
                ConfigUtil.KnownKeys.KK_WHITELIST_WHILE_SLEEPING.getKeyName() ,
                ConfigUtil.KnownKeys.KK_BLOCK_SETTINGS.getKeyName()));
    }
}
