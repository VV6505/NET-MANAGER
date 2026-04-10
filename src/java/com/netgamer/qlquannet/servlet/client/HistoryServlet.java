package com.netgamer.qlquannet.servlet.client;

import com.netgamer.qlquannet.dao.HistoryDao;
import com.netgamer.qlquannet.model.Customer;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HistoryServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Customer customer = session != null ? (Customer) session.getAttribute("customer") : null;
        if (customer == null) {
            resp.sendRedirect(req.getContextPath() + "/login?from=/history");
            return;
        }

        int page = parseInt(req.getParameter("page"), 1);
        int size = parseInt(req.getParameter("size"), 10);
        page = Math.max(1, page);
        size = Math.max(5, Math.min(50, size));

        try {
            HistoryDao.HistoryPage hp = new HistoryDao(getServletContext())
                    .getUsageHistory(customer.getMaKhachHang(), page, size);
            req.setAttribute("history", hp.items);
            req.setAttribute("totalRecords", hp.totalRecords);
            req.setAttribute("page", hp.pageNumber);
            req.setAttribute("size", hp.pageSize);
            req.setAttribute("latestMaHoaDon", hp.latestMaHoaDon);
            req.setAttribute("latestFoods", hp.latestFoods);
            req.setAttribute("latestGames", hp.latestGames);
        } catch (Exception e) {
            req.setAttribute("history", java.util.Collections.emptyList());
            req.setAttribute("error", "Lỗi DB: " + e.getMessage());
        }

        forward(req, resp, "/WEB-INF/jsp/client/history.jsp");
    }

    private int parseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignored) {
            return fallback;
        }
    }
}

