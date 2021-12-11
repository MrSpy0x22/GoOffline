package pl.gooffline.lists;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import pl.gooffline.R;

public class JournalList extends ArrayAdapter<JournalList.EventData> {

    //region Klasa modelu
    public static class EventData {
        LocalDate date;
        LocalTime time;
        String name;
        String details;

        public EventData(LocalDate date, LocalTime time, String name, String details) {
            this.date = date;
            this.time = time;
            this.name = name;
            this.details = details;
        }

        public LocalDate getDate() {
            return date;
        }

        public LocalTime getTime() {
            return time;
        }

        public String getName() {
            return name;
        }

        public String getDetails() {
            return details;
        }
    }
    //endregion

    private final Context context;
    private final List<EventData> eventDataList;

    public JournalList(Context context , List<EventData> data) {
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
    public EventData getItem(int position) {
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

        EventData data = eventDataList.get(position);
        LocalTime timeData = data.getTime();

        name.setText(data.getName());
        time.setText(String.format(context.getString(R.string.time_format) ,
                timeData.getHour() , timeData.getMinute() , timeData.getSecond()));

        return view;
    }
    //endregion
}
