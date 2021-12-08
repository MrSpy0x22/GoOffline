package pl.gooffline.lists;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.gooffline.R;
import pl.gooffline.utils.PermissionsUtil;

public class PermissionList extends ArrayAdapter<PermissionList.ItemData> {

    //region Klasa modelu
    public static class ItemData {
        String name;
        boolean state;
        PermissionsUtil.InnerPermissionName innerPermissionName;

        public ItemData(String name , PermissionsUtil.InnerPermissionName innerPermissionName , boolean state) {
            this.name = name;
            this.innerPermissionName = innerPermissionName;
            this.state = state;
        }

        public PermissionsUtil.InnerPermissionName getInnerPermissionName() {
            return innerPermissionName;
        }

        public boolean getState() {
            return state;
        }

        public void setState(boolean state) {
            this.state = state;
        }

        public String getName() {
            return name;
        }
    }
    //endregion

    private final Context context;
    private final List<ItemData> itemDataList;

    public PermissionList(Context context , List<ItemData> data) {
        super(context , 0 , data);
        this.context = context;
        this.itemDataList = data == null ? new ArrayList<>() : data;
    }

    //region Metody adaptera
    @Override
    public int getCount() {
        return itemDataList.size();
    }

    @Override
    public ItemData getItem(int position) {
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
            view = View.inflate(context , R.layout.list_permission_item , null);
        }

        ImageView icon = view.findViewById(R.id.permission_list_icon);
        TextView name = view.findViewById(R.id.permission_list_name);

        ItemData data = itemDataList.get(position);

        icon.setImageResource(data.state
                ? R.drawable.ic_custom_checked
                : R.drawable.ic_custom_unchecked);
        name.setText(data.name);

        return view;
    }
    //endregion
}