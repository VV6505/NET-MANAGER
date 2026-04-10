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
    /** Tiền game / dịch vụ game (cột HoaDon.tongTienGame) */
    private double tongTienGame;
    /**
     * Tiền giờ chơi ước lượng: soGioChoi × giá giờ máy (từ bảng MayTinh qua LichSuSuDung).
     * Phục vụ dashboard báo cáo; có thể = 0 nếu chưa gán máy hoặc giá giờ = 0.
     */
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

    public double getTongTienGame() {
        return tongTienGame;
    }

    public void setTongTienGame(double tongTienGame) {
        this.tongTienGame = tongTienGame;
    }

    public double getTienGioChoi() {
        return tienGioChoi;
    }

    public void setTienGioChoi(double tienGioChoi) {
        this.tienGioChoi = tienGioChoi;
    }

    /** Tổng một dòng báo cáo: tiền trong hóa đơn + tiền giờ (nếu tính được). */
    public double getTongCong() {
        return tongTien + tienGioChoi;
    }
}

