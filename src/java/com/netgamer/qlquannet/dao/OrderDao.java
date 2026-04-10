package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletContext;

public class OrderDao {
    private final ServletContext ctx;

    public OrderDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public String addFoodToCustomerOrder(String maKhachHang, String maDoAn, int soLuong) throws Exception {
        String sql = "EXEC sp_ThemMonAnTheoKhachHang ?, ?, ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ps.setString(2, maDoAn);
            ps.setInt(3, soLuong);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString(1);
            }
        }
    }

    public String updateOrderStatus(String maHoaDon, String trangThai) throws Exception {
        String sql = "EXEC sp_CapNhatTrangThaiHoaDon ?, ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.setString(2, trangThai);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString(1);
            }
        }
    }

    public double getTongTienDoAn(String maHoaDon) throws Exception {
        String sql = "SELECT tongTienDoAn FROM HoaDon WHERE maHoaDon = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return 0.0;
                return rs.getDouble("tongTienDoAn");
            }
        }
    }
}

