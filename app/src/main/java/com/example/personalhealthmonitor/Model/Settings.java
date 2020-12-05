package com.example.personalhealthmonitor.Model;

public class Settings {
    Boolean notifications;
    int hour;

    public Settings(Boolean notifications, int hour) {
        this.notifications = notifications;
        this.hour = hour;
    }

    public Boolean getNotifications() {
        return notifications;
    }

    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
}
