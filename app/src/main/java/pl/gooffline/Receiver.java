package pl.gooffline;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;

import pl.gooffline.database.entity.Log;
import pl.gooffline.presenters.LogPresenter;
import pl.gooffline.services.BroadcastLogger;

public class Receiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String jsonData = intent.getStringExtra("jsonData");

        if (action.equals(BroadcastLogger.class.toString())) {
            LogPresenter presenter = new LogPresenter(context);
            BroadcastLogger broadcastLogger;

            try {
                broadcastLogger = new BroadcastLogger(jsonData);

                Log log = new Log(broadcastLogger.getType() , broadcastLogger.getMessage(), broadcastLogger.getTime().getEpochSecond());
                presenter.addEvent(log);
            } catch (JSONException e) {
                android.util.Log.d(getClass().toString() , String.format("Błąd serializacji do [%s] danych: %s" ,
                        BroadcastLogger.class.toString() , jsonData));
            }
        }
    }
}
