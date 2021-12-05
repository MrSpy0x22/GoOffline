package pl.gooffline.lists;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.gooffline.R;

public class SimpleList extends BaseAdapter {

    //region Klasa modelu
    public static class ItemData {
        int resIcon;
        String name;
        int navigationAction;

        public ItemData(int resIcon, String name , int navigationAction) {
            this.resIcon = resIcon;
            this.name = name;
            this.navigationAction = navigationAction;
        }

        public int getIcon() {
            return resIcon;
        }

        public String getName() {
            return name;
        }
    }
    //endregion

    private final Context context;
    private final List<ItemData> itemDataList;

    public SimpleList(Context context , List<ItemData> data) {
        this.context = context;
        this.itemDataList = data == null ? new ArrayList<>() : data;
    }

    //region Metody adaptera
    @Override
    public int getCount() {
        return itemDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = View.inflate(context , R.layout.list_setting_item , null);
        }

        ImageView icon = view.findViewById(R.id.setting_list_icon);
        TextView name = view.findViewById(R.id.setting_list_name);

        ItemData data = itemDataList.get(position);

        icon.setImageResource(data.resIcon);
        name.setText(data.name);

        name.setOnClickListener(e -> {
        });

        return view;
    }
    //endregion
}