package pl.gooffline.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import pl.gooffline.Receiver;

public class BroadcastActivity implements BroadcastSerialization<BroadcastActivity> {
    private String packageName;
    private LocalDateTime measureTimeStart;
    private LocalDateTime measureTimeStop;

    public BroadcastActivity(String packageName, LocalDateTime measureTimeStart, LocalDateTime measureTimeStop) {
        this.packageName = packageName;
        this.measureTimeStart = measureTimeStart;
        this.measureTimeStop = measureTimeStop;
    }

    public BroadcastActivity(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        this.packageName = jsonObject.getString("package_name");
        this.measureTimeStart = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(jsonObject.getLong("measure_time_start")) ,
                ZoneId.systemDefault()
        );
        this.measureTimeStop = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(jsonObject.getLong("measure_time_stop")) ,
                ZoneId.systemDefault()
        );
    }

    public String getPackageName() {
        return packageName;
    }

    public LocalDateTime getMeasureTimeStart() {
        return measureTimeStart;
    }

    public LocalDateTime getMeasureTimeStop() {
        return measureTimeStop;
    }

    public void setMeasureTimeStop(LocalDateTime measureTimeStop) {
        this.measureTimeStop = measureTimeStop;
    }

    public void setMeasureTimeStart(LocalDateTime measureTimeStart) {
        this.measureTimeStart = measureTimeStart;
    }

    @Override
    public String toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("package_name" , this.packageName);
            json.put("measure_time_start" , this.measureTimeStart.toEpochSecond(ZoneOffset.UTC));
            json.put("measure_time_stop" , this.measureTimeStop.toEpochSecond(ZoneOffset.UTC));
        } catch (JSONException e) {
            Log.e(getClass().toString() , e.getMessage());
        }

        return json.toString();
    }

    public static void broadcastEvent(Context context , String packageName , LocalDateTime measureTimeStart , LocalDateTime measureTimeStop) {
        BroadcastActivity ba = new BroadcastActivity(packageName, measureTimeStart, measureTimeStop);
        String jsonData = ba.toJSON();

        Intent intent = new Intent(context , Receiver.class);
        intent.putExtra("broadcastName" , ba.getClass().toString());
        intent.putExtra("jsonData" , jsonData);
        context.sendBroadcast(intent);
    }
}
