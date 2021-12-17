package pl.gooffline.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.gooffline.R;
import pl.gooffline.lists.JournalList;

public class JournalFragment extends Fragment {
    private List<JournalList.EventData> eventDataList;
    private JournalList eventListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal, container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Zmienne pomocnicze
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Widoki
        TextView textCalendarDate = view.findViewById(R.id.text_date_selector);

        DatePickerDialog dateDialog = new DatePickerDialog(requireContext(), (dialog , y , m , d) -> {
            textCalendarDate.setText(String.format(getString(R.string.date_format) , d , m + 1 , y));
        } , year , month , day);

        textCalendarDate.setOnClickListener(view1 -> dateDialog.show());

        // Lista zdarzeń
        eventDataList = getTestEventList();

        eventListAdapter = new JournalList(requireContext() , eventDataList);

        ListView listView = view.findViewById(R.id.journal_event_list);
        listView.setAdapter(eventListAdapter);
    }

    private List<JournalList.EventData> getTestEventList() {
        List<JournalList.EventData> result = new ArrayList<>();
        result.add(new JournalList.EventData(LocalDate.now() , LocalTime.now() , "Logowanie jako admin" , "N/A"));
        result.add(new JournalList.EventData(LocalDate.now() , LocalTime.now() , "Logowanie jako admin" , "N/A"));
        result.add(new JournalList.EventData(LocalDate.now() , LocalTime.now() , "Blokada aplikacji GMail" , "N/A"));

        return result;
    }
}
