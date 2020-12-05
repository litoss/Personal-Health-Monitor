package com.example.personalhealthmonitor.Model.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.personalhealthmonitor.Model.Database;
import com.example.personalhealthmonitor.Model.Report;
import com.example.personalhealthmonitor.Model.Threshold;
import com.example.personalhealthmonitor.Model.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    private static PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        Database db = Database.getDB(context);
        List<Report> reports = db.getReports(Utils.getStartOfDay(calendar), Utils.getEndOfDay(calendar));
        List<Threshold> thresholds = db.getThresholds();
        List<Double> bodyTemperatures = new ArrayList<>();
        List<Integer> systolicPressures = new ArrayList<>();
        List<Integer> diastolicPressures = new ArrayList<>();
        List<Integer> glicemixIndexes = new ArrayList<>();

        NotificationService notificationHelper = new NotificationService(context);

        if(reports.size() == 0){
            notificationHelper.createRemind();
        }

        for(Report report : reports){
            if(report.getRating() > 3) {
                if (report.getBodyTemperature() != 0)
                    bodyTemperatures.add(report.getBodyTemperature());
                if (report.getSystolicPressure() != 0)
                    systolicPressures.add(report.getSystolicPressure());
                if (report.getDiastolicPressure() != 0)
                    diastolicPressures.add(report.getDiastolicPressure());
                if (report.getGlicemicIndex() != 0) glicemixIndexes.add(report.getGlicemicIndex());
            }
        }

        StringBuilder warnings = new StringBuilder();

        for(Threshold threshold : thresholds){
            if(threshold.getType().equals("Body temperature") && !bodyTemperatures.isEmpty() && (Utils.avgDouble(bodyTemperatures) < threshold.getMin() || Utils.avgDouble(bodyTemperatures) > threshold.getMax()) && threshold.isMonitored())
                warnings.append("La tua temperatura corporea è oltre ai limiti impostati\n");
            if(threshold.getType().equals("Systolic pressure") && !systolicPressures.isEmpty() && (Utils.avgInteger(systolicPressures) < threshold.getMin() || Utils.avgInteger(systolicPressures) > threshold.getMax()))
                warnings.append("La tua pressione sistolica è oltre ai limiti impostati\n");
            else if(threshold.getType().equals("Diastolic pressure") && !diastolicPressures.isEmpty() && (Utils.avgInteger(diastolicPressures) < threshold.getMin() || Utils.avgInteger(diastolicPressures) > threshold.getMax()))
                warnings.append("La tua pressione diastolica è oltre ai limiti impostati\n");
            if(threshold.getType().equals("Glycemic index") && !glicemixIndexes.isEmpty() && (Utils.avgInteger(glicemixIndexes) < threshold.getMin() || Utils.avgInteger(glicemixIndexes) > threshold.getMax()) && threshold.isMonitored())
                warnings.append("Il tuo indice glicemico è oltre ai limiti impostati\n");
        }

        if(warnings.length() != 0){
            notificationHelper.createMonitoredReport(warnings);
        }
    }

    public static void setRecurringAlarm(Context context, int hour) {

        Calendar updateTime = Calendar.getInstance();
        updateTime.set(Calendar.HOUR_OF_DAY, hour);
        updateTime.set(Calendar.MINUTE, 0);
        updateTime.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void cancelRecurringAlarm(Context context){
        AlarmManager alarms = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(pendingIntent);
    }
}