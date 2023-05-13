package com.example.aiforyou.viewmodels;

import androidx.lifecycle.ViewModel;

public class WorkSpaceFragmentViewModel extends ViewModel {
    private int projectId;
    private String query;


    public int getProjectPosition() {
        return projectId;
    }

    public String getQuery() {
        return query;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}