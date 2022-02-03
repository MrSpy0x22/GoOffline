package pl.gooffline.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import pl.gooffline.R;
import pl.gooffline.ServiceConfigManager;
import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.lists.AppList;
import pl.gooffline.presenters.WhitelistPresenter;

public class WhitelistFragment extends Fragment implements WhitelistPresenter.View {
    private List<AppList.AppData> installedApps;
    private AppList appListAdapter;
    private RecyclerView appListView;
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

        //region Szukanie widoków
        appListView = view.findViewById(R.id.app_list);
        SearchView searchEditText = view.findViewById(R.id.app_search_edit);
        Button buttonUpdate = view.findViewById(R.id.button_update);
        //endregion

        // Funkcja wyszukiwania
        searchEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String text) {
                appListAdapter.getFilter().filter(text);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }
        });

        // Zapisywanie danych
        buttonUpdate.setOnClickListener(e -> onClickButtonUpdate());

        DividerItemDecoration horizontalLine = new DividerItemDecoration(requireContext() , DividerItemDecoration.VERTICAL);
        appListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        appListView.addItemDecoration(horizontalLine);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            installedApps = presenter.getAsAppData();
            appListAdapter = new AppList(installedApps , this::onClickListItem);
            appListView.setAdapter(appListAdapter);
        } catch (Exception e) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Wystąpił błąd")
                    .setMessage("Nie udało się pobrać zainstalowanych aplikacji.")
                    .setCancelable(true)
                    .create().show();
        }
    }

    @Override
    public void onClickButtonUpdate() {
        AtomicInteger key = new AtomicInteger(0);
        // Tworzenie kolekcji danych Whitelist
        List<Whitelist> updatedList = installedApps.stream()
                .filter(AppList.AppData::isSelected)
                .map(a -> new Whitelist(key.getAndIncrement() , a.getPackageName() , a.isSelected()))
                .collect(Collectors.toList());

        // Zapis kolekcji do bazy danych
        presenter.pushData(updatedList);

        // Aktualizacja konfiguracji serwisu
        ServiceConfigManager.getInstance().setAllowedPackages(
                updatedList.stream()
                    .map(Whitelist::getPackageName)
                    .collect(Collectors.toList())
        );

        this.onDataUpdated();
    }

    @Override
    public void onDataUpdated() {
        NavHostFragment fragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_main_nav);

        // Powrót do porpzedniego widoku
        if (fragment != null) {
            NavController navController = fragment.getNavController();

            Toast.makeText(requireContext(), getString(R.string.just_saved_changes), Toast.LENGTH_SHORT).show();
            navController.navigateUp();
        } else {
            Log.d(getClass().toString() , "fragment = null");
        }
    }

    @Override
    public void onClickListItem(AppList.AppData appData , CheckBox checkBox) {
        appData.setSelected(!appData.isSelected());
        checkBox.setChecked(appData.isSelected());
    }
}
