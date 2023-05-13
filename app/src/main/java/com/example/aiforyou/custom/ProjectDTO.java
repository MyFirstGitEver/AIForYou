package com.example.aiforyou.custom;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.aiforyou.interfaces.DetailsItem;

import java.util.Date;

public class ProjectDTO implements DetailsItem, Parcelable {
    protected ProjectDTO(Parcel in) {
        id = in.readInt();
        name = in.readString();
        diaChiExcel = in.readString();
        sharedByName = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        type = ProjectType.values()[in.readInt()];
        date = new Date(in.readLong());
    }

    public static final Creator<ProjectDTO> CREATOR = new Creator<ProjectDTO>() {
        @Override
        public ProjectDTO createFromParcel(Parcel in) {
            return new ProjectDTO(in);
        }

        @Override
        public ProjectDTO[] newArray(int size) {
            return new ProjectDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(diaChiExcel);
        dest.writeString(sharedByName);
        dest.writeParcelable(uri, flags);
        dest.writeInt(type.ordinal());
        dest.writeLong(date.getTime());
    }

    public enum ProjectType {
        STATS,
        ML,
        PIE,
        HIST1,
        HIST2,
        SCATTER,
        SEGMENTS,
        LR,
        LOR,
        CLUSTERING,
        NN
    }

    private Integer id;
    private String name, diaChiExcel, sharedByName;
    private Uri uri;
    private ProjectType type;
    private Date date;

    public ProjectDTO() {

    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProjectType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public Uri getUri() {
        return uri;
    }

    public String getDiaChiExcel() {
        return diaChiExcel;
    }

    public String getSharedByName() {
        return sharedByName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setDiaChiExcel(String diaChiExcel) {
        this.diaChiExcel = diaChiExcel;
    }

    public void setSharedByName(String sharedByName) {
        this.sharedByName = sharedByName;
    }
}