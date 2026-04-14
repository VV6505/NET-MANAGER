package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.CartDbItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class CartDao {
    private final ServletContext ctx;

    public CartDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public CartState getTempCart(String maKhachHang) throws Exception {
        String maHoaDon = findOpenInvoiceId(maKhachHang);
        if (maHoaDon == null) {
            return new CartState(null, new ArrayList<>(), 0, 0);
        }

        Totals totals = getTotals(maHoaDon);
        List<CartDbItem> items = new ArrayList<>();
        items.addAll(getFoodItems(maHoaDon));
        return new CartState(maHoaDon, items, totals.tongTienDoAn, totals.tongTien);
    }

    public String setItemQuantity(String maKhachHang, String maItem, int quantity) throws Exception {
        String sql = "EXEC sp_CapNhatSoLuongGio ?, ?, ?, ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ps.setString(2, maItem);
            ps.setString(3, "food");
            ps.setInt(4, quantity);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return "Error: No result";
                // SP trả về: Result, MaHoaDon (khi success)
                return rs.getString(1);
            }
        }
    }

    public String clearCart(String maKhachHang) throws Exception {
        String sql = "EXEC sp_XoaGioHang ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : "Error: No result";
            }
        }
    }

    public int countItems(String maKhachHang) throws Exception {
        CartState st = getTempCart(maKhachHang);
        int count = 0;
        for (CartDbItem it : st.items) {
            count += it.getQuantity();
        }
        return count;
    }

    private String findOpenInvoiceId(String maKhachHang) throws Exception {
        String sql =
                "SELECT TOP 1 maHoaDon " +
                "FROM HoaDon " +
                "WHERE maKhachHang = ? AND trangThai IN (N'Tạm', N'Đã đặt') " +
                "ORDER BY ngayDat DESC";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("maHoaDon") : null;
            }
        }
    }

    private Totals getTotals(String maHoaDon) throws Exception {
        String sql = "SELECT tongTienDoAn, tongTien FROM HoaDon WHERE maHoaDon = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return new Totals(0, 0);
                return new Totals(rs.getDouble("tongTienDoAn"), rs.getDouble("tongTien"));
            }
        }
    }

    private List<CartDbItem> getFoodItems(String maHoaDon) throws Exception {
        String sql =
                "SELECT da.maDoAn, da.tenDoAn, hdda.soLuong, hdda.donGia, hdda.thanhTien, da.hinhAnh " +
                "FROM HoaDonDoAn hdda " +
                "JOIN DoAn da ON hdda.maDoAn = da.maDoAn " +
                "WHERE hdda.maHDDoAn = ? " +
                "ORDER BY da.tenDoAn";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                List<CartDbItem> out = new ArrayList<>();
                while (rs.next()) {
                    CartDbItem it = new CartDbItem();
                    it.setType("food");
                    it.setId(rs.getString("maDoAn"));
                    it.setName(rs.getString("tenDoAn"));
                    it.setQuantity(rs.getInt("soLuong"));
                    it.setPrice(rs.getDouble("donGia"));
                    it.setLineTotal(rs.getDouble("thanhTien"));
                    it.setImageUrl(rs.getString("hinhAnh"));
                    out.add(it);
                }
                return out;
            }
        }
    }

    public static class CartState {
        public final String maHoaDon;
        public final List<CartDbItem> items;
        public final double tongTienDoAn;
        public final double tongTien;

        public CartState(String maHoaDon, List<CartDbItem> items, double tongTienDoAn, double tongTien) {
            this.maHoaDon = maHoaDon;
            this.items = items;
            this.tongTienDoAn = tongTienDoAn;
            this.tongTien = tongTien;
        }
    }

    private static class Totals {
        final double tongTienDoAn;
        final double tongTien;

        Totals(double tongTienDoAn, double tongTien) {
            this.tongTienDoAn = tongTienDoAn;
            this.tongTien = tongTien;
        }
    }
}

