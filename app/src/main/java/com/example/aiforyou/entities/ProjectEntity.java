package com.example.aiforyou.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.aiforyou.interfaces.DetailsItem;

import java.util.Date;

public class ProjectEntity implements Parcelable{
    private Integer id, idSoHuu;
    private String tenProject, diaChiExcel;

    private Date thoiGianKhoiTao;

    public ProjectEntity() {

    }

    public ProjectEntity(Integer idSoHuu, String tenProject, String diaChiExcel, Date thoiGianKhoiTao) {
        this.tenProject = tenProject;
        this.diaChiExcel = diaChiExcel;
        this.thoiGianKhoiTao = thoiGianKhoiTao;
        this.idSoHuu = idSoHuu;
    }

    protected ProjectEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        idSoHuu = in.readInt();
        tenProject = in.readString();
        diaChiExcel = in.readString();
        thoiGianKhoiTao = new Date(in.readLong());
    }

    public static final Creator<ProjectEntity> CREATOR = new Creator<ProjectEntity>() {
        @Override
        public ProjectEntity createFromParcel(Parcel in) {
            return new ProjectEntity(in);
        }

        @Override
        public ProjectEntity[] newArray(int size) {
            return new ProjectEntity[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public String getTenProject() {
        return tenProject;
    }

    public String getDiaChiExcel() {
        return diaChiExcel;
    }

    public Date getThoiGianKhoiTao() {
        return thoiGianKhoiTao;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTenProject(String tenProject) {
        this.tenProject = tenProject;
    }

    public void setDiaChiExcel(String diaChiExcel) {
        this.diaChiExcel = diaChiExcel;
    }

    public void setThoiGianKhoiTao(Date thoiGianKhoiTao) {
        this.thoiGianKhoiTao = thoiGianKhoiTao;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeInt(idSoHuu);
        dest.writeString(tenProject);
        dest.writeString(diaChiExcel);
        dest.writeLong(thoiGianKhoiTao.getTime());
    }
}