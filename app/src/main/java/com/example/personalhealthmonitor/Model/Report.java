package com.example.personalhealthmonitor.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Report implements Parcelable  {
    private final int id;
    private final long date;
    private final double bodyTemperature;
    private final int diastolicPressure;
    private final int systolicPressure;
    private final int glicemicIndex;
    private final String comment;
    private final int rating;

    public Report(int id, long date, double bodyTemperature,  int systolicPressure, int diastolicPressure, int glicemicIndex, String comment, int rating) {
        this.id = id;
        this.date = date;
        this.bodyTemperature = bodyTemperature;
        this.systolicPressure = systolicPressure;
        this.diastolicPressure = diastolicPressure;
        this.glicemicIndex = glicemicIndex;
        this.comment = comment;
        this.rating = rating;
    }

    public Report(Parcel in) {
        this.id = in.readInt();
        this.date = in.readLong();
        this.bodyTemperature = in.readDouble();
        this.systolicPressure = in.readInt();
        this.diastolicPressure = in.readInt();
        this.glicemicIndex = in.readInt();
        this.comment = in.readString();
        this.rating = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    public int getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public double getBodyTemperature() {
        return bodyTemperature;
    }

    public int getSystolicPressure() {
        return systolicPressure;
    }

    public int getDiastolicPressure() {
        return diastolicPressure;
    }

    public int getGlicemicIndex() {
        return glicemicIndex;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeLong(date);
        parcel.writeDouble(bodyTemperature);
        parcel.writeInt(systolicPressure);
        parcel.writeInt(diastolicPressure);
        parcel.writeInt(glicemicIndex);
        parcel.writeString(comment);
        parcel.writeInt(rating);
    }
}
