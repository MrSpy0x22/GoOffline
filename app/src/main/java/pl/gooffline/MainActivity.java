package pl.gooffline;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import pl.gooffline.services.MonitorService;

public class MainActivity extends AppCompatActivity {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this , R.id.fragment_main_nav);
        Toolbar mainToolbar = findViewById(R.id.toolbar_main);
        mainToolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        NavigationUI.setupWithNavController(mainToolbar , navController);

        startForegroundService(new Intent(getBaseContext() , MonitorService.class));
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
}