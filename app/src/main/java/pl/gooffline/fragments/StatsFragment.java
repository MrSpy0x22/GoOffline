package pl.gooffline.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import pl.gooffline.R;

public class StatsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats , container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HorizontalCalendarView horizontalCalendarView = view.findViewById(R.id.calendar_line);
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH , -1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH , 1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view , R.id.calendar_line)
                .range(startDate , endDate)
                .datesNumberOnScreen(5)
                .build();
    }
}
