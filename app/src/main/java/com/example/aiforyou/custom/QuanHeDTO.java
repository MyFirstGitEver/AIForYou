package com.example.aiforyou.custom;

public class QuanHeDTO {
    private int idNguoiNhan;
    private String tenNguoiNhan;

    public QuanHeDTO() {

    }

    public QuanHeDTO(int idNguoiNhan, String tenNguoiNhan) {
        this.idNguoiNhan = idNguoiNhan;
        this.tenNguoiNhan = tenNguoiNhan;
    }

    public int getIdNguoiNhan() {
        return idNguoiNhan;
    }

    public String getTenNguoiNhan() {
        return tenNguoiNhan;
    }
}
