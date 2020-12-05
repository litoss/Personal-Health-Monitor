package com.example.personalhealthmonitor.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalhealthmonitor.Model.Database;
import com.example.personalhealthmonitor.R;
import com.example.personalhealthmonitor.Model.Report;
import com.example.personalhealthmonitor.Model.Utils;

import java.util.Calendar;

public class ViewReportActivity  extends AppCompatActivity {

    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        EditText date = findViewById(R.id.date);
        date.setEnabled(false);
        EditText time = findViewById(R.id.time);
        time.setEnabled(false);
        EditText bodyTemperature = findViewById(R.id.bodyTemperatureEditText);
        bodyTemperature.setEnabled(false);
        EditText glicemicIndex = findViewById(R.id.glicemixIndexEditText);
        glicemicIndex.setEnabled(false);
        EditText systolicPressure = findViewById(R.id.systolic);
        systolicPressure.setEnabled(false);
        EditText diastolicPressure = findViewById(R.id.diastolic);
        diastolicPressure.setEnabled(false);
        EditText personalComment = findViewById(R.id.personalCommentEditText);
        personalComment.setEnabled(false);
        RatingBar rating = findViewById(R.id.ratingBar);
        rating.setIsIndicator(true);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            report = bundle.getParcelable("report");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(report.getDate());
            date.setText(Utils.dateFormat(calendar, "dd/MM/yyyy"));
            time.setText(Utils.dateFormat(calendar, "HH:mm"));
            bodyTemperature.setText(String.valueOf(report.getBodyTemperature()));
            glicemicIndex.setText(String.valueOf(report.getGlicemicIndex()));
            systolicPressure.setText(String.valueOf(report.getSystolicPressure()));
            diastolicPressure.setText(String.valueOf(report.getDiastolicPressure()));
            personalComment.setText(String.valueOf(report.getComment()));
            rating.setRating(report.getRating());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.edit_report) {

            Intent intent = new Intent(this, AddReportActivity.class);
            Bundle b = new Bundle();
            b.putParcelable("report", report);
            intent.putExtras(b);
            startActivity(intent);
            finish();
            return true;
        }else if(item.getItemId() == R.id.delete_report){
            Database.getDB(getApplicationContext()).removeReport(report);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}