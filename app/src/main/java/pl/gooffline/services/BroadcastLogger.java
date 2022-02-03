package pl.gooffline.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;

import pl.gooffline.Receiver;

public class BroadcastLogger implements BroadcastSerialization<BroadcastLogger> {
    //region Typ zdarzenia logu
    public enum LogType {
        LT_NULL(0) ,
        LT_AUTH(1) ,
        LT_LOCK(2) ,
        LT_LIMIT(3) ;

        int type;

        LogType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static LogType getByNumber(int num) {
            if (num == 1) {
                return LT_AUTH;
            } else if (num == 2) {
                return LT_LOCK;
            } else if (num == 3) {
                return LT_LIMIT;
            } else {
                return LT_NULL;
            }
        }
    }
    //endregion

    int type;
    String message;
    Instant time;

    public BroadcastLogger(int type, String message , Instant time) {
        this.type = type;
        this.message = message;
        this.time = time;
    }

    public BroadcastLogger(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        this.type = jsonObject.getInt("type");
        this.message = jsonObject.getString("message");
        this.time = Instant.ofEpochSecond(jsonObject.getLong("time"));
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTime() {
        return time;
    }

    @Override
    public String toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("type" , this.type);
            json.put("message" , this.message);
            json.put("time" , time.getEpochSecond());
        } catch (JSONException e) {
            Log.e(getClass().toString() , e.getMessage());
        }

        return json.toString();
    }

    public static void broadcastEvent(Context context , LogType type , String message , Instant time) {
        BroadcastLogger bl = new BroadcastLogger(type.getType() , message , time);
        String jsonData = bl.toJSON();

        Intent intent = new Intent(context , Receiver.class);
        intent.putExtra("broadcastName" , bl.getClass().toString());
        intent.putExtra("jsonData" , jsonData);
        context.sendBroadcast(intent);
    }
}
