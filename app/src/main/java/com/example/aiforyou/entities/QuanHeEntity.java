package com.example.aiforyou.entities;

import java.util.Date;

public class QuanHeEntity {
    private int idA;
    private int idB;

    private Date requestDate;
    private boolean accepted;

    public QuanHeEntity() {

    }

    public int getIdA() {
        return idA;
    }

    public int getIdB() {
        return idB;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
