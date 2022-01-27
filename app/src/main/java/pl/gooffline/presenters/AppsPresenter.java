package pl.gooffline.presenters;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.WhitelistDao;
import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.utils.ConfigUtil;

public class AppsPresenter extends ConfigPresenter {
    /**
     * Interfejs widoku.
     */
    public interface View {
        void onViewReady();
        void onRowOptionSelected(int layoutRowId);
        void onTimeSliderUpdated();
        void onAppsInSleeptimeUpdated();
        void onBlockSettingsUpdated();
    }

    public AppsPresenter(Context context) {
        super(context , Arrays.asList(
                ConfigUtil.KnownKeys.KK_WHITELIST_WHILE_SLEEPING.getKeyName() ,
                ConfigUtil.KnownKeys.KK_BLOCK_SETTINGS.getKeyName() ,
                ConfigUtil.KnownKeys.KK_TIME_LIMIT_TOTAL.getKeyName()));
    }
}
