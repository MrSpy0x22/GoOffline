package pl.gooffline.lists;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.gooffline.R;

public class AppList extends BaseAdapter implements Filterable {

    //region Klasa modelu
    public static class AppData {
        Drawable icon;
        String name;
        String packageName;
        boolean selected;

        public AppData(Drawable icon, String name, String packageName, boolean selected) {
            this.icon = icon;
            this.name = name;
            this.packageName = packageName;
            this.selected = selected;
        }

        public Drawable getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }

        public String getPackageName() {
            return packageName;
        }

        public boolean isSelected() {
            return selected;
        }
    }
    //endregion

    private final Context context;
    private final List<AppData> appData;
    private List<AppData> appDataFiltered;

    public AppList(Context context , List<AppData> data) {
        this.context = context;
        this.appData = data == null ? new ArrayList<>() : data;
        this.appDataFiltered = data == null ? new ArrayList<>() : data;
    }

    //region Metody adaptera
    @Override
    public int getCount() {
        return appDataFiltered.size();
    }

    @Override
    public Object getItem(int position) {
        return appDataFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = View.inflate(context , R.layout.app_list_item , null);
        }

        ImageView icon = view.findViewById(R.id.app_list_icon);
        TextView caption = view.findViewById(R.id.app_list_header);
        TextView text = view.findViewById(R.id.app_list_subheader);
        CheckBox checkBox = view.findViewById(R.id.app_list_checkbox);

        AppData data = appDataFiltered.get(position);
        icon.setImageDrawable(data.icon);
        caption.setText(data.name);
        text.setText(data.packageName);
        checkBox.setChecked(data.selected);

        final Context c = view.getContext();
        checkBox.setOnClickListener(e -> data.selected = !data.selected);

        return view;
    }
    //endregion

    //region Filtrowanie listy
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (appData == null || appData.size() == 0) {
                    filterResults.values = appData;
                    filterResults.count = 0;
                } else {
                    List<AppData> results = new ArrayList<>();
                    String searchedText = constraint.toString().toLowerCase();

                    for (AppData data : appData) {
                        if (data.name.toLowerCase().contains(searchedText)) {
                            results.add(data);
                        }
                    }

                    filterResults.values = results;
                    filterResults.count = results.size() - 1;
                }

                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                appDataFiltered = (ArrayList<AppData>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    //endregion

}
