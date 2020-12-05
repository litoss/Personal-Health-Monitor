package com.example.personalhealthmonitor.Controller.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.personalhealthmonitor.Model.Database;
import com.example.personalhealthmonitor.Model.Report;
import com.example.personalhealthmonitor.Model.Threshold;
import com.example.personalhealthmonitor.Model.Utils;
import com.example.personalhealthmonitor.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    LinearLayout status, summary;
    List<Report> reports;
    List<Threshold> thresholds;
    List<String> warnings = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        status = root.findViewById(R.id.status);
        summary = root.findViewById(R.id.summary);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateView();
    }

    public void populateView(){
        Calendar calendar = Calendar.getInstance();
        reports = Database.getDB(getContext()).getReports(Utils.getStartOfDay(calendar), Utils.getEndOfDay(calendar));
        thresholds = Database.getDB(getContext()).getThresholds();

        List<Double> bodyTemperatures = new ArrayList<>();
        List<Integer> systolicPressures = new ArrayList<>();
        List<Integer> diastolicPressures = new ArrayList<>();
        List<Integer> glicemixIndexes = new ArrayList<>();

        int maxRating = 0;
        String comment = "";

        status.removeAllViews();
        summary.removeAllViews();
        warnings.clear();
        if(reports.isEmpty())
            warnings.add("Non hai inserito ancora nessun report oggi");
        for(Report report : reports){
            if(report.getBodyTemperature() != 0) bodyTemperatures.add(report.getBodyTemperature());
            if(report.getSystolicPressure() != 0) systolicPressures.add(report.getSystolicPressure());
            if(report.getDiastolicPressure() != 0) diastolicPressures.add(report.getDiastolicPressure());
            if(report.getGlicemicIndex() != 0) glicemixIndexes.add(report.getGlicemicIndex());
            if(report.getRating() > maxRating && !report.getComment().isEmpty()) {
                maxRating = report.getRating();
                comment = "\"" + report.getComment() + "\"";
            }
        }

        createBodyTemperatureSummary(bodyTemperatures);
        createBloodPressureSummary(systolicPressures, diastolicPressures);
        createGlycemicIndexSummary(glicemixIndexes);
        summary.addView(createCommentView(comment));
        status.addView(createStatusView(warnings));
    }

    private void createGlycemicIndexSummary(List<Integer> glicemixIndexes) {

        int avg, min, max;

        if(glicemixIndexes.isEmpty()){
            avg = min = max = 0;
        }else{
            avg = Utils.avgInteger(glicemixIndexes);
            min = Utils.minInteger(glicemixIndexes);
            max = Utils.maxInteger(glicemixIndexes);

            for(Threshold threshold : thresholds){
                if(threshold.getType().equals("Glycemic index")){
                    if(avg < threshold.getMin())
                        warnings.add("Il tuo indice glicemico è più basso della soglia impostata");
                    if(avg > threshold.getMax())
                        warnings.add("Il tuo indice glicemico è più alto della soglia impostata");
                }
            }
        }
        summary.addView(createSummaryView("Indice Glicemico", R.drawable.ic_read_more_18, String.valueOf(avg), String.valueOf(min), String.valueOf(max)));
    }

    public void createBloodPressureSummary(List<Integer> systolicPressure, List<Integer> diastolicPressure){

        int avgDiastolic, avgSystolic, minDiastolic, minSystolic, maxDiastolic, maxSystolic;

        if(diastolicPressure.isEmpty()){
            avgDiastolic = avgSystolic =  minDiastolic = minSystolic = maxDiastolic = maxSystolic = 0;
        }else{
            avgDiastolic = Utils.avgInteger(diastolicPressure);
            avgSystolic = Utils.avgInteger(systolicPressure);
            minDiastolic = Utils.minInteger(diastolicPressure);
            minSystolic = Utils.minInteger(systolicPressure);
            maxDiastolic = Utils.maxInteger(diastolicPressure);
            maxSystolic = Utils.maxInteger(systolicPressure);

            for(Threshold threshold : thresholds){
                if(threshold.getType().equals("Systolic pressure")){
                    if(avgSystolic < threshold.getMin())
                        warnings.add("La tua pressione sanguigna è più bassa della soglia impostata");
                    if(avgSystolic > threshold.getMax())
                        warnings.add("La tua pressione sanguigna è più alta della soglia impostata");
                }else if(threshold.getType().equals("Diastolic pressure")){
                    if(avgDiastolic < threshold.getMin())
                        warnings.add("La tua pressione sanguigna è più bassa della soglia impostata");
                    if(avgDiastolic > threshold.getMax())
                        warnings.add("La tua pressione sanguigna è più alta della soglia impostata");
                }
            }
        }
        summary.addView(createSummaryView("Pressione Sanguigna", R.drawable.ic_favorite_18, avgSystolic + "/" + avgDiastolic, minSystolic + "/" + minDiastolic, maxSystolic + "/" + maxDiastolic));
    }

    public void createBodyTemperatureSummary(List<Double> bodyTemperatures){

        double avg, min, max;

        if(bodyTemperatures.isEmpty())
            avg = min = max = 0;
        else{
            avg = Utils.avgDouble(bodyTemperatures);
            min = Utils.minDouble(bodyTemperatures);
            max = Utils.maxDouble(bodyTemperatures);

            for(Threshold threshold : thresholds){
                if(threshold.getType().equals("Body temperature")){
                    if(avg < threshold.getMin())
                        warnings.add("La tua Temperatura corporea è più bassa della soglia impostata");
                    if(avg > threshold.getMax())
                        warnings.add("La tua Temperatura corporea è più alta della soglia impostata");
                }
            }
        }
        summary.addView(createSummaryView("Temperatura Corporea", R.drawable.ic_temperature_18, String.valueOf(avg), String.valueOf(min), String.valueOf(max)));
    }

    public View createSummaryView(String title, int icon, String avg, String min, String max){
        LayoutInflater li = LayoutInflater.from(getContext());
        View view = li.inflate(R.layout.card_summary, null);

        ((TextView) view.findViewById(R.id.title)).setText(title);
        ((TextView) view.findViewById(R.id.title)).setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0,0);
        ((TextView) view.findViewById(R.id.avg)).setText(avg);
        ((TextView) view.findViewById(R.id.min)).setText(min);
        ((TextView) view.findViewById(R.id.max)).setText(max);

        return view;
    }

    private View createCommentView(String comment) {

        if(comment.isEmpty())
            comment = "Non sono stati inseriti commenti oggi";

        LayoutInflater li = LayoutInflater.from(getContext());
        View view = li.inflate(R.layout.card_comment, null);
        ((TextView) view.findViewById(R.id.title)).setText("Commento più importante");
        ((TextView) view.findViewById(R.id.title)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_comment_18, 0, 0,0);
        ((TextView) view.findViewById(R.id.comment)).setText(comment);

        return view;
    }

    public View createStatusView(List<String> warnings) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View view = li.inflate(R.layout.card_status, null);

        StringBuilder subtext = new StringBuilder();
        for (String warn : warnings)
            subtext.append(warn).append("\n");

        if (warnings.size() == 0) {
            ((TextView) view.findViewById(R.id.title)).setText(R.string.TuttoBene);
            ((TextView) view.findViewById(R.id.subtitle)).setText(R.string.ContinuaCosì);
            view.findViewById(R.id.card).setBackgroundColor(Color.rgb(50, 168, 82));
        } else if (warnings.size() < 3){
            ((TextView) view.findViewById(R.id.title)).setText(R.string.Attenzione);
            ((TextView) view.findViewById(R.id.subtitle)).setText(subtext);
            view.findViewById(R.id.card).setBackgroundColor(Color.rgb(254, 201, 1));
        }
        else {
            ((TextView) view.findViewById(R.id.title)).setText(R.string.Attenzione);
            ((TextView) view.findViewById(R.id.subtitle)).setText(subtext);
            view.findViewById(R.id.card).setBackgroundColor(Color.rgb(129,0 ,39));
        }

        return view;
    }
}