package pl.gooffline;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.time.Instant;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import pl.gooffline.presenters.SecurityPresenter;
import pl.gooffline.services.BroadcastActivity;
import pl.gooffline.services.BroadcastLogger;
import pl.gooffline.services.MonitorService;
import pl.gooffline.utils.ConfigUtil;

public class MainActivity extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_SERVICE = "gooffline_channel_service";
    public static final String NOTIFICATION_CHANNEL_INFO = "gooffline_channel_info";

    private SecurityPresenter secPresenter;
    private NavController navController;
    private static Boolean workingStatusIndicator;
    private static Subject<Boolean> workingStatusSubject;
    private Receiver receiver;

    private LinearProgressIndicator workingProgresView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Wymuszanie trybu dziennogo dla UI
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Layout
        setContentView(R.layout.activity_main);

        // Tworzenie obiektu prezentera
        secPresenter = new SecurityPresenter(this);

        //region Szukanie widoków
        Toolbar mainToolbar = findViewById(R.id.toolbar_main);
        workingProgresView = findViewById(R.id.working_progress_view);
        navController = Navigation.findNavController(this, R.id.fragment_main_nav);
        //endregion

        // Navigacja
        NavigationUI.setupWithNavController(mainToolbar, navController);

        // Menu
        mainToolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        // Ustawianie flagi pracy w tle TODO: usunąć jeżeli aplikacja działa na jednym wątku
        workingStatusIndicator = false;
        workingStatusSubject = PublishSubject.create();
        workingStatusSubject.subscribe(this::handleWorkingStatusIndicatorObservable, e -> e.printStackTrace());

        // Tworzenie obiektu do odbioru broadcast-ów
        receiver = new Receiver();

        // Broadcast
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastLogger.class.toString());
        intentFilter.addAction(BroadcastActivity.class.toString());
        registerReceiver(receiver, intentFilter);

        // Tworzenie kanałów powiadomień
        NotificationManager notifyMan = getSystemService(NotificationManager.class);
        notifyMan.createNotificationChannel(new NotificationChannel(
                NOTIFICATION_CHANNEL_SERVICE ,
                getString(R.string.services_channel_name) ,
                NotificationManager.IMPORTANCE_HIGH
        ));
        notifyMan.createNotificationChannel(new NotificationChannel(
                NOTIFICATION_CHANNEL_INFO ,
                getString(R.string.services_channel_name) ,
                NotificationManager.IMPORTANCE_HIGH
        ));

        // Uruchamianie serwisu
        startForegroundService(new Intent(getBaseContext(), MonitorService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //AppDatabase.getInstance(this).close();
        unregisterReceiver(receiver);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.mmi_notifications) {
            Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            startActivity(intent);
        } else if (itemId == R.id.mmi_settings) {
            View dialogPasswordDialogLayout = View.inflate(this, R.layout.dialog_admin_password, null);

            SecurityPresenter secPresenter = new SecurityPresenter(this);
            String passwordHash = secPresenter.getConfigValue(ConfigUtil.KnownKeys.KK_SEC_ADMIN_PASSWD);

            if (passwordHash.length() == 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Brak hasła")
                        .setMessage("Przejdź do ustawień bezpieczeństwa, aby uniemożliwość dostęp do ustawień osobom nieupoważnionym.")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            navController.navigate(R.id.action_home_to_settings);
                            BroadcastLogger.broadcastEvent(getApplicationContext(), BroadcastLogger.LogType.LT_AUTH,
                                    "Zalogowano.", Instant.now());
                        })
                        .create()
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setView(dialogPasswordDialogLayout)
                        .setTitle("Hasło administratora")
                        .setCancelable(false)
                        .setNegativeButton("Anuluj", (dialog, id) -> {
                            Toast.makeText(this, "Anulowano logowanie!", Toast.LENGTH_SHORT).show();
                        })
//                    .setNeutralButton("Reset" , (dialog , id) -> {
//                        Toast.makeText(this, "Przypominajka!", Toast.LENGTH_SHORT).show();
//                    })
                        .setPositiveButton("Zaloguj", (dialog, id) -> {
                            EditText passwordIput = dialogPasswordDialogLayout.findViewById(R.id.password_input);

                            if (secPresenter.compareCodeAndHash(passwordIput.getText().toString(), passwordHash)) {
                                navController.navigate(R.id.action_home_to_settings);
                            } else {
                                Toast.makeText(this, "Niepoprawne hasło!", Toast.LENGTH_SHORT).show();
                                BroadcastLogger.broadcastEvent(getApplicationContext(), BroadcastLogger.LogType.LT_AUTH,
                                        "Zły kod PIN.", Instant.now());
                            }
                        })
                        .create()
                        .show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setWorkingStatusIndicator(boolean status) {
        workingStatusIndicator = status;
        workingStatusSubject.onNext(status);
    }

    private void handleWorkingStatusIndicatorObservable(boolean status) {
        Log.d("thread", Thread.currentThread().getName());

        if (workingProgresView != null) {
            workingProgresView.setVisibility(View.GONE);
            workingProgresView.setIndeterminate(status);
            workingProgresView.setVisibility(View.VISIBLE);

            Log.d("handleWorkingStatusIndicatorObservable",
                    "Flaga pracy w tle została " + (status ? "włączona" : "wyłączona" + "."));
        } else {
            Log.d("handleWorkingStatusIndicatorObservable", "Widok jest null-em.");
        }
    }
}