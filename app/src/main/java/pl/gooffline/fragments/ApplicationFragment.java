package pl.gooffline.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import pl.gooffline.R;

public class ApplicationFragment extends PreferenceFragmentCompat {
    private Context context;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext().getApplicationContext();
    }
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_apps , rootKey);

        NavHostFragment fragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_main_nav);

        if (fragment == null) {
            Toast.makeText(requireContext(), "Wystąpił krytyczny błąd!", Toast.LENGTH_SHORT).show();
        } else {
            NavController navController = fragment.getNavController();

            Preference preferenceOpenWhiteList = getPreferenceManager().findPreference("app_pref_whitelist");
            preferenceOpenWhiteList.setOnPreferenceClickListener(p -> {
                navController.navigate(R.id.action_application_to_whitelist);
                return true;
            });
        }
    }
}
