package com.example.personalhealthmonitor.Model;

public class Threshold {

    String type;
    double min;
    double max;
    boolean monitored;

    public Threshold(String type, double min, double max, boolean monitored) {
        this.type = type;
        this.min = min;
        this.max = max;
        this.monitored = monitored;
    }

    public String getType() {
        return type;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public boolean isMonitored() {
        return monitored;
    }

    public void setMonitored(boolean monitored) {
        this.monitored = monitored;
    }
}
