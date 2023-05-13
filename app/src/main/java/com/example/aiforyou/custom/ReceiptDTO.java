package com.example.aiforyou.custom;

import com.example.aiforyou.interfaces.DetailsItem;

import java.util.Date;

public class ReceiptDTO implements DetailsItem {
    private String name;
    private Date buyDate;
    private boolean expired, periodic;

    public ReceiptDTO() {

    }

    public String getName() {
        return name;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public boolean isExpired() {
        return expired;
    }

    public boolean isPeriodic() {
        return periodic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }
}