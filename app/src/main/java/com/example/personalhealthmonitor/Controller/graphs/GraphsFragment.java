package com.example.personalhealthmonitor.Controller.graphs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.APIlib;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.example.personalhealthmonitor.Model.Database;
import com.example.personalhealthmonitor.R;
import com.example.personalhealthmonitor.Model.Report;
import com.example.personalhealthmonitor.Model.Threshold;
import com.example.personalhealthmonitor.Model.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GraphsFragment extends Fragment {

    private AnyChartView pieChart, bodyTemperatureChart, bloodPressureChart, glycemicIndexChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_graph, container, false);

        Database db = Database.getDB(getContext());

        final List<Report> month = db.getReports(Utils.addDays(Calendar.getInstance(), -30), Calendar.getInstance().getTimeInMillis());
        final List<Report> week = db.getReports(Utils.addDays(Calendar.getInstance(), -7), Calendar.getInstance().getTimeInMillis());
        final List<Threshold> thresholds = db.getThresholds();

        pieChart = root.findViewById(R.id.pie_chart);
        APIlib.getInstance().setActiveAnyChartView(pieChart);
        createPieGraph(month, thresholds);

        bodyTemperatureChart = root.findViewById(R.id.body_temperature_chart);
        APIlib.getInstance().setActiveAnyChartView(bodyTemperatureChart);
        createBodyTemperatureGraph(week);

        bloodPressureChart = root.findViewById(R.id.blood_pressure_chart);
        APIlib.getInstance().setActiveAnyChartView(bloodPressureChart);
        createBloodPressureGraph(week);

        glycemicIndexChart = root.findViewById(R.id.glycemic_index_chart);
        APIlib.getInstance().setActiveAnyChartView(glycemicIndexChart);
        createGlycemicIndexGraph(week);

        return root;
    }

    public void createPieGraph(List<Report> reports, List<Threshold> thresholds){

        int good = 0, weak = 0, bad = 0;

        for(Report report : reports){
            int count = 0;
            for(Threshold threshold : thresholds){
                if(threshold.getType().equals("Body temperature") && (report.getBodyTemperature() < threshold.getMin() || report.getBodyTemperature() > threshold.getMax()))
                    count++;
                if(threshold.getType().equals("Systolic pressure") && (report.getSystolicPressure() < threshold.getMin() || report.getSystolicPressure() > threshold.getMax()))
                    count++;
                else if(threshold.getType().equals("Diastolic pressure") && (report.getDiastolicPressure() < threshold.getMin() || report.getDiastolicPressure() > threshold.getMax()))
                    count++;
                if(threshold.getType().equals("Glycemic index") && (report.getGlicemicIndex() < threshold.getMin() || report.getGlicemicIndex() > threshold.getMax()))
                    count++;
            }

            if(count == 0)
                good++;
            else if(count < 3)
                weak++;
            else
                bad++;
        }

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Report Buoni", good));
        data.add(new ValueDataEntry("Report Discreti", weak));
        data.add(new ValueDataEntry("Report Cattivi", bad));

        Pie pie = ReportsPie.ReportsPie(data);
        pieChart.setChart(pie);
    }

    public void createBodyTemperatureGraph(List<Report> reports) {

        List<DataEntry> seriesData = new ArrayList<>();
        for (Report report : reports) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(report.getDate());
            String date = Utils.dateFormat(calendar, "dd/MM");
            seriesData.add(new ValueDataEntry(date, report.getBodyTemperature()));
        }

        Cartesian cartesian = BodyTemperatureLineChart.BodyTemperatureLineChart(seriesData);
        bodyTemperatureChart.setChart(cartesian);
    }

    public void createBloodPressureGraph(List<Report> reports){

        List<DataEntry> seriesData = new ArrayList<>();
        for (Report report : reports) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(report.getDate());
            String date = Utils.dateFormat(calendar, "dd/MM");
            seriesData.add(new CustomDataEntry(date, report.getSystolicPressure(), report.getDiastolicPressure()));
        }

        Cartesian cartesian = BloodPressureLineChart.BloodPressureLineChart(seriesData);
        bloodPressureChart.setChart(cartesian);
    }

    public void createGlycemicIndexGraph(List<Report> reports){

        List<DataEntry> seriesData = new ArrayList<>();
        for (Report report : reports) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(report.getDate());
            String date = Utils.dateFormat(calendar, "dd/MM");
            seriesData.add(new ValueDataEntry(date, report.getGlicemicIndex()));
        }

        Cartesian cartesian = GlycemicIndexLineChart.GlycemicIndexLineChart(seriesData);
        glycemicIndexChart.setChart(cartesian);
    }

    private static class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }
    }
}