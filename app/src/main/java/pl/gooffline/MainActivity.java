package pl.gooffline;

import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import pl.gooffline.services.MonitorService;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private static Boolean workingStatusIndicator;
    private static Subject<Boolean> workingStatusSubject;

    private LinearProgressIndicator workingProgresView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workingProgresView = findViewById(R.id.working_progress_view);

        navController = Navigation.findNavController(this , R.id.fragment_main_nav);
        Toolbar mainToolbar = findViewById(R.id.toolbar_main);
        mainToolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        NavigationUI.setupWithNavController(mainToolbar , navController);

        startForegroundService(new Intent(getBaseContext() , MonitorService.class));

        // Ustawianie flagi pracy w tle
        Log.d("threadM" , Thread.currentThread().getName());
        workingStatusIndicator = false;
        workingStatusSubject = PublishSubject.create();
        workingStatusSubject.subscribe(this::handleWorkingStatusIndicatorObservable , e -> e.printStackTrace());
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.mmi_settings) {
            View dialogPasswordDialogLayout = View.inflate(this , R.layout.dialog_admin_password , null);
            new AlertDialog.Builder(this)
                    .setView(dialogPasswordDialogLayout)
                    .setTitle("Hasło administratora")
                    .setCancelable(false)
                    .setNegativeButton("Anuluj" , (dialog , id) -> {
                        Toast.makeText(this, "Anulowano logowanie!", Toast.LENGTH_SHORT).show();
                    })
                    .setNeutralButton("Reset" , (dialog , id) -> {
                        Toast.makeText(this, "Przypominajka!", Toast.LENGTH_SHORT).show();
                    })
                    .setPositiveButton("Zaloguj" , (dialog , id) -> {
                        navController.navigate(R.id.action_home_to_settings);
                    })
                    .create()
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setWorkingStatusIndicator(boolean status) {
        workingStatusIndicator = status;
        workingStatusSubject.onNext(status);
    }

    private void handleWorkingStatusIndicatorObservable(boolean status) {
        Log.d("thread" , Thread.currentThread().getName());

        if (workingProgresView != null) {
            workingProgresView.setVisibility(View.GONE);
            workingProgresView.setIndeterminate(status);
            workingProgresView.setVisibility(View.VISIBLE);

            Log.d("handleWorkingStatusIndicatorObservable" ,
                    "Flaga pracy w tle została " + (status ? "włączona" : "wyłączona" + "."));
        } else {
            Log.d("handleWorkingStatusIndicatorObservable" , "Widok jest null-em.");
        }
    }
}