package pl.gooffline.fragments;

import android.os.Bundle;
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

public class ApplicationFragment extends Fragment implements AppsPresenter.View {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apps, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout layoutRowActiveWhileSleeping = view.findViewById(R.id.apps_option_row_active_while_sleeping);
        layoutRowActiveWhileSleeping.setOnClickListener(e -> onRowOptionSelected(layoutRowActiveWhileSleeping.getId()));

        SwitchMaterial activeWhileSleepingSwitch = view.findViewById(R.id.apps_option_row_active_while_sleeping_switch);

        LinearLayout layoutRowGoToWhitelist = view.findViewById(R.id.apps_option_row_whitelist);
        layoutRowGoToWhitelist.setOnClickListener(e -> onRowOptionSelected(layoutRowGoToWhitelist.getId()));
    }

    @Override
    public void onRowOptionSelected(int layoutRowId) {
        // Lista dozwolonych aplikacji
        if (layoutRowId == R.id.apps_option_row_active_while_sleeping) {

            boolean state
        } else if (layoutRowId == R.id.apps_option_row_whitelist) {
            NavHostFragment fragment = (NavHostFragment) requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_main_nav);

            if (fragment != null) {
                NavController navController = fragment.getNavController();
                navController.navigate(R.id.action_application_to_whitelist);
            }
        }
    }
}
