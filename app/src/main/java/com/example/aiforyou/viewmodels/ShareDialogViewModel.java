package com.example.aiforyou.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.aiforyou.custom.QuanHeDTO;

public class ShareDialogViewModel extends ViewModel {
    private QuanHeDTO[] quanHes = null;
    private int projectId;

    public QuanHeDTO[] getQuanHes() {
        return quanHes;
    }

    public void setQuanHes(QuanHeDTO[] quanHes) {
        this.quanHes = quanHes;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}