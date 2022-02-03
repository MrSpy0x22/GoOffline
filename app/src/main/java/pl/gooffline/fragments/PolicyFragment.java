package pl.gooffline.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.gooffline.R;
import pl.gooffline.lists.AppList;
import pl.gooffline.lists.UsagesList;
import pl.gooffline.presenters.PolicyPresenter;

public class PolicyFragment extends Fragment {
    private List<AppList.AppData> installedApps;
    private AppList appListAdapter;
    private PolicyPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_policy, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new PolicyPresenter(view.getContext());

        RecyclerView list = view.findViewById(R.id.policy_list);
        DividerItemDecoration horizontalLine = new DividerItemDecoration(requireContext() , DividerItemDecoration.VERTICAL);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.addItemDecoration(horizontalLine);

        try {
            installedApps = presenter.getAsAppData(view.getContext());
            appListAdapter = new AppList(installedApps , null); // Dummy!
            list.setAdapter(appListAdapter);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
