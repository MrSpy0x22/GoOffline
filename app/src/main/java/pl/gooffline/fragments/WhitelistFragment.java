package pl.gooffline.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.gooffline.MainActivity;
import pl.gooffline.R;
import pl.gooffline.database.AppDatabase;
import pl.gooffline.database.dao.WhitelistDao;
import pl.gooffline.database.entity.Config;
import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.lists.AppList;

public class WhitelistFragment extends Fragment {
    private List<AppList.AppData> installedApps;
    private AppList appListAdapter;
    private ListView appListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_whitelis, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appListView = view.findViewById(R.id.app_list);

        EditText searchEditText = view.findViewById(R.id.app_search_edit);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                appListAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button buttonUpdate = view.findViewById(R.id.button_update);
        // TODO: zmienić na MVP
        buttonUpdate.setOnClickListener(e -> {
            WhitelistDao whitelistDao = AppDatabase.getInstance(getContext()).whitelistDAO();
            whitelistDao.deleteAll();
            for (AppList.AppData app : installedApps) {
                if (app.isSelected()) {
                    whitelistDao.insertAll(new Whitelist(app.getPackageName()));
                }
            }
            Toast.makeText(getContext(), "Zapisano zmiany!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (installedApps == null) {
            installedApps = new ArrayList<>();
        }

        // Usuwanie zawartości listy adaptera
        if (installedApps.size() > 0) {
            installedApps.clear();
            appListAdapter.notifyDataSetChanged();
        }

        // Włączanie wskaźnika pracy
        MainActivity.setWorkingStatusIndicator(true);

        Context context = requireContext();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            try {
                // TODO: zmienić na MVP!!!
                List<String> packages = AppDatabase.getInstance(context)
                        .whitelistDAO()
                        .getAll()
                        .stream()
                        .map(Whitelist::getPackageName)
                        .collect(Collectors.toList());
                installedApps = getAppDataList(context, packages);

                appListAdapter = new AppList(getContext() , installedApps);
                appListView.setTextFilterEnabled(true);
                appListView.setAdapter(appListAdapter);
                MainActivity.setWorkingStatusIndicator(false);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }, 1500);
    }

    /**
     * Odpytuje <c>PackageManager</c> i zwraca listę danych z zainstalowanymi aplikacjami.
     * @param context Kontekst.
     * @param selectedPackages Stan checkbox-a dla pakietu (lub <c>null</c>).
     * @return Listę typu AppList.AppData.
     * @see pl.gooffline.lists.AppList.AppData
     */
    public static List<AppList.AppData> getAppDataList(Context context , List<String> selectedPackages) throws PackageManager.NameNotFoundException {
        List<AppList.AppData> dataList = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN , null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = context.getPackageManager();

        for (ResolveInfo app : packageManager.queryIntentActivities(intent , PackageManager.GET_META_DATA)) {
            if (app.activityInfo.packageName.equals(context.getPackageName())) {
                continue;
            }

            boolean isSelected = false;

            if (selectedPackages != null) {
                isSelected = selectedPackages.contains(app.activityInfo.packageName.toLowerCase());
            }

            dataList.add(new AppList.AppData(
                    app.loadIcon(packageManager) ,
                    app.loadLabel(packageManager).toString() ,
                    app.activityInfo.packageName ,
                    isSelected));
        }

        return dataList;
    }
}
