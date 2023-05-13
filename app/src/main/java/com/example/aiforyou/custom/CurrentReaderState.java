package com.example.aiforyou.custom;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CurrentReaderState implements Parcelable {
    private String currentQuery;
    private int lastSeen;


    public CurrentReaderState() {
        currentQuery = "";
    }

    public CurrentReaderState(Parcel in) {
        currentQuery = in.readString();
        lastSeen = in.readInt();
    }

    public static final Creator<CurrentReaderState> CREATOR = new Creator<CurrentReaderState>() {
        @Override
        public CurrentReaderState createFromParcel(Parcel in) {
            return new CurrentReaderState(in);
        }

        @Override
        public CurrentReaderState[] newArray(int size) {
            return new CurrentReaderState[size];
        }
    };

    public String getCurrentQuery() {
        return currentQuery;
    }

    public int getLastSeen() {
        return lastSeen;
    }

    public void setCurrentQuery(String currentQuery) {
        this.currentQuery = currentQuery;
    }

    public void setLastSeen(int lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void reset() {
        currentQuery = "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(currentQuery);
        dest.writeInt(lastSeen);
    }
}
