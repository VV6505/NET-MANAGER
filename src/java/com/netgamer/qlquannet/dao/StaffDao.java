package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.Employee;
import com.netgamer.qlquannet.model.Staff;
import com.netgamer.qlquannet.model.StaffType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class StaffDao {
    private final ServletContext ctx;

    public StaffDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public Staff login(String maNhanVien, String matKhau) throws Exception {
        if (!checkLogin(maNhanVien, matKhau)) {
            return null;
        }
        Staff dto = new Staff();
        dto.setMaNhanVien(maNhanVien);
        dto.setTenNhanVien(getTenNhanVien(maNhanVien));
        dto.setLoaiNhanVien(getLoaiNhanVien(maNhanVien));
        return dto;
    }

    private boolean checkLogin(String maNhanVien, String matKhau) throws Exception {
        String sql = "SELECT COUNT(*) AS c FROM NhanVien WHERE maNhanVien = ? AND matKhau = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            ps.setString(2, matKhau);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                return rs.getInt("c") > 0;
            }
        }
    }

    private String getTenNhanVien(String maNhanVien) throws Exception {
        String sql = "SELECT tenNhanVien FROM NhanVien WHERE maNhanVien = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("tenNhanVien") : "";
            }
        }
    }

    private String getLoaiNhanVien(String maNhanVien) throws Exception {
        String sql =
                "SELECT lnv.tenLoaiNhanVien " +
                "FROM NhanVien nv " +
                "JOIN LoaiNhanVien lnv ON nv.maLoaiNhanVien = lnv.maLoaiNhanVien " +
                "WHERE nv.maNhanVien = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : "";
            }
        }
    }

    public List<StaffType> getStaffTypes() throws Exception {
        String sql = "SELECT maLoaiNhanVien, tenLoaiNhanVien FROM LoaiNhanVien ORDER BY tenLoaiNhanVien";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<StaffType> out = new ArrayList<>();
            while (rs.next()) {
                StaffType t = new StaffType();
                t.setMaLoaiNhanVien(rs.getString(1));
                t.setTenLoaiNhanVien(rs.getString(2));
                out.add(t);
            }
            return out;
        }
    }

    public List<Employee> listEmployees() throws Exception {
        String sql =
                "SELECT nv.maNhanVien, nv.tenNhanVien, nv.sdt, nv.email, nv.phongBan, nv.ngayBatDau, nv.trangThai, " +
                "       nv.maLoaiNhanVien, lnv.tenLoaiNhanVien " +
                "FROM NhanVien nv " +
                "JOIN LoaiNhanVien lnv ON nv.maLoaiNhanVien = lnv.maLoaiNhanVien " +
                "ORDER BY nv.maNhanVien DESC";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Employee> out = new ArrayList<>();
            while (rs.next()) {
                Employee e = new Employee();
                e.setMaNhanVien(rs.getString("maNhanVien"));
                e.setTenNhanVien(rs.getString("tenNhanVien"));
                e.setSdt(rs.getString("sdt"));
                e.setEmail(rs.getString("email"));
                e.setPhongBan(rs.getString("phongBan"));
                java.sql.Date d = rs.getDate("ngayBatDau");
                e.setNgayBatDau(d != null ? d.toLocalDate() : null);
                e.setTrangThai(rs.getString("trangThai"));
                e.setMaLoaiNhanVien(rs.getString("maLoaiNhanVien"));
                e.setTenLoaiNhanVien(rs.getString("tenLoaiNhanVien"));
                out.add(e);
            }
            return out;
        }
    }

    public Employee findEmployeeById(String maNhanVien) throws Exception {
        String sql =
                "SELECT nv.maNhanVien, nv.tenNhanVien, nv.ngaySinh, nv.gioiTinh, nv.cmnd, nv.ngayCap, nv.noiCap, " +
                "       nv.sdt, nv.email, nv.diaChi, nv.phongBan, nv.ngayBatDau, nv.luongCoBan, nv.trangThai, " +
                "       nv.maLoaiNhanVien, lnv.tenLoaiNhanVien " +
                "FROM NhanVien nv " +
                "LEFT JOIN LoaiNhanVien lnv ON nv.maLoaiNhanVien = lnv.maLoaiNhanVien " +
                "WHERE nv.maNhanVien = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Employee e = new Employee();
                e.setMaNhanVien(rs.getString("maNhanVien"));
                e.setTenNhanVien(rs.getString("tenNhanVien"));
                java.sql.Date ns = rs.getDate("ngaySinh");
                e.setNgaySinh(ns != null ? ns.toLocalDate() : null);
                e.setGioiTinh(rs.getString("gioiTinh"));
                e.setCmnd(rs.getString("cmnd"));
                java.sql.Date nc = rs.getDate("ngayCap");
                e.setNgayCap(nc != null ? nc.toLocalDate() : null);
                e.setNoiCap(rs.getString("noiCap"));
                e.setSdt(rs.getString("sdt"));
                e.setEmail(rs.getString("email"));
                e.setDiaChi(rs.getString("diaChi"));
                e.setPhongBan(rs.getString("phongBan"));
                java.sql.Date nbd = rs.getDate("ngayBatDau");
                e.setNgayBatDau(nbd != null ? nbd.toLocalDate() : null);
                e.setLuongCoBan(rs.getString("luongCoBan"));
                e.setTrangThai(rs.getString("trangThai"));
                e.setMaLoaiNhanVien(rs.getString("maLoaiNhanVien"));
                e.setTenLoaiNhanVien(rs.getString("tenLoaiNhanVien"));
                return e;
            }
        }
    }

    public String nextEmployeeId() throws Exception {
        String sql = "SELECT ISNULL(MAX(CAST(SUBSTRING(maNhanVien, 3, 4) AS INT)), 0) + 1 AS n FROM NhanVien";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int n = rs.next() ? rs.getInt(1) : 1;
            return String.format("NV%04d", n);
        }
    }

    public Employee createEmployee(Employee in) throws Exception {
        if (in == null) return null;
        String ma = (in.getMaNhanVien() == null || in.getMaNhanVien().trim().isEmpty())
                ? nextEmployeeId()
                : in.getMaNhanVien().trim();

        String sql =
                "INSERT INTO NhanVien (maNhanVien, tenNhanVien, ngaySinh, gioiTinh, cmnd, ngayCap, noiCap, sdt, email, diaChi, phongBan, matKhau, ngayBatDau, luongCoBan, maLoaiNhanVien, trangThai) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ma);
            ps.setString(2, in.getTenNhanVien());
            if (in.getNgaySinh() != null) ps.setDate(3, java.sql.Date.valueOf(in.getNgaySinh())); else ps.setNull(3, java.sql.Types.DATE);
            ps.setString(4, in.getGioiTinh());
            ps.setString(5, in.getCmnd());
            if (in.getNgayCap() != null) ps.setDate(6, java.sql.Date.valueOf(in.getNgayCap())); else ps.setNull(6, java.sql.Types.DATE);
            ps.setString(7, in.getNoiCap());
            ps.setString(8, in.getSdt());
            ps.setString(9, in.getEmail());
            ps.setString(10, in.getDiaChi());
            ps.setString(11, in.getPhongBan());
            ps.setString(12, in.getMatKhau());
            LocalDate nbd = in.getNgayBatDau() != null ? in.getNgayBatDau() : LocalDate.now();
            ps.setDate(13, java.sql.Date.valueOf(nbd));
            ps.setString(14, in.getLuongCoBan());
            ps.setString(15, in.getMaLoaiNhanVien());
            ps.setString(16, (in.getTrangThai() == null || in.getTrangThai().trim().isEmpty()) ? "Đang làm việc" : in.getTrangThai());
            ps.executeUpdate();

            Employee out = new Employee();
            out.setMaNhanVien(ma);
            out.setTenNhanVien(in.getTenNhanVien());
            out.setSdt(in.getSdt());
            out.setEmail(in.getEmail());
            out.setPhongBan(in.getPhongBan());
            out.setNgayBatDau(nbd);
            out.setTrangThai((in.getTrangThai() == null || in.getTrangThai().trim().isEmpty()) ? "Đang làm việc" : in.getTrangThai());
            out.setMaLoaiNhanVien(in.getMaLoaiNhanVien());
            return out;
        }
    }
}

