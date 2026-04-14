package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RevenueExcelPlaceholderServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }

        session.setAttribute("errorMessage", "Chức năng xuất Excel đang phát triển.");
        resp.sendRedirect(req.getContextPath() + "/staff/revenue" + buildQuery(req));
    }

    private String buildQuery(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        appendQuery(sb, "month", req.getParameter("month"));
        appendQuery(sb, "from", req.getParameter("from"));
        appendQuery(sb, "to", req.getParameter("to"));
        appendQuery(sb, "customerName", req.getParameter("customerName"));
        return sb.toString();
    }

    private void appendQuery(StringBuilder sb, String key, String value) throws IOException {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        sb.append(sb.length() == 0 ? "?" : "&")
                .append(URLEncoder.encode(key, StandardCharsets.UTF_8.name()))
                .append("=")
                .append(URLEncoder.encode(value.trim(), StandardCharsets.UTF_8.name()));
    }
}
