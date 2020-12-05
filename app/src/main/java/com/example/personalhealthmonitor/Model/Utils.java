package com.example.personalhealthmonitor.Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static long getStartOfDay(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static long getEndOfDay(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTimeInMillis();
    }

    public static long addDays(Calendar calendar, int days)
    {
        calendar.add(Calendar.DATE, days);
        return calendar.getTimeInMillis();
    }

    public static double avgDouble(List<Double> list){
        double sum = 0;
        for(Double elem : list) sum += elem;
        return sum / list.size();
    }

    public static double minDouble(List<Double> list){
        double min = list.get(0);
        for(double elem : list) if(elem < min) min = elem;
        return min;
    }

    public static double maxDouble(List<Double> list){
        double max = list.get(0);
        for(double elem : list) if(elem > max) max = elem;
        return max;
    }

    public static int avgInteger(List<Integer> list){
        Integer sum = 0;
        for(Integer elem : list) sum += elem;
        return sum / list.size();
    }

    public static int minInteger(List<Integer> list){
        Integer min = list.get(0);
        for(Integer elem : list) if(elem < min) min = elem;
        return min;
    }

    public static int maxInteger(List<Integer> list){
        Integer max = list.get(0);
        for(Integer elem : list) if(elem > max) max = elem;
        return max;
    }

    public static String dateFormat(Calendar calendar, String dateFormat) {

        return new SimpleDateFormat(dateFormat, Locale.ITALY).format(calendar.getTime());
    }
}
