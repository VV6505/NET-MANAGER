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
        String maHoaDon = findTodayTempInvoiceId(maKhachHang);
        if (maHoaDon == null) {
            return new CartState(null, new ArrayList<>(), 0, 0, 0);
        }

        Totals totals = getTotals(maHoaDon);
        List<CartDbItem> items = new ArrayList<>();
        items.addAll(getFoodItems(maHoaDon));
        items.addAll(getGameItems(maHoaDon));
        return new CartState(maHoaDon, items, totals.tongTienDoAn, totals.tongTienGame, totals.tongTien);
    }

    public String setItemQuantity(String maKhachHang, String maItem, String type, int quantity) throws Exception {
        String sql = "EXEC sp_CapNhatSoLuongGio ?, ?, ?, ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ps.setString(2, maItem);
            ps.setString(3, type);
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

    public String checkoutFromWeb(String maKhachHang, String maHoaDon) throws Exception {
        // Prefer stored procedure if present; fallback to SQL if DB thiếu proc
        try {
            String sql = "EXEC sp_DatMonTuWeb ?, ?";
            try (Connection con = Db.getConnection(ctx);
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maKhachHang);
                ps.setString(2, maHoaDon);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return "Error: No result";
                    // SP trả: MaSuDung, Result, ThoiGianDat, MaHoaDon
                    String result = rs.getString("Result");
                    if (result == null) {
                        result = rs.getString(2);
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.toLowerCase().contains("could not find stored procedure")
                    || msg.toLowerCase().contains("sp_datmontuweb")) {
                return checkoutFromWebFallback(maKhachHang, maHoaDon);
            }
            throw e;
        }
    }

    private String checkoutFromWebFallback(String maKhachHang, String maHoaDon) throws Exception {
        // Mimic logic of sp_DatMonTuWeb
        try (Connection con = Db.getConnection(ctx)) {
            con.setAutoCommit(false);
            try {
                String maSuDung;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT 'LS' + RIGHT('0000' + CAST(ISNULL(MAX(CAST(SUBSTRING(maSuDung, 3, 4) AS INT)), 0) + 1 AS VARCHAR(4)), 4) AS maSuDung FROM LichSuSuDung")) {
                    try (ResultSet rs = ps.executeQuery()) {
                        maSuDung = rs.next() ? rs.getString(1) : null;
                    }
                }
                if (maSuDung == null) throw new Exception("Không tạo được mã sử dụng");

                java.sql.Timestamp thoiGianDat;
                try (PreparedStatement ps = con.prepareStatement("SELECT ngayDat FROM HoaDon WHERE maHoaDon = ?")) {
                    ps.setString(1, maHoaDon);
                    try (ResultSet rs = ps.executeQuery()) {
                        thoiGianDat = rs.next() ? rs.getTimestamp(1) : null;
                    }
                }
                if (thoiGianDat == null) thoiGianDat = new java.sql.Timestamp(System.currentTimeMillis());

                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO LichSuSuDung (maSuDung, maKhachHang, thoiGianVao) VALUES (?,?,?)")) {
                    ps.setString(1, maSuDung);
                    ps.setString(2, maKhachHang);
                    ps.setTimestamp(3, thoiGianDat);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE HoaDon SET maSuDung = ?, trangThai = N'Đã đặt' WHERE maHoaDon = ? AND maKhachHang = ?")) {
                    ps.setString(1, maSuDung);
                    ps.setString(2, maHoaDon);
                    ps.setString(3, maKhachHang);
                    int updated = ps.executeUpdate();
                    if (updated <= 0) throw new Exception("Không cập nhật được hóa đơn");
                }

                con.commit();
                return "Success";
            } catch (Exception ex) {
                try { con.rollback(); } catch (Exception ignored) {}
                return "Error: " + ex.getMessage();
            } finally {
                try { con.setAutoCommit(true); } catch (Exception ignored) {}
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

    private String findTodayTempInvoiceId(String maKhachHang) throws Exception {
        String sql =
                "SELECT TOP 1 maHoaDon " +
                "FROM HoaDon " +
                "WHERE maKhachHang = ? AND CAST(ngayDat AS DATE) = CAST(GETDATE() AS DATE) AND trangThai = N'Tạm' " +
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
        String sql = "SELECT tongTienDoAn, tongTienGame, tongTien FROM HoaDon WHERE maHoaDon = ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return new Totals(0, 0, 0);
                return new Totals(rs.getDouble("tongTienDoAn"), rs.getDouble("tongTienGame"), rs.getDouble("tongTien"));
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

    private List<CartDbItem> getGameItems(String maHoaDon) throws Exception {
        String sql =
                "SELECT g.maGame, g.tenGame, hdg.soLuong, hdg.donGia, hdg.thanhTien, g.hinhAnh " +
                "FROM HoaDonGame hdg " +
                "JOIN Game g ON hdg.maGame = g.maGame " +
                "WHERE hdg.maHDGame = ? " +
                "ORDER BY g.tenGame";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                List<CartDbItem> out = new ArrayList<>();
                while (rs.next()) {
                    CartDbItem it = new CartDbItem();
                    it.setType("game");
                    it.setId(rs.getString("maGame"));
                    it.setName(rs.getString("tenGame"));
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
        public final double tongTienGame;
        public final double tongTien;

        public CartState(String maHoaDon, List<CartDbItem> items, double tongTienDoAn, double tongTienGame, double tongTien) {
            this.maHoaDon = maHoaDon;
            this.items = items;
            this.tongTienDoAn = tongTienDoAn;
            this.tongTienGame = tongTienGame;
            this.tongTien = tongTien;
        }
    }

    private static class Totals {
        final double tongTienDoAn;
        final double tongTienGame;
        final double tongTien;

        Totals(double tongTienDoAn, double tongTienGame, double tongTien) {
            this.tongTienDoAn = tongTienDoAn;
            this.tongTienGame = tongTienGame;
            this.tongTien = tongTien;
        }
    }
}

