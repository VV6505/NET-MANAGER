package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.InvoiceDao;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StaffInvoicesServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        String customerName = req.getParameter("customerName");
        String fromStr = req.getParameter("from");
        String toStr = req.getParameter("to");

        req.setAttribute("customerName", customerName != null ? customerName : "");
        req.setAttribute("from", fromStr != null ? fromStr : "");
        req.setAttribute("to", toStr != null ? toStr : "");

        LocalDate from = parseDate(fromStr);
        LocalDate to = parseDate(toStr);
        try {
            req.setAttribute("invoices", new InvoiceDao(getServletContext()).search(from, to, customerName));
        } catch (Exception e) {
            req.setAttribute("invoices", java.util.Collections.emptyList());
            req.setAttribute("error", "Lỗi DB: " + e.getMessage());
        }
        forward(req, resp, "/WEB-INF/jsp/staff/invoices.jsp");
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception ignored) {
            return null;
        }
    }
}

