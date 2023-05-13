package com.example.aiforyou.custom;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.aiforyou.mytools.Vector;

public class RegressionParams implements Parcelable {
    private Vector w;
    private float b;
    private float accuracy;

    public RegressionParams() {

    }

    protected RegressionParams(Parcel in) {
        b = in.readFloat();
        accuracy = in.readFloat();
        w = new Vector(in.createDoubleArray());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(b);
        dest.writeFloat(accuracy);
        dest.writeDoubleArray(w.getPoints());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RegressionParams> CREATOR = new Creator<RegressionParams>() {
        @Override
        public RegressionParams createFromParcel(Parcel in) {
            return new RegressionParams(in);
        }

        @Override
        public RegressionParams[] newArray(int size) {
            return new RegressionParams[size];
        }
    };

    public Vector getW() {
        return w;
    }

    public float getB() {
        return b;
    }

    public float getError() {
        return accuracy;
    }

    public void setW(Vector w) {
        this.w = w;
    }

    public void setB(float b) {
        this.b = b;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}