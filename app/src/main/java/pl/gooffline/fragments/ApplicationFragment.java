package pl.gooffline.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import pl.gooffline.R;
import pl.gooffline.presenters.AppsPresenter;
import pl.gooffline.utils.ConfigUtil;

public class ApplicationFragment extends Fragment implements AppsPresenter.View {
    private SwitchMaterial activeWhileSleepingSwitch;
    private SwitchMaterial disableSystemSettings;
    private AppsPresenter presenter;

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
        activeWhileSleepingSwitch.setChecked(
                presenter.getConfigValue(ConfigUtil.KnownKeys.KK_WHITELIST_WHILE_SLEEPING).equals("true")
        );

        LinearLayout layoutRowGoToWhitelist = view.findViewById(R.id.apps_option_row_whitelist);
        layoutRowGoToWhitelist.setOnClickListener(e -> onRowOptionSelected(layoutRowGoToWhitelist.getId()));

        LinearLayout layoutRowDisableSettings = view.findViewById(R.id.apps_option_row_disable_options);
        layoutRowDisableSettings.setOnClickListener(e -> onRowOptionSelected(layoutRowDisableSettings.getId()));

        disableSystemSettings = view.findViewById(R.id.apps_option_row_disable_options_switch);
        disableSystemSettings.setChecked(
                presenter.getConfigValue(ConfigUtil.KnownKeys.KK_BLOCK_SETTINGS).equals("true")
        );
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
}
