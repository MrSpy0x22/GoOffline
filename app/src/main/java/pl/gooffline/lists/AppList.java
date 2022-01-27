package pl.gooffline.lists;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.gooffline.R;
import pl.gooffline.database.entity.Category;
import pl.gooffline.database.entity.Wordbase;

public class AppList extends RecyclerView.Adapter<AppList.ViewHolder> implements Filterable {
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

    //region ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView appIcon;
        private final TextView appName;
        private final TextView appPackage;
        private final CheckBox appWhitelistedCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            appIcon = itemView.findViewById(R.id.app_list_icon);
            appName = itemView.findViewById(R.id.app_list_header);
            appPackage = itemView.findViewById(R.id.app_list_subheader);
            appWhitelistedCheck = itemView.findViewById(R.id.app_list_checkbox);
        }

        @Override
        public void onClick(View view) {
            boolean status = appWhitelistedCheck.isChecked();
            appWhitelistedCheck.setChecked(!status);
        }
    }
    //endregion

    private final List<AppData> appData;
    private List<AppData> appDataFiltered;

    public AppList(List<AppData> appData) {
        this.appData = appData == null ? new ArrayList<>() : appData;
        this.appDataFiltered = appData == null ? new ArrayList<>() : appData;
    }

    @NonNull
    @Override
    public AppList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_app_item , parent , false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppList.ViewHolder holder, int position) {
        AppData listItem = appDataFiltered.get(position);

        holder.appIcon.setImageDrawable(listItem.icon);
        holder.appName.setText(listItem.getName());
        holder.appPackage.setText(listItem.getPackageName());
        holder.appWhitelistedCheck.setChecked(listItem.isSelected());
    }

    @Override
    public int getItemCount() {
        return appDataFiltered.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

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
