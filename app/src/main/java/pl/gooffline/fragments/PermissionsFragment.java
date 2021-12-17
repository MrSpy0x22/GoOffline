package pl.gooffline.fragments;

import android.content.Context;
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
import java.util.Map;

import pl.gooffline.R;
import pl.gooffline.lists.PermissionList;
import pl.gooffline.utils.PermissionsUtil;

public class PermissionsFragment extends Fragment {
    private List<PermissionList.ItemData> permissionList;
    private PermissionList permissionListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_permissions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        permissionList = getPreparedRequiredPermissionList();
        permissionListAdapter = new PermissionList(requireContext() , permissionList);

        ListView listView = view.findViewById(R.id.permissions_list_view);
        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            PermissionList.ItemData data = (PermissionList.ItemData) adapterView.getItemAtPosition(i);
            if (data != null) {
                if (data.getInnerPermissionName() == PermissionsUtil.InnerPermissionName.CAN_IGNORE_BATTERY_OPTIMIZATION) {
                    PermissionsUtil.askForBatteryOptimization(requireContext());
                } else if (data.getInnerPermissionName() == PermissionsUtil.InnerPermissionName.CAN_DRAW_OVERALL) {
                    PermissionsUtil.askForCanDrawOverlay(requireContext());
                } else if (data.getInnerPermissionName() == PermissionsUtil.InnerPermissionName.CAN_USE_STATS) {
                    PermissionsUtil.askForUsageStats(requireContext());
                } else if (data.getInnerPermissionName() == PermissionsUtil.InnerPermissionName.CAN_SEND_SMS) {
                    PermissionsUtil.askForSendingSMS(requireContext() , requireActivity());
                } else if (data.getInnerPermissionName() == PermissionsUtil.InnerPermissionName.CAN_READ_PHONE_STATE) { // TODO: usunąć
                    PermissionsUtil.askForReadPhoneState(requireContext() , requireActivity());
                }
            }
        });

        updatePermissionListStatus();
        listView.setAdapter(permissionListAdapter);

    }

    /**
     * Przygotowuje listę wymaganych uprawnień dla listy {@link #permissionList}.
     * @return Lista uprawnień.
     * @see PermissionList.ItemData
     * @see PermissionsUtil#getRequiredPermissionsStatus(Context)
     */
    private List<PermissionList.ItemData> getPreparedRequiredPermissionList() {
        List<PermissionList.ItemData> result = new ArrayList<>();
        Map<PermissionsUtil.InnerPermissionName , Boolean> permissionStatus = PermissionsUtil.getRequiredPermissionsStatus(requireContext());
        Boolean state;

        state = permissionStatus.get(PermissionsUtil.InnerPermissionName.CAN_IGNORE_BATTERY_OPTIMIZATION);
        result.add(new PermissionList.ItemData(
                "Brak optymalizacji baterii" ,
                PermissionsUtil.InnerPermissionName.CAN_IGNORE_BATTERY_OPTIMIZATION ,
                state != null && state));

        state = permissionStatus.get(PermissionsUtil.InnerPermissionName.CAN_DRAW_OVERALL);
        result.add(new PermissionList.ItemData(
                "Rysowanie nad innymi oknami" ,
                PermissionsUtil.InnerPermissionName.CAN_DRAW_OVERALL ,
                state != null && state));

        state = permissionStatus.get(PermissionsUtil.InnerPermissionName.CAN_USE_STATS);
        result.add(new PermissionList.ItemData(
                "Dostęp do statystyk systemu" ,
                PermissionsUtil.InnerPermissionName.CAN_USE_STATS ,
                state != null && state));

        state = permissionStatus.get(PermissionsUtil.InnerPermissionName.CAN_SEND_SMS);
        result.add(new PermissionList.ItemData(
                "Wysyłąnie wiadomości SMS" ,
                PermissionsUtil.InnerPermissionName.CAN_SEND_SMS ,
                state != null && state));

        state = permissionStatus.get(PermissionsUtil.InnerPermissionName.CAN_READ_PHONE_STATE);
        result.add(new PermissionList.ItemData(
                "Odczytywanie numeru telefonu" ,
                PermissionsUtil.InnerPermissionName.CAN_READ_PHONE_STATE ,
                state != null && state));

        return result;
    }

    /**
     * Pobiera stan wymaganych uprawnień i aktualizuje elementy na liście {@link #permissionList}.
     * @see PermissionsUtil#getRequiredPermissionsStatus(Context)
     */
    private void updatePermissionListStatus() {
        // Mapa uprawnień
        Map<PermissionsUtil.InnerPermissionName , Boolean> permissionStatus = PermissionsUtil.getRequiredPermissionsStatus(requireContext());
        // Flaga oznaczająca wprowadzednie zmian
        boolean changed = false;

        // Przeszukiwanie listy uprawnień pod względem tych pasujących do pobranej mapy
        // oraz zaktualizowanie ich stanu.
        for (PermissionList.ItemData itemData : permissionList) {
            Boolean status = permissionStatus.get(itemData.getInnerPermissionName());

            if (status != null) {
                // Aktualizowanie zmian tylko jeżeli nowy stan jest inny od starego
                if (itemData.getState() != status) {
                    itemData.setState(status);
                    changed = true;
                }
            }
        }

        // Adapter musi dostać powiadomienie o wprowadzonych zmianach
        if (changed) {
            permissionListAdapter.notifyDataSetChanged();
        }
    }
}
