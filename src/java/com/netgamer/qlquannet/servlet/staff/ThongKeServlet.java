package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.RevenueDao;
import com.netgamer.qlquannet.model.InvoiceSummary;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ThongKeServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        if (!canViewRevenue(session)) {
            session.setAttribute("errorMessage", "Bạn không có quyền xem doanh thu.");
            resp.sendRedirect(req.getContextPath() + "/staff");
            return;
        }

        LocalDate from = parseDate(req.getParameter("from"));
        LocalDate to = parseDate(req.getParameter("to"));
        String month = req.getParameter("month");
        if (month != null && month.matches("\\d{4}-\\d{2}")) {
            try {
                java.time.YearMonth ym = java.time.YearMonth.parse(month);
                from = ym.atDay(1);
                to = ym.atEndOfMonth();
            } catch (Exception ignored) {
            }
        }
        String customerName = req.getParameter("customerName");

        try {
            RevenueDao dao = new RevenueDao(getServletContext());
            RevenueDao.RevenueResult rr = dao.getPaidInvoices(from, to, customerName);

            // Gom doanh thu theo ngày (tổng từng hóa đơn đã gồm tiền máy + đồ ăn)
            Map<LocalDate, Double> byDay = new LinkedHashMap<>();
            for (InvoiceSummary inv : rr.invoices) {
                if (inv.getNgay() == null) {
                    continue;
                }
                byDay.merge(inv.getNgay(), inv.getTongCong(), Double::sum);
            }
            List<LocalDate> sortedDays = new ArrayList<>(byDay.keySet());
            sortedDays.sort(LocalDate::compareTo);
            List<String> chartLabels = new ArrayList<>();
            List<Double> chartData = new ArrayList<>();
            for (LocalDate d : sortedDays) {
                chartLabels.add(d.toString());
                chartData.add(byDay.get(d));
            }

            req.setAttribute("paidCustomerNames", dao.listPaidCustomerNames());
            req.setAttribute("from", req.getParameter("from"));
            req.setAttribute("to", req.getParameter("to"));
            req.setAttribute("month", month != null ? month : "");
            req.setAttribute("customerName", customerName);

            // Tên biến theo đề bài
            req.setAttribute("listHoaDon", rr.invoices);
            req.setAttribute("tongDoanhThu", rr.total);
            req.setAttribute("tongTienDoAn", rr.totalFood);
            req.setAttribute("tongTienGioChoi", rr.totalPlayMoney);

            // Tương thích mã JSP cũ
            req.setAttribute("invoices", rr.invoices);
            req.setAttribute("total", rr.total);
            req.setAttribute("totalFood", rr.totalFood);
            req.setAttribute("totalPlayMoney", rr.totalPlayMoney);

            req.setAttribute("chartLabels", chartLabels);
            req.setAttribute("chartData", chartData);

            forward(req, resp, "/WEB-INF/jsp/staff/revenue.jsp");
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi DB: " + e.getMessage());
            req.setAttribute("paidCustomerNames", java.util.Collections.emptyList());
            req.setAttribute("listHoaDon", java.util.Collections.emptyList());
            req.setAttribute("invoices", java.util.Collections.emptyList());
            req.setAttribute("chartLabels", java.util.Collections.emptyList());
            req.setAttribute("chartData", java.util.Collections.emptyList());
            req.setAttribute("tongDoanhThu", 0d);
            req.setAttribute("tongTienDoAn", 0d);
            req.setAttribute("tongTienGioChoi", 0d);
            forward(req, resp, "/WEB-INF/jsp/staff/revenue.jsp");
        }
    }

    private LocalDate parseDate(String s) {
        try {
            if (s == null || s.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(s.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean canViewRevenue(HttpSession session) {
        String role = (String) session.getAttribute("staffRole");
        if (role == null) return false;
        String r = role.toLowerCase();
        return r.contains("quản lý") || r.contains("thu ngân");
    }
}
