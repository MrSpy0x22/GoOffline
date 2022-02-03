package pl.gooffline.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

import pl.gooffline.R;
import pl.gooffline.ServiceConfigManager;
import pl.gooffline.lists.SimpleList;
import pl.gooffline.presenters.SettingsPresenter;
import pl.gooffline.utils.ConfigUtil;

public class SettingsFragment extends Fragment implements SettingsPresenter.View {
    private final String NOTIFICATION_CHANNEL = "Informacje";
    private LinearLayout serviceEnablerRow;
    private SwitchCompat serviceStateSwitch;
    private SettingsPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new SettingsPresenter(view.getContext());

        serviceStateSwitch = view.findViewById(R.id.service_enable_switch);

        serviceEnablerRow = view.findViewById(R.id.service_enable);
        serviceEnablerRow.setOnClickListener(e -> onServiceStateChanged(!serviceStateSwitch.isChecked()));

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

        this.onViewReady();
    }

    private List<SimpleList.ItemData> getPreparedSettingList() {
        List<SimpleList.ItemData> result = new ArrayList<>();

        result.add(new SimpleList.ItemData(R.drawable.ic_setting_rules , getString(R.string.settings_list_whtelist) , R.id.action_settings_to_application));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_sleeptime , getString(R.string.settings_list_sleeptime) , R.id.action_settings_to_sleeptime));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_game , getString(R.string.settings_list_game) , R.id.action_settings_to_game));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_history , getString(R.string.settings_list_journal) , R.id.action_settings_to_journal));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_permission , getString(R.string.settings_list_permissions) , R.id.action_settings_to_permissions));
        result.add(new SimpleList.ItemData(R.drawable.ic_setting_password , getString(R.string.settings_list_security) , R.id.action_settings_to_security));

        return result;
    }

    @Override
    public void onViewReady() {
        serviceStateSwitch.setChecked(
                ServiceConfigManager.getInstance().isServiceEnabled()
        );
    }

    @Override
    public void onServiceStateChanged(boolean state) {
        serviceStateSwitch.setChecked(state);
        ServiceConfigManager.getInstance().setServiceEnabled(state);
        presenter.setConfigValue(ConfigUtil.KnownKeys.KK_ACTIVE , state ? "1" : "0");
        presenter.pushData();
    }
}
