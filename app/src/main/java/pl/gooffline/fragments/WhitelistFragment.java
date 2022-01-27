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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pl.gooffline.MainActivity;
import pl.gooffline.R;
import pl.gooffline.ServiceConfigManager;
import pl.gooffline.database.entity.Whitelist;
import pl.gooffline.lists.AppList;
import pl.gooffline.presenters.WhitelistPresenter;
import pl.gooffline.services.MonitorService;

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

        // Pobieranie referencji do widoków
        appListView = view.findViewById(R.id.app_list);
        SearchView searchEditText = view.findViewById(R.id.app_search_edit);
        Button buttonUpdate = view.findViewById(R.id.button_update);

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

        try {
            appListAdapter = new AppList(presenter.getAsAppData());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        DividerItemDecoration horizontalLine = new DividerItemDecoration(requireContext() , DividerItemDecoration.VERTICAL);
        appListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        appListView.addItemDecoration(horizontalLine);
        appListView.setAdapter(appListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClickButtonUpdate() {
        // Tworzenie kolekcji danych Whitelist
        List<Whitelist> updatedList = installedApps.stream()
                .map(a -> new Whitelist(a.getPackageName() , a.isSelected()))
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
}
