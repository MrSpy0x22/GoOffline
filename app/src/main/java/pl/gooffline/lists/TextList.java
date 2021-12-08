package pl.gooffline.lists;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class TextList extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> data;

    public TextList(Context context , List<String> data) {
        super(context , 0 , data);
        this.context = context;
        this.data = data == null ? new ArrayList<>() : data;
    }

}
