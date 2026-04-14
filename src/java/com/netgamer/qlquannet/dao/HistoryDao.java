package com.netgamer.qlquannet.dao;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.InvoiceLineItem;
import com.netgamer.qlquannet.model.UsageHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class HistoryDao {
    private final ServletContext ctx;

    public HistoryDao(ServletContext ctx) {
        this.ctx = ctx;
    }

    public HistoryPage getUsageHistory(String maKhachHang, int pageNumber, int pageSize) throws Exception {
        // Stored procedure trả nhiều result sets:
        // (1) TotalRecords
        // (2) list usage + invoice
        // (3) food detail of latest
        String sql = "EXEC sp_GetLichSuSuDungKhachHang ?, ?, ?";
        try (Connection con = Db.getConnection(ctx);
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, maKhachHang);
            ps.setInt(2, pageNumber);
            ps.setInt(3, pageSize);

            boolean has = ps.execute();
            int total = 0;
            List<UsageHistory> items = new ArrayList<>();
            List<InvoiceLineItem> latestFoods = new ArrayList<>();
            String latestMaHoaDon = null;

            // RS1: total
            if (has) {
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs != null && rs.next()) {
                        total = rs.getInt("TotalRecords");
                    }
                }
            }

            // RS2: list
            if (ps.getMoreResults()) {
                try (ResultSet rs = ps.getResultSet()) {
                    while (rs != null && rs.next()) {
                        UsageHistory u = new UsageHistory();
                        u.setMaSuDung(rs.getString("maSuDung"));
                        u.setMaMay(rs.getString("maMay"));
                        u.setTenKhu(rs.getString("tenKhu"));
                        u.setTenLoaiMay(rs.getString("tenLoaiMay"));
                        u.setThoiGianVao(rs.getTimestamp("thoiGianVao"));
                        u.setThoiGianRa(rs.getTimestamp("thoiGianRa"));
                        u.setMaHoaDon(rs.getString("maHoaDon"));
                        u.setTongTienDoAn(rs.getDouble("tongTienDoAn"));
                        u.setTongTien(rs.getDouble("tongTien"));
                        u.setTrangThai(rs.getString("trangThai"));
                        items.add(u);
                    }
                }
            }

            // RS3: food detail of latest
            if (ps.getMoreResults()) {
                try (ResultSet rs = ps.getResultSet()) {
                    while (rs != null && rs.next()) {
                        if (latestMaHoaDon == null) {
                            latestMaHoaDon = rs.getString("maHoaDon");
                        }
                        InvoiceLineItem it = new InvoiceLineItem();
                        it.setMaHoaDon(rs.getString("maHoaDon"));
                        it.setTen(rs.getString("tenDoAn"));
                        it.setSoLuong(rs.getInt("soLuong"));
                        it.setDonGia(rs.getDouble("donGia"));
                        it.setThanhTien(rs.getDouble("thanhTien"));
                        latestFoods.add(it);
                    }
                }
            }

            return new HistoryPage(total, items, pageNumber, pageSize, latestMaHoaDon, latestFoods);
        }
    }

    public static class HistoryPage {
        public final int totalRecords;
        public final List<UsageHistory> items;
        public final int pageNumber;
        public final int pageSize;
        public final String latestMaHoaDon;
        public final List<InvoiceLineItem> latestFoods;

        public HistoryPage(
                int totalRecords,
                List<UsageHistory> items,
                int pageNumber,
                int pageSize,
                String latestMaHoaDon,
                List<InvoiceLineItem> latestFoods
        ) {
            this.totalRecords = totalRecords;
            this.items = items;
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.latestMaHoaDon = latestMaHoaDon;
            this.latestFoods = latestFoods;
        }
    }
}

