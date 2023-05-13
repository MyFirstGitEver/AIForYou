package com.example.aiforyou.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class ShareEntity implements Parcelable {
    private int idNguoiNhan, idNguoiGui;
    private Date sentDate;
    private Integer idProject;

    public ShareEntity() {

    }

    public ShareEntity(int idNguoiNhan, int idNguoiGui, Integer idProject, Date sentDate) {
        this.idNguoiNhan = idNguoiNhan;
        this.idNguoiGui = idNguoiGui;
        this.idProject = idProject;
        this.sentDate = sentDate;
    }

    protected ShareEntity(Parcel in) {
        idNguoiNhan = in.readInt();
        idNguoiGui = in.readInt();
        idProject = in.readInt();
        sentDate = new Date(in.readLong());
    }

    public static final Creator<ShareEntity> CREATOR = new Creator<ShareEntity>() {
        @Override
        public ShareEntity createFromParcel(Parcel in) {
            return new ShareEntity(in);
        }

        @Override
        public ShareEntity[] newArray(int size) {
            return new ShareEntity[size];
        }
    };

    public int getIdNguoiNhan() {
        return idNguoiNhan;
    }

    public int getIdNguoiGui() {
        return idNguoiGui;
    }

    public Integer getIdProject() {
        return idProject;
    }

    public void setIdNguoiNhan(int idNguoiNhan) {
        this.idNguoiNhan = idNguoiNhan;
    }

    public void setIdNguoiGui(int idNguoiGui) {
        this.idNguoiGui = idNguoiGui;
    }

    public void setIdProject(Integer idProject) {
        this.idProject = idProject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(idNguoiNhan);
        dest.writeInt(idNguoiGui);
        dest.writeInt(idProject);
        dest.writeLong(sentDate.getTime());
    }
}