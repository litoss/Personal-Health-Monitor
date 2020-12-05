package com.example.personalhealthmonitor.Controller;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalhealthmonitor.Model.Database;
import com.example.personalhealthmonitor.Model.Settings;
import com.example.personalhealthmonitor.Model.Threshold;
import com.example.personalhealthmonitor.Model.notifications.AlarmReceiver;
import com.example.personalhealthmonitor.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    Settings settings;
    LinearLayout listThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = Database.getDB(getApplicationContext()).getSettings();
        listThreshold = findViewById(R.id.list_threshold);

        List<String> hours = new ArrayList<>();
        for (int i=0;i<10;i++)hours.add("0" + i + ":00");
        for (int i=10;i<24;i++)hours.add (i + ":00");

        final Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hours);
        spinner.setAdapter(adapter);
        spinner.setSelection(settings.getHour());

        final SwitchMaterial switchMaterial = findViewById(R.id.switchMaterial);

        if(settings.getNotifications())
            switchMaterial.setChecked(true);
        else
            spinner.setEnabled(false);

        createThresholdList();

        Button button = findViewById(R.id.save_threshold);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveThreshold();
            }
        });

        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setNotifications(b);
                spinner.setEnabled(b);
                Database.getDB(getApplicationContext()).editSettings(settings);

                if(b)
                    AlarmReceiver.setRecurringAlarm(getApplicationContext(), settings.getHour());
                else
                    AlarmReceiver.cancelRecurringAlarm(getApplicationContext());
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settings.setHour(i);
                Database.getDB(getApplicationContext()).editSettings(settings);

                if(switchMaterial.isEnabled())
                    AlarmReceiver.setRecurringAlarm(getApplicationContext(), i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void createThresholdList(){
        List<Threshold> thresholds = Database.getDB(getApplicationContext()).getThresholds();

        for(final Threshold threshold : thresholds){
            CheckBox checkBox = null;

            switch (threshold.getType()) {
                case "Body temperature": {
                    EditText min = findViewById(R.id.min_body_temperature);
                    EditText max = findViewById(R.id.max_body_temperature);
                    checkBox = findViewById(R.id.checkbox_body_temperature);
                    checkBox.setChecked(threshold.isMonitored());
                    min.setText(String.valueOf(threshold.getMin()));
                    max.setText(String.valueOf(threshold.getMax()));
                    break;
                }
                case "Systolic pressure": {
                    EditText min = findViewById(R.id.min_systolic_pressure);
                    EditText max = findViewById(R.id.max_systolic_pressure);
                    checkBox = findViewById(R.id.checkbox_blood_pressure);
                    checkBox.setChecked(threshold.isMonitored());
                    min.setText(String.valueOf(threshold.getMin()));
                    max.setText(String.valueOf(threshold.getMax()));
                    break;
                }
                case "Diastolic pressure": {
                    EditText min = findViewById(R.id.min_diastolic_pressure);
                    EditText max = findViewById(R.id.max_diastolic_pressure);
                    checkBox = findViewById(R.id.checkbox_blood_pressure);
                    checkBox.setChecked(threshold.isMonitored());
                    min.setText(String.valueOf(threshold.getMin()));
                    max.setText(String.valueOf(threshold.getMax()));
                    break;
                }
                case "Glycemic index": {
                    EditText min = findViewById(R.id.min_glycemic_index);
                    EditText max = findViewById(R.id.max_glycemic_index);
                    checkBox = findViewById(R.id.checkbox_glycemic_index);
                    checkBox.setChecked(threshold.isMonitored());
                    min.setText(String.valueOf(threshold.getMin()));
                    max.setText(String.valueOf(threshold.getMax()));
                    break;
                }
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Database db = Database.getDB(getApplicationContext());
                    threshold.setMonitored(b);
                    db.editThreshold(threshold);
                }
            });
        }
    }

    private void saveThreshold(){
        Database db = Database.getDB(getApplicationContext());

        try {
            db.editThreshold(new Threshold(
                    "Body temperature",
                    Double.parseDouble(((EditText) findViewById(R.id.min_body_temperature)).getText().toString()),
                    Double.parseDouble(((EditText) findViewById(R.id.max_body_temperature)).getText().toString()),
                    ((CheckBox) findViewById(R.id.checkbox_glycemic_index)).isChecked()
            ));
            db.editThreshold(new Threshold(
                    "Systolic pressure",
                    Double.parseDouble(((EditText) findViewById(R.id.min_systolic_pressure)).getText().toString()),
                    Double.parseDouble(((EditText) findViewById(R.id.max_systolic_pressure)).getText().toString()),
                    ((CheckBox) findViewById(R.id.checkbox_blood_pressure)).isChecked()
            ));
            db.editThreshold(new Threshold(
                    "Diastolic pressure",
                    Double.parseDouble(((EditText) findViewById(R.id.min_diastolic_pressure)).getText().toString()),
                    Double.parseDouble(((EditText) findViewById(R.id.max_diastolic_pressure)).getText().toString()),
                    ((CheckBox) findViewById(R.id.checkbox_blood_pressure)).isChecked()
            ));
            db.editThreshold(new Threshold(
                    "Glycemic index",
                    Double.parseDouble(((EditText) findViewById(R.id.min_glycemic_index)).getText().toString()),
                    Double.parseDouble(((EditText) findViewById(R.id.max_glycemic_index)).getText().toString()),
                    ((CheckBox) findViewById(R.id.checkbox_glycemic_index)).isChecked()
            ));
        }catch (Exception e){
            ((TextView) findViewById(R.id.error)).setText(R.string.badFormat);
        }
    }
}
