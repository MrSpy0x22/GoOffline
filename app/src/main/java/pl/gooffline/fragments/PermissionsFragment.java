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
                }
            }
        });

        listView.setAdapter(permissionListAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        updatePermissionListStatus();
    }

    /**
     * Przygotowuje list?? wymaganych uprawnie?? dla listy {@link #permissionList}.
     * @return Lista uprawnie??.
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
                "Dost??p do statystyk systemu" ,
                PermissionsUtil.InnerPermissionName.CAN_USE_STATS ,
                state != null && state));

        return result;
    }

    /**
     * Pobiera stan wymaganych uprawnie?? i aktualizuje elementy na li??cie {@link #permissionList}.
     * @see PermissionsUtil#getRequiredPermissionsStatus(Context)
     */
    private void updatePermissionListStatus() {
        // Mapa uprawnie??
        Map<PermissionsUtil.InnerPermissionName , Boolean> permissionStatus = PermissionsUtil.getRequiredPermissionsStatus(requireContext());
        // Flaga oznaczaj??ca wprowadzednie zmian
        boolean changed = false;

        // Przeszukiwanie listy uprawnie?? pod wzgl??dem tych pasuj??cych do pobranej mapy
        // oraz zaktualizowanie ich stanu.
        for (PermissionList.ItemData itemData : permissionList) {
            Boolean status = permissionStatus.get(itemData.getInnerPermissionName());

            if (status != null) {
                // Aktualizowanie zmian tylko je??eli nowy stan jest inny od starego
                if (itemData.getState() != status) {
                    itemData.setState(status);
                    changed = true;
                }
            }
        }

        // Adapter musi dosta?? powiadomienie o wprowadzonych zmianach
        if (changed) {
            permissionListAdapter.notifyDataSetChanged();
        }
    }
}
