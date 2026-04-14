package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.InvoiceLineItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class StaffInvoiceBillDao {
    private final ServletContext ctx;

    public StaffInvoiceBillDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public BillDetail getByInvoiceId(String maHoaDon) throws Exception {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return null;
        BillDetail out = new BillDetail();

        String sql =
                "SELECT h.maHoaDon, h.ngay, h.trangThai, h.soGioChoi, h.tongTienDoAn, COALESCE(h.tienMay,0) AS tienMay, h.tongTien, " +
                "       kh.maKhachHang, kh.tenKhachHang, kh.sdt, kh.email, " +
                "       ls.thoiGianVao, ls.thoiGianRa, mt.maMay, mt.tenMay " +
                "FROM HoaDon h " +
                "LEFT JOIN KhachHang kh ON h.maKhachHang = kh.maKhachHang " +
                "LEFT JOIN LichSuSuDung ls ON h.maSuDung = ls.maSuDung " +
                "LEFT JOIN MayTinh mt ON ls.maMay = mt.maMay " +
                "WHERE h.maHoaDon = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                out.maHoaDon = rs.getString("maHoaDon");
                java.sql.Date ngay = rs.getDate("ngay");
                out.ngay = ngay != null ? ngay.toLocalDate() : null;
                out.trangThai = rs.getString("trangThai");
                out.soGioChoi = rs.getInt("soGioChoi");
                out.tongTienDoAn = rs.getDouble("tongTienDoAn");
                out.tienMay = rs.getDouble("tienMay");
                out.tongTien = rs.getDouble("tongTien");
                out.maKhachHang = rs.getString("maKhachHang");
                out.tenKhachHang = rs.getString("tenKhachHang");
                out.sdt = rs.getString("sdt");
                out.email = rs.getString("email");
                java.sql.Timestamp vao = rs.getTimestamp("thoiGianVao");
                java.sql.Timestamp ra = rs.getTimestamp("thoiGianRa");
                out.thoiGianVao = vao != null ? vao.toLocalDateTime() : null;
                out.thoiGianRa = ra != null ? ra.toLocalDateTime() : null;
                out.maMay = rs.getString("maMay");
                out.tenMay = rs.getString("tenMay");
            }
        }

        out.foods = new ArrayList<>();
        String sqlFood =
                "SELECT da.tenDoAn, hdda.soLuong, hdda.donGia, hdda.thanhTien " +
                "FROM HoaDonDoAn hdda " +
                "JOIN DoAn da ON hdda.maDoAn = da.maDoAn " +
                "WHERE hdda.maHDDoAn = ? " +
                "ORDER BY da.tenDoAn";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sqlFood)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceLineItem it = new InvoiceLineItem();
                    it.setMaHoaDon(maHoaDon);
                    it.setTen(rs.getString(1));
                    it.setSoLuong(rs.getInt(2));
                    it.setDonGia(rs.getDouble(3));
                    it.setThanhTien(rs.getDouble(4));
                    out.foods.add(it);
                }
            }
        }
        return out;
    }

    public static class BillDetail {
        public String maHoaDon;
        public java.time.LocalDate ngay;
        public String trangThai;
        public int soGioChoi;
        public double tienMay;
        public double tongTienDoAn;
        public double tongTien;
        public String maKhachHang;
        public String tenKhachHang;
        public String sdt;
        public String email;
        public String maMay;
        public String tenMay;
        public LocalDateTime thoiGianVao;
        public LocalDateTime thoiGianRa;
        public List<InvoiceLineItem> foods;
    }
}
