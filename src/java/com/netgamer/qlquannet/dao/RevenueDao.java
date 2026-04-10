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

public class RevenueDao {
    private final ServletContext ctx;

    /**
     * Hóa đơn tính vào doanh thu: không lấy bản nháp {@code Tạm}.
     * Khách đặt qua web thường là {@code Đã đặt}; thanh toán tại quầy là {@code Đã thanh toán}.
     */
    static final String SQL_DOANH_THU_TRANG_THAI =
            "h.trangThai IN (N'Đã thanh toán', N'Đã đặt')";

    /**
     * Ngày dùng cho báo cáo/lọc: ưu tiên {@code ngay}, fallback {@code ngayDat} (nhiều HD khách chỉ có ngayDat).
     */
    static final String SQL_NGAY_HIEU_LUC = "COALESCE(h.ngay, CAST(h.ngayDat AS DATE))";

    public RevenueDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public RevenueResult getPaidInvoices(LocalDate from, LocalDate to, String customerName) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT h.maHoaDon, " + SQL_NGAY_HIEU_LUC + " AS ngayHieuLuc, h.trangThai, h.maKhachHang, kh.tenKhachHang, h.soGioChoi, "
                        + "h.tongTien, h.tongTienDoAn, h.tongTienGame, COALESCE(mt.giaGio, 0) AS giaGioMay "
                        + "FROM HoaDon h "
                        + "LEFT JOIN KhachHang kh ON h.maKhachHang = kh.maKhachHang "
                        + "LEFT JOIN LichSuSuDung ls ON h.maSuDung = ls.maSuDung "
                        + "LEFT JOIN MayTinh mt ON ls.maMay = mt.maMay "
                        + "WHERE " + SQL_DOANH_THU_TRANG_THAI
        );
        List<Object> params = new ArrayList<>();
        if (from != null) {
            sql.append(" AND ").append(SQL_NGAY_HIEU_LUC).append(" >= ?");
            params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND ").append(SQL_NGAY_HIEU_LUC).append(" <= ?");
            params.add(java.sql.Date.valueOf(to));
        }
        if (customerName != null && !customerName.trim().isEmpty()) {
            sql.append(" AND kh.tenKhachHang LIKE ?");
            params.add("%" + customerName.trim() + "%");
        }
        sql.append(" ORDER BY ").append(SQL_NGAY_HIEU_LUC).append(" DESC, h.maHoaDon DESC");

        double sumTongTien = 0;
        double sumFood = 0;
        double sumGame = 0;
        double sumPlay = 0;
        double sumGrand = 0;
        List<InvoiceSummary> out = new ArrayList<>();
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceSummary dto = new InvoiceSummary();
                    dto.setMaHoaDon(rs.getString("maHoaDon"));
                    java.sql.Date ngay = rs.getDate("ngayHieuLuc");
                    dto.setNgay(ngay != null ? ngay.toLocalDate() : null);
                    dto.setTrangThai(rs.getString("trangThai"));
                    dto.setMaKhachHang(rs.getString("maKhachHang"));
                    dto.setTenKhachHang(rs.getString("tenKhachHang"));
                    int soGio = rs.getInt("soGioChoi");
                    dto.setSoGioChoi(soGio);
                    double tong = rs.getDouble("tongTien");
                    double food = rs.getDouble("tongTienDoAn");
                    double game = rs.getDouble("tongTienGame");
                    double giaGio = rs.getDouble("giaGioMay");
                    double tienGio = soGio * giaGio;
                    dto.setTongTien(tong);
                    dto.setTongTienDoAn(food);
                    dto.setTongTienGame(game);
                    dto.setTienGioChoi(tienGio);
                    out.add(dto);

                    sumTongTien += tong;
                    sumFood += food;
                    sumGame += game;
                    sumPlay += tienGio;
                    sumGrand += tong + tienGio;
                }
            }
        }
        return new RevenueResult(out, sumGrand, sumFood, sumGame, sumPlay, sumTongTien);
    }

    public List<RevenueBucket> groupPaidRevenue(String groupBy, LocalDate from, LocalDate to) throws Exception {
        String g = (groupBy == null) ? "day" : groupBy.trim().toLowerCase();
        String periodExpr;
        String orderExpr;
        String ngayExpr = SQL_NGAY_HIEU_LUC;
        if ("month".equals(g)) {
            periodExpr = "CONVERT(varchar(7), " + ngayExpr + ", 120)"; // yyyy-MM
            orderExpr = "period DESC";
        } else {
            periodExpr = "CONVERT(varchar(10), " + ngayExpr + ", 120)"; // yyyy-MM-dd
            orderExpr = "period DESC";
        }

        StringBuilder sql = new StringBuilder(
                "SELECT " + periodExpr + " AS period, COUNT(*) AS orders, SUM(h.tongTien) AS revenue " +
                "FROM HoaDon h " +
                "WHERE " + SQL_DOANH_THU_TRANG_THAI
        );
        List<Object> params = new ArrayList<>();
        if (from != null) {
            sql.append(" AND ").append(ngayExpr).append(" >= ?");
            params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND ").append(ngayExpr).append(" <= ?");
            params.add(java.sql.Date.valueOf(to));
        }
        sql.append(" GROUP BY ").append(periodExpr);
        sql.append(" ORDER BY ").append(orderExpr);

        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                List<RevenueBucket> out = new ArrayList<>();
                while (rs.next()) {
                    RevenueBucket b = new RevenueBucket();
                    b.period = rs.getString("period");
                    b.orders = rs.getInt("orders");
                    b.revenue = rs.getDouble("revenue");
                    out.add(b);
                }
                return out;
            }
        }
    }

    public List<String> listPaidCustomerNames() throws Exception {
        String sql =
                "SELECT DISTINCT kh.tenKhachHang " +
                "FROM HoaDon h " +
                "JOIN KhachHang kh ON h.maKhachHang = kh.maKhachHang " +
                "WHERE " + SQL_DOANH_THU_TRANG_THAI + " " +
                "ORDER BY kh.tenKhachHang";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<String> out = new ArrayList<>();
            while (rs.next()) {
                out.add(rs.getString(1));
            }
            return out;
        }
    }

    public static class RevenueResult {
        public final List<InvoiceSummary> invoices;
        /** Tổng doanh thu (tổng tiền hóa đơn + tiền giờ chơi ước lượng). */
        public final double total;
        public final double totalFood;
        public final double totalGame;
        public final double totalPlayMoney;
        /** Chỉ tổng cột tongTien trên DB (đồ ăn + game). */
        public final double totalInvoiceMoney;

        public RevenueResult(List<InvoiceSummary> invoices, double total, double totalFood, double totalGame,
                double totalPlayMoney, double totalInvoiceMoney) {
            this.invoices = invoices;
            this.total = total;
            this.totalFood = totalFood;
            this.totalGame = totalGame;
            this.totalPlayMoney = totalPlayMoney;
            this.totalInvoiceMoney = totalInvoiceMoney;
        }
    }

    public static class RevenueBucket {
        public String period; // yyyy-MM-dd or yyyy-MM
        public int orders;
        public double revenue;
    }
}

