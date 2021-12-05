package pl.gooffline.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import pl.gooffline.R;
import pl.gooffline.lists.SimpleList;

public class SettingsFragment extends Fragment {
    private List<SimpleList.ItemData> simpleList;
    private SimpleList simpleListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        simpleList = getPreparedSettingList();
        simpleListAdapter = new SimpleList(requireContext() , simpleList);

        ListView listView = view.findViewById(R.id.setting_list_view);
        listView.setAdapter(simpleListAdapter);

    }

    private List<SimpleList.ItemData> getPreparedSettingList() {
        List<SimpleList.ItemData> result = new ArrayList<>();

        result.add(new SimpleList.ItemData(R.drawable.ic_setting_rules , "Dozwolone aplikacje" , 1));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_sleeptime , "Czas snu" , 1));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_game , "Dodatkowy czas" , 1));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_history , "Dziennik akcji" , 1));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_permission , "Menadżer uprawnień" , 1));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_password , "Bezpieczeństwo" , 1));

        return result;
    }
}
