package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.InvoiceSummary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class InvoiceDao {
    private final ServletContext ctx;

    public InvoiceDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public List<InvoiceSummary> search(LocalDate from, LocalDate to, String customerName) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT h.maHoaDon, h.ngay, h.trangThai, h.maKhachHang, kh.tenKhachHang, h.soGioChoi, h.tongTien " +
                "FROM HoaDon h " +
                "LEFT JOIN KhachHang kh ON h.maKhachHang = kh.maKhachHang " +
                "WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();
        if (from != null) {
            sql.append(" AND h.ngay >= ?");
            params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND h.ngay <= ?");
            params.add(java.sql.Date.valueOf(to));
        }
        if (customerName != null && !customerName.trim().isEmpty()) {
            sql.append(" AND kh.tenKhachHang LIKE ?");
            params.add("%" + customerName.trim() + "%");
        }
        sql.append(" ORDER BY h.ngay DESC, h.maHoaDon DESC");

        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<InvoiceSummary> out = new ArrayList<>();
                while (rs.next()) {
                    InvoiceSummary dto = new InvoiceSummary();
                    dto.setMaHoaDon(rs.getString("maHoaDon"));
                    java.sql.Date ngay = rs.getDate("ngay");
                    dto.setNgay(ngay != null ? ngay.toLocalDate() : null);
                    dto.setTrangThai(rs.getString("trangThai"));
                    dto.setMaKhachHang(rs.getString("maKhachHang"));
                    dto.setTenKhachHang(rs.getString("tenKhachHang"));
                    dto.setSoGioChoi(rs.getInt("soGioChoi"));
                    dto.setTongTien(rs.getDouble("tongTien"));
                    out.add(dto);
                }
                return out;
            }
        }
    }
}

