package pl.gooffline.lists;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.gooffline.R;

public class UsagesList extends RecyclerView.Adapter<UsagesList.ViewHolder> {
    //region Klasa modelu
    public static class AppDetails {
        Drawable icon;
        String name;
        int seconds;

        public AppDetails(Drawable icon, String name, int seconds) {
            this.icon = icon;
            this.name = name;
            this.seconds = seconds;
        }

        public Drawable getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }

        public int getSeconds() {
            return seconds;
        }
    }
    //endregion

    private final List<AppDetails> appDetails;

    //region ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView appIcon;
        private final TextView appName;
        private final TextView appTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            appIcon = itemView.findViewById(R.id.app_details_icon);
            appName = itemView.findViewById(R.id.app_details_name);
            appTime = itemView.findViewById(R.id.app_details_time);
        }
    }


    public UsagesList(List<AppDetails> appDetails) {
        this.appDetails = appDetails == null ? new ArrayList<>() : appDetails;
    }

    @NonNull
    @Override
    public UsagesList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_usage_item , parent , false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsagesList.ViewHolder holder, int position) {
        AppDetails listItem = appDetails.get(position);

        holder.appIcon.setImageDrawable(listItem.icon);
        holder.appName.setText(listItem.getName());
        holder.appTime.setText(convertSecondsToString(listItem.getSeconds()));
    }

    @Override
    public int getItemCount() {
        return appDetails.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static String convertSecondsToString(int seconds) {
        int d = seconds / 86400;
        int h = (seconds % 86400 ) / 3600 ;
        int m = ((seconds % 86400 ) % 3600 ) / 60;
        int s = ((seconds % 86400 ) % 3600 ) % 60  ;

        if (d > 0) {
            return String.format("%d dni %d:%02d:%02d godz" , d , h, m , s);
        }else if (h > 0) {
            return String.format("%d:%02d:%02d godz" , h, m , s);
        } else if (m > 0) {
            return String.format("%d:%02d min", m , s);
        } else {
            return String.format("%d sek", s);
        }
    }
}
