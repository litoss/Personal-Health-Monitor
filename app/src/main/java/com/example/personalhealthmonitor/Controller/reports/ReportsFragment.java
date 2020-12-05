package com.example.personalhealthmonitor.Controller.reports;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.personalhealthmonitor.Model.Database;
import com.example.personalhealthmonitor.R;
import com.example.personalhealthmonitor.Model.Report;
import com.example.personalhealthmonitor.Model.Utils;
import com.example.personalhealthmonitor.Controller.AddReportActivity;
import com.example.personalhealthmonitor.Controller.GetDateActivity;
import com.example.personalhealthmonitor.Controller.ViewReportActivity;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportsFragment extends Fragment {

    private ListView listView;
    private TextView title;
    private ChipGroup chipGroup;
    private List<Report> reports;
    private Calendar calendar;
    private static final int GET_DATE_REQUEST = 886;
    private static final int ADD_REPORT_REQUEST = 188;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_reports, container, false);
        calendar = Calendar.getInstance();
        title = root.findViewById(R.id.title);
        title.setText(Utils.dateFormat(calendar, "dd MMMM yyyy"));
        listView = root.findViewById(R.id.reportsList);
        chipGroup = root.findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                int rating;
                switch (checkedId) {
                    case R.id.allRatings: rating = 0; break;
                    case R.id.oneStar: rating = 1; break;
                    case R.id.twoStars: rating = 2; break;
                    case R.id.threeStars: rating = 3; break;
                    case R.id.fourStars: rating = 4; break;
                    case R.id.fiveStars: rating = 5; break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + checkedId);
                }

                createList(rating);
            }
        });
        chipGroup.check(R.id.allRatings);

        final FloatingActionButton floatingActionButton = root.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    addReport();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        final Button button = root.findViewById(R.id.buttonDate);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        reports = Database.getDB(getContext()).getReports(Utils.getStartOfDay(calendar), Utils.getEndOfDay(calendar));
        createList(0);
    }

    public void createList(final int rating){

        List<Map<String, String>> data = new ArrayList<>();

        int count = 0;
        for (Report report:reports) {
            if(report.getRating() == rating || rating == 0) {

                Map<String, String> line = new HashMap<>(2);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(report.getDate());

                String secondLine = "Time: " + Utils.dateFormat(calendar, "HH:mm");
                if(report.getRating() != 0)
                    secondLine += "   " + report.getRating() + " ★";
                else
                    secondLine += "   No Rating";

                line.put("First Line", "Report #" + count);
                line.put("Second Line", secondLine);
                data.add(line);

            }
            count++;
        }

        SimpleAdapter adapter = new SimpleAdapter(getContext(), data,
                android.R.layout.simple_list_item_2,
                new String[] {"First Line", "Second Line" },
                new int[] {android.R.id.text1, android.R.id.text2 });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                for (Report report:reports) {
                    if (report.getRating() == rating || rating == 0) {
                        if(index == 0)
                            viewReport(report);

                         index--;
                    }
                }
            }
        });
    }

    public void selectDate(){
        Intent intent = new Intent(this.getContext(), GetDateActivity.class);
        startActivityForResult(intent, GET_DATE_REQUEST);
    }

    public void addReport() throws ParseException {
        Intent intent = new Intent(this.getActivity(), AddReportActivity.class);
        Bundle b = new Bundle();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ITALY);
        String dateString = title.getText().toString();
        Date date = sdf.parse(dateString);

        assert date != null;
        b.putLong("time", date.getTime());
        intent.putExtras(b);
        startActivity(intent);
    }

    public void viewReport(Report report){
        Intent intent = new Intent(this.getContext(), ViewReportActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("report", report);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case GET_DATE_REQUEST: {
                if(data != null){
                    calendar.setTimeInMillis(data.getExtras().getLong("date"));
                    title.setText(Utils.dateFormat(calendar, "dd MMMM yyyy"));
                }

                break;
            }
        }
    }
}