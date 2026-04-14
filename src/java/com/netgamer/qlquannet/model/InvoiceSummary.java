package com.netgamer.qlquannet.model;

import java.time.LocalDate;

public class InvoiceSummary {
    private String maHoaDon;
    private LocalDate ngay;
    private String trangThai;
    private String maKhachHang;
    private String tenKhachHang;
    private int soGioChoi;
    private double tongTien;
    /** Tiền đồ ăn trên hóa đơn (cột HoaDon.tongTienDoAn) */
    private double tongTienDoAn;
    /** Tiền thuê máy trên hóa đơn. */
    private double tienGioChoi;

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public int getSoGioChoi() {
        return soGioChoi;
    }

    public void setSoGioChoi(int soGioChoi) {
        this.soGioChoi = soGioChoi;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public double getTongTienDoAn() {
        return tongTienDoAn;
    }

    public void setTongTienDoAn(double tongTienDoAn) {
        this.tongTienDoAn = tongTienDoAn;
    }

    public double getTienGioChoi() {
        return tienGioChoi;
    }

    public void setTienGioChoi(double tienGioChoi) {
        this.tienGioChoi = tienGioChoi;
    }

    /** Tổng một dòng báo cáo (đã gồm tiền máy + đồ ăn). */
    public double getTongCong() {
        return tongTien;
    }
}

