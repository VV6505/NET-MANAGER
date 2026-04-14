package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.InvoiceLineItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class InvoiceDetailDao {
    private final ServletContext ctx;

    public InvoiceDetailDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public InvoiceDetail getPaidInvoiceDetailForCustomer(String maHoaDon, String maKhachHang) throws Exception {
        if (maHoaDon == null || maKhachHang == null) return null;

        InvoiceDetail out = new InvoiceDetail();

        String sqlHd =
                "SELECT h.maHoaDon, h.ngay, h.ngayDat, h.trangThai, h.tongTienDoAn, COALESCE(h.tienMay, 0) AS tienMay, h.tongTien, " +
                "       kh.maKhachHang, kh.tenKhachHang, kh.sdt, kh.email " +
                "FROM HoaDon h " +
                "JOIN KhachHang kh ON h.maKhachHang = kh.maKhachHang " +
                "WHERE h.maHoaDon = ? AND h.maKhachHang = ? AND h.trangThai = N'Đã thanh toán'";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sqlHd)) {
            ps.setString(1, maHoaDon);
            ps.setString(2, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                out.maHoaDon = rs.getString("maHoaDon");
                java.sql.Date ngay = rs.getDate("ngay");
                out.ngay = ngay != null ? ngay.toLocalDate() : null;
                out.trangThai = rs.getString("trangThai");
                out.tenKhachHang = rs.getString("tenKhachHang");
                out.sdt = rs.getString("sdt");
                out.email = rs.getString("email");
                out.tongTienDoAn = rs.getDouble("tongTienDoAn");
                out.tienMay = rs.getDouble("tienMay");
                out.tongTien = rs.getDouble("tongTien");
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

    public static class InvoiceDetail {
        public String maHoaDon;
        public LocalDate ngay;
        public String trangThai;
        public String tenKhachHang;
        public String sdt;
        public String email;
        public double tongTienDoAn;
        public double tienMay;
        public double tongTien;
        public List<InvoiceLineItem> foods;
    }
}

