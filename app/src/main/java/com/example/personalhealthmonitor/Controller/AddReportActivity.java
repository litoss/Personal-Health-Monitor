package com.example.personalhealthmonitor.Controller;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalhealthmonitor.Model.Database;
import com.example.personalhealthmonitor.R;
import com.example.personalhealthmonitor.Model.Report;
import com.example.personalhealthmonitor.Model.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddReportActivity extends AppCompatActivity {

    private EditText bodyTemperature, glicemicIndex, diastolicPressure, systolicPressure, personalComment, date, time;
    private TextView error;
    private RatingBar rating;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        bodyTemperature = findViewById(R.id.bodyTemperatureEditText);
        glicemicIndex = findViewById(R.id.glicemixIndexEditText);
        diastolicPressure = findViewById(R.id.diastolic);
        systolicPressure = findViewById(R.id.systolic);
        personalComment = findViewById(R.id.personalCommentEditText);
        rating = findViewById(R.id.ratingBar);
        error = findViewById(R.id.error);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.getParcelable("report") != null) {
            Report report = bundle.getParcelable("report");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(report.getDate());
            date.setText(Utils.dateFormat(calendar, "dd/MM/YYYY"));
            time.setText(Utils.dateFormat(calendar, "HH:mm"));
            bodyTemperature.setText(String.valueOf(report.getBodyTemperature()));
            glicemicIndex.setText(String.valueOf(report.getGlicemicIndex()));
            systolicPressure.setText(String.valueOf(report.getSystolicPressure()));
            diastolicPressure.setText(String.valueOf(report.getDiastolicPressure()));
            personalComment.setText(String.valueOf(report.getComment()));
            rating.setNumStars(report.getRating());
            id = report.getId();
        }else if(bundle != null){
            long millis = bundle.getLong("time");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millis);
            date.setText(Utils.dateFormat(calendar, "dd/MM/YYYY"));
            time.setText(Utils.dateFormat(calendar, "HH:mm"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_report) {

            int fields = 0;
            if(!bodyTemperature.getText().toString().isEmpty())
                fields++;
            else
                bodyTemperature.setText("0");

            if(!glicemicIndex.getText().toString().isEmpty())
                fields++;
            else
                glicemicIndex.setText("0");

            if(!systolicPressure.getText().toString().isEmpty())
                fields++;
            else
                systolicPressure.setText("0");

            if(!diastolicPressure.getText().toString().isEmpty())
                fields++;
            else
                diastolicPressure.setText("0");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY);
            String dateString = date.getText().toString() + " " + time.getText().toString();

            Date date;
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e) {
                error.setText(R.string.badFormat);
                return false;
            }

            if(fields >= 2){

                try{
                    Report report = new Report(
                            id,
                            Objects.requireNonNull(sdf.parse(dateString)).getTime(),
                            Double.parseDouble(bodyTemperature.getText().toString()),
                            Integer.parseInt(systolicPressure.getText().toString()),
                            Integer.parseInt(diastolicPressure.getText().toString()),
                            Integer.parseInt(glicemicIndex.getText().toString()),
                            personalComment.getText().toString(),
                            (int) rating.getRating()
                    );

                    if(id != 0)
                        Database.getDB(getApplicationContext()).editReport(report);
                    else
                        Database.getDB(getApplicationContext()).addReport(report);

                    finish();

                } catch (Exception e) {
                    error.setText(R.string.badFormat);
                    return false;
                }


            }else{
                error.setText(R.string.minParam);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
