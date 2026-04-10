package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.ComputerDao;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StaffComputersServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        try {
            req.setAttribute("computers", new ComputerDao(getServletContext()).findAll());
        } catch (Exception e) {
            req.setAttribute("computers", java.util.Collections.emptyList());
            req.setAttribute("error", "Lỗi DB: " + e.getMessage());
        }
        forward(req, resp, "/WEB-INF/jsp/staff/computers.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }

        String pathInfo = req.getPathInfo(); // /status
        if (pathInfo == null || !"/status".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String maMay = req.getParameter("maMay");
        String trangThai = req.getParameter("trangThai");
        if (maMay == null || maMay.trim().isEmpty() || trangThai == null || trangThai.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Thiếu mã máy hoặc trạng thái.");
            resp.sendRedirect(req.getContextPath() + "/staff/computers");
            return;
        }
        try {
            new ComputerDao(getServletContext()).updateStatus(maMay.trim(), trangThai.trim());
            session.setAttribute("successMessage", "Cập nhật trạng thái thành công.");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Lỗi DB: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/staff/computers");
    }
}

