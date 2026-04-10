package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class CustomerDao {
    private final ServletContext ctx;

    public CustomerDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public boolean existsBySdt(String sdt) throws Exception {
        String sql = "SELECT COUNT(*) AS c FROM KhachHang WHERE sdt = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sdt);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                return rs.getInt("c") > 0;
            }
        }
    }

    public Customer findBySdtAndPassword(String sdt, String password) throws Exception {
        String sql =
                "SELECT kh.maKhachHang, kh.tenKhachHang, kh.sdt, kh.email, kh.soGioChoi, kh.soDu, kh.maLoaiKhachHang, lkh.tenLoaiKhachHang " +
                "FROM KhachHang kh " +
                "LEFT JOIN LoaiKhachHang lkh ON kh.maLoaiKhachHang = lkh.maLoaiKhachHang " +
                "WHERE kh.sdt = ? AND kh.matKhau = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sdt);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
    }

    public Customer findBySdt(String sdt) throws Exception {
        String sql =
                "SELECT kh.maKhachHang, kh.tenKhachHang, kh.sdt, kh.email, kh.soGioChoi, kh.soDu, kh.maLoaiKhachHang, lkh.tenLoaiKhachHang " +
                "FROM KhachHang kh " +
                "LEFT JOIN LoaiKhachHang lkh ON kh.maLoaiKhachHang = lkh.maLoaiKhachHang " +
                "WHERE kh.sdt = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sdt);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
    }

    public List<Customer> findAll() throws Exception {
        String sql =
                "SELECT kh.maKhachHang, kh.tenKhachHang, kh.sdt, kh.email, kh.soGioChoi, kh.soDu, kh.maLoaiKhachHang, lkh.tenLoaiKhachHang " +
                "FROM KhachHang kh " +
                "LEFT JOIN LoaiKhachHang lkh ON kh.maLoaiKhachHang = lkh.maLoaiKhachHang " +
                "ORDER BY kh.maKhachHang";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Customer> out = new ArrayList<>();
            while (rs.next()) {
                out.add(map(rs));
            }
            return out;
        }
    }

    public Customer createCustomer(String tenKhachHang, String sdt, String email, String password) throws Exception {
        String newId = generateNewCustomerId();
        String sql =
                "INSERT INTO KhachHang (maKhachHang, tenKhachHang, sdt, email, matKhau, soGioChoi, soDu, maLoaiKhachHang) " +
                "VALUES (?, ?, ?, ?, ?, 0, 0, 'LK0001')";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newId);
            ps.setString(2, tenKhachHang);
            ps.setString(3, sdt);
            ps.setString(4, email);
            ps.setString(5, password);
            ps.executeUpdate();
        }
        return findBySdt(sdt);
    }

    public void updateBySdt(String sdt, String tenKhachHang, String email, String matKhauMoiOrNull) throws Exception {
        if (matKhauMoiOrNull != null && !matKhauMoiOrNull.trim().isEmpty()) {
            String sql = "UPDATE KhachHang SET tenKhachHang = ?, email = ?, matKhau = ? WHERE sdt = ?";
            try (Connection con = Db.getConnection(ctx);
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, tenKhachHang);
                ps.setString(2, email);
                ps.setString(3, matKhauMoiOrNull);
                ps.setString(4, sdt);
                ps.executeUpdate();
            }
            return;
        }

        String sql = "UPDATE KhachHang SET tenKhachHang = ?, email = ? WHERE sdt = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenKhachHang);
            ps.setString(2, email);
            ps.setString(3, sdt);
            ps.executeUpdate();
        }
    }

    private String generateNewCustomerId() throws Exception {
        String sql = "SELECT TOP 1 maKhachHang FROM KhachHang ORDER BY maKhachHang DESC";
        String lastId = null;
        try (Connection con = Db.getConnection(ctx);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                lastId = rs.getString("maKhachHang");
            }
        }

        if (lastId == null || lastId.length() < 3) {
            return "KH0001";
        }
        try {
            int number = Integer.parseInt(lastId.substring(2));
            number++;
            return String.format("KH%04d", number);
        } catch (NumberFormatException ex) {
            return "KH0001";
        }
    }

    private Customer map(ResultSet rs) throws Exception {
        Customer c = new Customer();
        c.setMaKhachHang(rs.getString("maKhachHang"));
        c.setTenKhachHang(rs.getString("tenKhachHang"));
        c.setSdt(rs.getString("sdt"));
        c.setEmail(rs.getString("email"));
        c.setSoGioChoi(rs.getDouble("soGioChoi"));
        c.setSoDu(rs.getDouble("soDu"));
        c.setMaLoaiKhachHang(rs.getString("maLoaiKhachHang"));
        c.setTenLoaiKhachHang(rs.getString("tenLoaiKhachHang"));
        return c;
    }
}

