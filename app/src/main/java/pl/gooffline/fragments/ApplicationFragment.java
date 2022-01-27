package pl.gooffline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import pl.gooffline.R;
import pl.gooffline.presenters.AppsPresenter;
import pl.gooffline.utils.ConfigUtil;

public class ApplicationFragment extends Fragment implements AppsPresenter.View {
    private SwitchMaterial activeWhileSleepingSwitch;
    private SwitchMaterial disableSystemSettings;
    private AppsPresenter presenter;
    private Slider timeSlider;
    private TextView timeSliderValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apps, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new AppsPresenter(requireContext());

        LinearLayout layoutRowActiveWhileSleeping = view.findViewById(R.id.apps_option_row_active_while_sleeping);
        layoutRowActiveWhileSleeping.setOnClickListener(e -> onRowOptionSelected(layoutRowActiveWhileSleeping.getId()));

        activeWhileSleepingSwitch = view.findViewById(R.id.apps_option_row_active_while_sleeping_switch);

        LinearLayout layoutRowGoToWhitelist = view.findViewById(R.id.apps_option_row_whitelist);
        layoutRowGoToWhitelist.setOnClickListener(e -> onRowOptionSelected(layoutRowGoToWhitelist.getId()));

        LinearLayout layoutRowDisableSettings = view.findViewById(R.id.apps_option_row_disable_options);
        layoutRowDisableSettings.setOnClickListener(e -> onRowOptionSelected(layoutRowDisableSettings.getId()));

        disableSystemSettings = view.findViewById(R.id.apps_option_row_disable_options_switch);

        timeSliderValue = view.findViewById(R.id.app_slider_time_value);

        timeSlider = view.findViewById(R.id.apps_time_slider);
        timeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                onTimeSliderUpdated();
            }
        });

        timeSlider.setLabelFormatter(value -> {
            int hours = (int) (value / 6f);
            int minutes = (int) (value % 6f) * 10;

            if (hours > 0) {
                return String.format(getString(R.string.allowed_time_format_h) , hours , minutes );
            } else {
                return String.format(getString(R.string.allowed_time_format_m) , minutes);
            }
        });

        this.onViewReady();
    }

    @Override
    public void onViewReady() {
        activeWhileSleepingSwitch.setChecked(
                presenter.getConfigValue(ConfigUtil.KnownKeys.KK_WHITELIST_WHILE_SLEEPING).equals("true")
        );
        disableSystemSettings.setChecked(
                presenter.getConfigValue(ConfigUtil.KnownKeys.KK_BLOCK_SETTINGS).equals("true")
        );
        timeSlider.setValue(
                Float.parseFloat(presenter.getConfigValue(ConfigUtil.KnownKeys.KK_TIME_LIMIT_TOTAL))
        );
        updateTimeSliderText();
    }

    @Override
    public void onRowOptionSelected(int layoutRowId) {
        if (layoutRowId == R.id.apps_option_row_active_while_sleeping) { // Aktywuj wyjątki w trybie snu
            boolean state = !activeWhileSleepingSwitch.isChecked();
            presenter.setConfigValue(ConfigUtil.KnownKeys.KK_WHITELIST_WHILE_SLEEPING, state ? "true" : "false");
            activeWhileSleepingSwitch.setChecked(state);

        } else if (layoutRowId == R.id.apps_option_row_whitelist) { // Lista dozwolonych aplikacji
            NavHostFragment fragment = (NavHostFragment) requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_main_nav);

            if (fragment != null) {
                NavController navController = fragment.getNavController();
                navController.navigate(R.id.action_application_to_whitelist);
            } else {
                Log.d(this.getClass().toString() , "fragment = null");
            }

        } else if (layoutRowId == R.id.apps_option_row_disable_options) { // Blokuj ustawienia
            boolean state = !disableSystemSettings.isChecked();
            presenter.setConfigValue(ConfigUtil.KnownKeys.KK_BLOCK_SETTINGS, state ? "true" : "false");
            disableSystemSettings.setChecked(state);
        }
    }

    @Override
    public void onTimeSliderUpdated() {
        int allowedTime = (int) timeSlider.getValue();
        presenter.setConfigValue(ConfigUtil.KnownKeys.KK_TIME_LIMIT_TOTAL , String.valueOf(allowedTime));
        updateTimeSliderText();
    }

    @Override
    public void onAppsInSleeptimeUpdated() {

    }

    @Override
    public void onBlockSettingsUpdated() {

    }

    private void updateTimeSliderText() {
        int value = (int) timeSlider.getValue();
        int hr = value / 6;
        int min = value % 6;

        timeSliderValue.setText(
                String.format(getString(R.string.app_slider_time_value) , hr , min)
        );
    }
}
