package com.netgamer.qlquannet.servlet.client;

import com.netgamer.qlquannet.db.Db;
import com.netgamer.qlquannet.model.Customer;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ClientPaidInvoicesExportServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Customer customer = session != null ? (Customer) session.getAttribute("customer") : null;
        if (customer == null) {
            resp.sendRedirect(req.getContextPath() + "/login?from=/history");
            return;
        }

        LocalDate from = parseDate(req.getParameter("from"));
        LocalDate to = parseDate(req.getParameter("to"));

        String file = "don_da_thanh_toan_" + customer.getMaKhachHang() + ".csv";
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(file, StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        sb.append('\uFEFF');
        sb.append("MaHoaDon,Ngay,TrangThai,TienMay,TongTienDoAn,TongTien\n");

        StringBuilder sql = new StringBuilder(
                "SELECT maHoaDon, ngay, trangThai, COALESCE(tienMay, 0) AS tienMay, tongTienDoAn, tongTien " +
                "FROM HoaDon WHERE maKhachHang = ? AND trangThai = N'Đã thanh toán'"
        );
        if (from != null) sql.append(" AND ngay >= ?");
        if (to != null) sql.append(" AND ngay <= ?");
        sql.append(" ORDER BY ngay DESC, maHoaDon DESC");

        try (Connection con = Db.getConnection(getServletContext());
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, customer.getMaKhachHang());
            if (from != null) ps.setDate(idx++, java.sql.Date.valueOf(from));
            if (to != null) ps.setDate(idx++, java.sql.Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sb.append(csv(rs.getString("maHoaDon"))).append(',')
                      .append(csv(rs.getDate("ngay") != null ? rs.getDate("ngay").toString() : "")).append(',')
                      .append(csv(rs.getString("trangThai"))).append(',')
                      .append(rs.getDouble("tienMay")).append(',')
                      .append(rs.getDouble("tongTienDoAn")).append(',')
                      .append(rs.getDouble("tongTien"))
                      .append('\n');
                }
            }
        } catch (Exception e) {
            // vẫn trả về CSV nhưng có dòng lỗi để người dùng biết
            sb.append("\"ERROR\",\"").append(csv(e.getMessage())).append("\"\n");
        }

        resp.getWriter().write(sb.toString());
    }

    private LocalDate parseDate(String s) {
        try {
            if (s == null || s.trim().isEmpty()) return null;
            return LocalDate.parse(s.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private String csv(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\n") || v.contains("\r")) return "\"" + v + "\"";
        return v;
    }
}

