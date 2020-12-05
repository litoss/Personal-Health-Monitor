package com.example.personalhealthmonitor.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.personalhealthmonitor.Model.Database;
import com.example.personalhealthmonitor.Model.Settings;
import com.example.personalhealthmonitor.Model.Threshold;
import com.example.personalhealthmonitor.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int SETTINGS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        checkFirstStartUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkFirstStartUp(){

        Database db = Database.getDB(getApplicationContext());

        if(db.getSettings() == null){
            db.addSettings(new Settings(false, 19));
        }

        if(db.getThresholds().size() == 0){
            db.addThreshold(new Threshold("Glycemic index", 70, 100, false));
            db.addThreshold(new Threshold("Body temperature", 36, 37.5, false));
            db.addThreshold(new Threshold("Systolic pressure", 90, 130, false));
            db.addThreshold(new Threshold("Diastolic pressure", 60, 85, false));
        }
    }
}