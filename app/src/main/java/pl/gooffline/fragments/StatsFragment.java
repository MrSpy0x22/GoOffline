package pl.gooffline.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import pl.gooffline.R;
import pl.gooffline.ServiceConfigManager;
import pl.gooffline.database.entity.Usages;
import pl.gooffline.lists.UsagesList;
import pl.gooffline.utils.UsageStatsUtil;

public class StatsFragment extends Fragment {
    private final byte TAB_DAY = 0;
    private final byte TAB_WEEK = 1;
    private final byte TAB_MONTH = 2;
    private final byte TAB_YEAR = 3;

    private List<UsagesList.AppDetails> installedApps;
    private UsagesList usageList;
    private TextView label;
    private TextView textDate;

    private LinearLayout statsNotification;
    private LocalDateTime usedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats , container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = view.getContext();
        usedDate = LocalDateTime.now();

        installedApps = new ArrayList<>();

        statsNotification = view.findViewById(R.id.stats_notification_layout);

        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {
                    updateView(context, TAB_DAY);
                } else if (tab.getPosition() == 1) {
                    updateView(context, TAB_WEEK);
                } else if (tab.getPosition() == 2) {
                    updateView(context, TAB_MONTH);
                } else if (tab.getPosition() == 3) {
                    updateView(context, TAB_YEAR);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        textDate = view.findViewById(R.id.usages_calendar_label);
        LinearLayout calendarRow = view.findViewById(R.id.stats_calendar_row);
        calendarRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = usedDate.getYear();
                int month = usedDate.getMonthValue();
                int day = usedDate.getDayOfMonth();

                DatePickerDialog dateDialog = new DatePickerDialog(requireContext(),
                        (dialog , y , m , d) -> {
                            usedDate = LocalDateTime.of(y, m, d, 0, 0);

                            int tabIndex = tabs.getSelectedTabPosition();
                            if (tabIndex == 0) {
                                updateView(context, TAB_DAY);
                            } else if (tabIndex == 1) {
                                updateView(context, TAB_WEEK);
                            } else if (tabIndex == 2) {
                                updateView(context, TAB_MONTH);
                            } else if (tabIndex == 3) {
                                updateView(context, TAB_YEAR);
                            }
                            updateDateButton();

                        } , year , month , day);

                dateDialog.show();
            }
        });
        updateDateButton();

        label = view.findViewById(R.id.usages_label);

        usageList = new UsagesList(installedApps);
        RecyclerView list = view.findViewById(R.id.usages_list);
        list.setLayoutManager(new LinearLayoutManager(context));
        list.addItemDecoration(new DividerItemDecoration(requireContext() , DividerItemDecoration.VERTICAL));
        list.setAdapter(usageList);

        this.updateView(context, TAB_DAY);
    }

    private void updateView(Context context , byte forTabIdx) {
        List<Usages> usages;
        switch (forTabIdx) {
            case TAB_DAY: usages = UsageStatsUtil.getDailyUsagesDataPoints(context , usedDate); break;
            case TAB_WEEK: usages = UsageStatsUtil.getWeeklyUsagesDataPoints(context , usedDate); break;
            case TAB_MONTH: usages = UsageStatsUtil.getMonthlyUsagesDataPoints(context , usedDate); break;
            case TAB_YEAR: usages = UsageStatsUtil.getYearUsagesDataPoints(context , usedDate); break;
            default: return;
        }

        PackageManager packageManager = context.getPackageManager();
        final AtomicInteger total = new AtomicInteger(0);

        List<UsagesList.AppDetails> tempList = new ArrayList<>();
        for (Usages u : usages) {
            try {
                ApplicationInfo appInfo = packageManager.getApplicationInfo(u.getPackageName() , 0);
                tempList.add(
                        new UsagesList.AppDetails(
                                appInfo.loadIcon(packageManager) ,
                                packageManager.getApplicationLabel(appInfo).toString() ,
                                usages.stream()
                                        .filter(us -> us.getPackageName().equals(u.getPackageName()))
                                        .mapToInt(us -> {
                                            int tmp = us.getTotalSeconds();
                                            total.addAndGet(tmp);
                                            return tmp;
                                        })
                                        .sum())
                );
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        label.setText(UsagesList.convertSecondsToString(total.get()));

        tempList = tempList.stream()
                .sorted(Comparator.comparingInt(UsagesList.AppDetails::getSeconds).reversed())
                .collect(Collectors.toList());

        int size = installedApps.size();
        int time = installedApps.stream().mapToInt(u -> u.getSeconds()).sum();

        installedApps.clear();
        installedApps.addAll(tempList);

        usageList.notifyItemRangeRemoved(0 , size);
        usageList.notifyDataSetChanged();

        statsNotification.setVisibility(shouldWarningBeShown(forTabIdx, time ,
                ServiceConfigManager.getInstance().getDailyTimeTotal()) ? View.VISIBLE : View.GONE);
    }

    private boolean shouldWarningBeShown(int tabPos , int current , int total) {
        if (tabPos != 0) {
            int multiply = tabPos == 1 ? 7 : (tabPos == 2 ? 30 : 365);
            int max = (total / multiply);
            float warnLevel = 0.85f;

            return current > total || current > (max * warnLevel);
        }

        return false;
    }

    private void updateDateButton() {
        textDate.setText(String.format("%d.%02d.%04d" , usedDate.getDayOfMonth() , usedDate.getMonthValue() , usedDate.getYear()));
    }
}
