package pl.gooffline.presenters;

import android.content.Context;
import android.widget.LinearLayout;

public class AppsPresenter extends ConfigPresenter {
    /**
     * Interfejs widoku.
     */
    public interface View {
        void onRowOptionSelected(int layoutRowId);
    }

    public AppsPresenter(Context context) {
        super(context);
    }
}
