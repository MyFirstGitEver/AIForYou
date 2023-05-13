package com.example.aiforyou.custom;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class ShareDTO implements Parcelable {
    private String tenNguoiGui, anhNguoiGui, tenProject;
    private Integer idProject;

    private Date sentDate;

    public ShareDTO() {

    }

    protected ShareDTO(Parcel in) {
        tenNguoiGui = in.readString();
        anhNguoiGui = in.readString();
        tenProject = in.readString();
        if (in.readByte() == 0) {
            idProject = null;
        } else {
            idProject = in.readInt();
        }

        sentDate = new Date(in.readLong());
    }

    public static final Creator<ShareDTO> CREATOR = new Creator<ShareDTO>() {
        @Override
        public ShareDTO createFromParcel(Parcel in) {
            return new ShareDTO(in);
        }

        @Override
        public ShareDTO[] newArray(int size) {
            return new ShareDTO[size];
        }
    };

    public String getTenNguoiGui() {
        return tenNguoiGui;
    }

    public String getAnhNguoiGui() {
        return anhNguoiGui;
    }

    public String getTenProject() {
        return tenProject;
    }

    public Integer getIdProject() {
        return idProject;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setTenNguoiGui(String tenNguoiGui) {
        this.tenNguoiGui = tenNguoiGui;
    }

    public void setAnhNguoiGui(String anhNguoiGui) {
        this.anhNguoiGui = anhNguoiGui;
    }

    public void setIdProject(Integer idProject) {
        this.idProject = idProject;
    }

    public void setTenProject(String tenProject) {
        this.tenProject = tenProject;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(tenNguoiGui);
        dest.writeString(anhNguoiGui);
        dest.writeString(tenProject);
        if (idProject == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(idProject);
        }

        dest.writeLong(sentDate.getTime());
    }
}