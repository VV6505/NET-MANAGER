package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.StaffDao;
import com.netgamer.qlquannet.model.Staff;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StaffAuthServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath(); // /staff/login or /staff/logout
        if ("/staff/login".equals(path)) {
            // Dùng chung UI đăng nhập với khách hàng (tab Admin)
            resp.sendRedirect(req.getContextPath() + "/login?tab=admin");
            return;
        }
        if ("/staff/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute("staffName");
                session.removeAttribute("staffUsername");
            }
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (!"/staff/login".equals(path)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String maNhanVien = req.getParameter("maNhanVien");
        String password = req.getParameter("password");
        if (maNhanVien == null || maNhanVien.trim().isEmpty() || password == null) {
            req.setAttribute("adminError", "Vui lòng nhập mã nhân viên và mật khẩu.");
            req.setAttribute("activeTab", "admin");
            forward(req, resp, "/WEB-INF/jsp/client/login.jsp");
            return;
        }

        try {
            StaffDao dao = new StaffDao(getServletContext());
            Staff staff = dao.login(maNhanVien.trim(), password);
            if (staff == null) {
                req.setAttribute("adminError", "Mã nhân viên hoặc mật khẩu không đúng.");
                req.setAttribute("activeTab", "admin");
                forward(req, resp, "/WEB-INF/jsp/client/login.jsp");
                return;
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("staff", staff);
            session.setAttribute("staffUsername", staff.getMaNhanVien());
            session.setAttribute("staffName", staff.getTenNhanVien());
            session.setAttribute("staffRole", staff.getLoaiNhanVien());
            resp.sendRedirect(req.getContextPath() + "/staff");
            return;
        } catch (Exception e) {
            req.setAttribute("adminError", "Lỗi DB: " + e.getMessage());
            req.setAttribute("activeTab", "admin");
            forward(req, resp, "/WEB-INF/jsp/client/login.jsp");
            return;
        }
    }
}

