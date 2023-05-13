package com.example.aiforyou.custom;

import com.example.aiforyou.mytools.statisticscanvas.ExcelReader;

public class DataInfo {
    public String fileName, date;
    public ExcelReader reader;

    public DataInfo(String fileName, String date, ExcelReader reader) {
        this.fileName = fileName;
        this.date = date;
        this.reader = reader;
    }
}
