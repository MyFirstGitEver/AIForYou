package com.example.aiforyou.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.aiforyou.mytools.statisticscanvas.ExcelReader;

import java.util.ArrayList;
import java.util.List;

public class ClusterResultViewModel extends ViewModel {
    private int currentGroupIndex = 0;
    private int currentTabIndex = 0;
    private int projectPosition = 0;

    private int[] indexes;

    private ExcelReader reader;

    public void setCurrentGroupIndex(int currentGroupIndex) {
        this.currentGroupIndex = currentGroupIndex;
    }

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    public void setCurrentTabIndex(int currentTabIndex) {
        this.currentTabIndex = currentTabIndex;
    }

    public int getProjectPosition() {
        return projectPosition;
    }

    public void setProjectPosition(int projectPosition) {
        this.projectPosition = projectPosition;
    }

    public ExcelReader getReader() {
        return reader;
    }

    public void setReader(ExcelReader reader) {
        this.reader = reader;
    }

    public void setIndexes(int[] indexes) {
        this.indexes = indexes;
    }

    public boolean notInitialised() {
        return reader == null;
    }

    public String[] getGroupData() {
        List<String> data = new ArrayList<>();

        for(int i=1;i<=indexes.length;i++) {
            if(indexes[i - 1] == currentGroupIndex) {
                data.add(reader.getDataFromColumn(i, currentTabIndex));
            }
        }

        return data.toArray(new String[0]);
    }
}