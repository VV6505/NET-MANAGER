package com.netgamer.qlquannet.servlet.client;

import com.netgamer.qlquannet.dao.CustomerDao;
import com.netgamer.qlquannet.model.Customer;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ProfileServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Customer sessionCustomer = session != null ? (Customer) session.getAttribute("customer") : null;
        if (sessionCustomer == null) {
            resp.sendRedirect(req.getContextPath() + "/login?from=/profile");
            return;
        }

        try {
            CustomerDao dao = new CustomerDao(getServletContext());
            Customer fresh = dao.findBySdt(sessionCustomer.getSdt());
            if (fresh != null) {
                session.setAttribute("customer", fresh);
                session.setAttribute("tenKhachHang", fresh.getTenKhachHang());
                req.setAttribute("customer", fresh);
            } else {
                req.setAttribute("customer", sessionCustomer);
            }
        } catch (Exception e) {
            req.setAttribute("customer", sessionCustomer);
            req.setAttribute("error", "Lỗi DB: " + e.getMessage());
        }

        String pathInfo = req.getPathInfo(); // null, /edit
        if (pathInfo != null && "/edit".equals(pathInfo)) {
            forward(req, resp, "/WEB-INF/jsp/client/profile-edit.jsp");
            return;
        }
        forward(req, resp, "/WEB-INF/jsp/client/profile.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Customer sessionCustomer = session != null ? (Customer) session.getAttribute("customer") : null;
        if (sessionCustomer == null) {
            resp.sendRedirect(req.getContextPath() + "/login?from=/profile/edit");
            return;
        }

        String ten = req.getParameter("tenKhachHang");
        String email = req.getParameter("email");
        String matKhauMoi = req.getParameter("matKhau");
        String xacNhan = req.getParameter("xacNhanMatKhau");

        if (ten == null || ten.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập tên và email.");
            req.setAttribute("customer", sessionCustomer);
            forward(req, resp, "/WEB-INF/jsp/client/profile-edit.jsp");
            return;
        }
        if (matKhauMoi != null && !matKhauMoi.trim().isEmpty()) {
            if (xacNhan == null || !matKhauMoi.equals(xacNhan)) {
                req.setAttribute("error", "Xác nhận mật khẩu không khớp.");
                req.setAttribute("customer", sessionCustomer);
                forward(req, resp, "/WEB-INF/jsp/client/profile-edit.jsp");
                return;
            }
        }

        try {
            CustomerDao dao = new CustomerDao(getServletContext());
            dao.updateBySdt(sessionCustomer.getSdt(), ten.trim(), email.trim(), matKhauMoi);
            Customer fresh = dao.findBySdt(sessionCustomer.getSdt());
            if (fresh != null) {
                session.setAttribute("customer", fresh);
                session.setAttribute("username", fresh.getSdt());
                session.setAttribute("tenKhachHang", fresh.getTenKhachHang());
            }
            session.setAttribute("message", "Cập nhật thông tin thành công.");
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi DB: " + e.getMessage());
            req.setAttribute("customer", sessionCustomer);
            forward(req, resp, "/WEB-INF/jsp/client/profile-edit.jsp");
            return;
        }
    }
}

