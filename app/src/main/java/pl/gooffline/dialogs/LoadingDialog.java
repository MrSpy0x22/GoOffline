package pl.gooffline.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pl.gooffline.R;

public class LoadingDialog<T> {
    private final Activity activity;
    private final AlertDialog alertDialog;
    private T result;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
        this.alertDialog = buildDialog();
    }

    private AlertDialog buildDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_loading , null);
        return new AlertDialog.Builder(this.activity)
                .setView(dialogView)
                .setCancelable(false)
                .create();
    }

    public T runTask(Callable<T> callable) {
        this.alertDialog.show();
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<T> futureResult = es.submit(callable);
        T result;

        try {
            result = futureResult.get();
        } catch (Exception e) {
            result = null;
        }

        alertDialog.dismiss();

        return result;
    }
}
