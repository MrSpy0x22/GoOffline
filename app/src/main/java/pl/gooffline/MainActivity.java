package pl.gooffline;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

import pl.gooffline.fragments.ApplicationFragment;
import pl.gooffline.lists.AppList;
import pl.gooffline.services.MonitorService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavController navController = Navigation.findNavController(this , R.id.fragment_main_nav);
        Toolbar mainToolbar = findViewById(R.id.toolbar_main);

        NavigationUI.setupWithNavController(mainToolbar , navController);

        startForegroundService(new Intent(getBaseContext() , MonitorService.class));
    }
}