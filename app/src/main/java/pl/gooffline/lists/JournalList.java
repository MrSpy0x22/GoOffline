package pl.gooffline.lists;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import pl.gooffline.R;
import pl.gooffline.database.entity.Log;

public class JournalList extends ArrayAdapter<Log> {

    //region Klasa modelu
//    public static class EventData {
//        LocalDate date;
//        LocalTime time;
//        String name;
//        String details;
//
//        public EventData(LocalDate date, LocalTime time, String name, String details) {
//            this.date = date;
//            this.time = time;
//            this.name = name;
//            this.details = details;
//        }
//
//        public LocalDate getDate() {
//            return date;
//        }
//
//        public LocalTime getTime() {
//            return time;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public String getDetails() {
//            return details;
//        }
//    }
    //endregion

    private final Context context;
    private final List<Log> eventDataList;

    public JournalList(Context context , List<Log> data) {
        super(context , 0 , data);
        this.context = context;
        this.eventDataList = data == null ? new ArrayList<>() : data;
    }

    //region Metody adaptera
    @Override
    public int getCount() {
        return eventDataList.size();
    }

    @Override
    public Log getItem(int position) {
        return eventDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = View.inflate(context , R.layout.list_journal_item, null);
        }

        TextView name = view.findViewById(R.id.journal_list_name);
        TextView time = view.findViewById(R.id.journal_list_time);

        Log data = eventDataList.get(position);
        LocalTime timeData = LocalDateTime.ofInstant(Instant.ofEpochMilli(data.getLog_date()), ZoneId.systemDefault()).toLocalTime();

        name.setText(data.getLog_message());
        time.setText(timeData.toString());

        return view;
    }
    //endregion
}
