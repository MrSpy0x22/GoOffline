package pl.gooffline.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.gooffline.MainActivity;
import pl.gooffline.R;
import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.lists.AppList;
import pl.gooffline.presenters.WhitelistPresenter;
import pl.gooffline.services.MonitorService;

public class WhitelistFragment extends Fragment implements WhitelistPresenter.View {
    private List<AppList.AppData> installedApps;
    private AppList appListAdapter;
    private ListView appListView;
    private WhitelistPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_whitelis, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tworzenie obiektu prezentera
        presenter = new WhitelistPresenter(requireContext());

        // Pobieranie referencji do widoków
        appListView = view.findViewById(R.id.app_list);
        EditText searchEditText = view.findViewById(R.id.app_search_edit);
        Button buttonUpdate = view.findViewById(R.id.button_update);

        // Funkcja wyszukiwania
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

        // Zapisywanie danych
        buttonUpdate.setOnClickListener(e -> onClickButtonUpdate());
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
        onChangeWorkingStatusFlag(true);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            try {
                installedApps = presenter.getAsAppData();

                appListAdapter = new AppList(getContext() , installedApps);
                appListView.setTextFilterEnabled(true);
                appListView.setAdapter(appListAdapter);
                onChangeWorkingStatusFlag(false);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }, 1500);
    }

    @Override
    public void onChangeWorkingStatusFlag(boolean statusFlag) {
        MainActivity.setWorkingStatusIndicator(statusFlag);
    }

    @Override
    public void onClickButtonUpdate() {
        this.onChangeWorkingStatusFlag(true);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            List<Whitelist> updatedList = installedApps.stream()
                    .map(a -> new Whitelist(a.getPackageName() , a.isSelected()))
                    .collect(Collectors.toList());

            presenter.pushData(updatedList);
            this.onChangeWorkingStatusFlag(false);
            this.onDataUpdated(updatedList);
        }, 1000);
    }

    @Override
    public void onDataUpdated(List<Whitelist> whitelists) {
        List<String> packagesToWatch = whitelists.stream()
                        .filter(w -> !w.isIgnored())
                        .map(Whitelist::getPackageName)
                        .collect(Collectors.toList());
        // Aktualizacja danych serwisu
        MonitorService.updateWatchedPackages(packagesToWatch);

        NavHostFragment fragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_main_nav);

        if (fragment != null) {
            NavController navController = fragment.getNavController();

            Toast.makeText(requireContext(), getString(R.string.just_saved_changes), Toast.LENGTH_SHORT).show();
            navController.navigateUp();
        } else {
            Log.d(this.getClass().toString() , "fragment = null");
        }
    }
}
