package pl.gooffline.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

import pl.gooffline.R;
import pl.gooffline.lists.SimpleList;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavHostFragment fragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_main_nav);
        NavController navController = fragment.getNavController();

        List<SimpleList.ItemData> simpleList = getPreparedSettingList();
        SimpleList simpleListAdapter = new SimpleList(requireContext() , simpleList);

        ListView listView = view.findViewById(R.id.setting_list_view);
        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            SimpleList.ItemData data = (SimpleList.ItemData) adapterView.getItemAtPosition(i);
            if (data != null && data.getAction() > 0) {
                navController.navigate(data.getAction());
            }
        });
        listView.setAdapter(simpleListAdapter);

    }

    private List<SimpleList.ItemData> getPreparedSettingList() {
        List<SimpleList.ItemData> result = new ArrayList<>();

        result.add(new SimpleList.ItemData(R.drawable.ic_setting_rules , "Dozwolone aplikacje" , R.id.action_settings_to_application));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_sleeptime , "Czas snu" , R.id.action_settings_to_sleeptime));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_game , "Dodatkowy czas" , R.id.action_settings_to_game));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_history , "Dziennik akcji" , R.id.action_settings_to_journal));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_permission , "Menadżer uprawnień" , R.id.action_settings_to_permissions));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_password , "Bezpieczeństwo" , R.id.action_settings_to_security));

        return result;
    }
}
