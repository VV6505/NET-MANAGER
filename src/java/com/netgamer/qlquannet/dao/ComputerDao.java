package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.Computer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class ComputerDao {
    private final ServletContext ctx;

    public ComputerDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public List<Computer> findAll() throws Exception {
        String sql = "SELECT m.maMay, m.tenMay, m.trangThai, m.maKhu, k.tenKhu, " +
                "COALESCE(m.maKhachHang, '') as maKhachHang, " +
                "COALESCE(kh.tenKhachHang, '') as tenKhachHang, " +
                "COALESCE(kh.soGioChoi, 0) as soGioChoi, " +
                "COALESCE(m.giaGio, 0) as giaGio " +
                "FROM MayTinh m " +
                "LEFT JOIN KhuVuc k ON m.maKhu = k.maKhu " +
                "LEFT JOIN KhachHang kh ON m.maKhachHang = kh.maKhachHang " +
                "ORDER BY m.maMay";

        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Computer> out = new ArrayList<>();
            while (rs.next()) {
                Computer c = new Computer();
                c.setMaMay(rs.getString("maMay"));
                c.setTenMay(rs.getString("tenMay"));
                c.setTrangThai(rs.getString("trangThai"));
                c.setMaKhu(rs.getString("maKhu"));
                c.setTenKhu(rs.getString("tenKhu"));
                c.setMaKhachHang(rs.getString("maKhachHang"));
                c.setTenKhachHang(rs.getString("tenKhachHang"));
                c.setSoGioChoi(rs.getDouble("soGioChoi"));
                c.setGiaGio(rs.getDouble("giaGio"));
                out.add(c);
            }
            return out;
        }
    }

    public void updateStatus(String maMay, String trangThai) throws Exception {
        String sql = "UPDATE MayTinh SET trangThai = ? WHERE maMay = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maMay);
            ps.executeUpdate();
        }
    }
}

