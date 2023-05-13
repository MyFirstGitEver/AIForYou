package com.example.aiforyou.custom;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class UserDTO implements Parcelable {
    private Integer id;
    private String tenDn, avatar;

    private List<ProjectDTO> projects;

    public UserDTO() {
    }

    protected UserDTO(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        tenDn = in.readString();
        avatar = in.readString();
        projects = in.createTypedArrayList(ProjectDTO.CREATOR);
    }

    public static final Creator<UserDTO> CREATOR = new Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public String getTenDn() {
        return tenDn;
    }

    public String getAvatar() {
        return avatar;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTenDn(String tenDn) {
        this.tenDn = tenDn;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
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
        dest.writeString(tenDn);
        dest.writeString(avatar);
        dest.writeTypedList(projects);
    }
}