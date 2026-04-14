package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.CustomerDao;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StaffCustomersServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        try {
            req.setAttribute("customers", new CustomerDao(getServletContext()).findAll());
        } catch (Exception e) {
            req.setAttribute("customers", java.util.Collections.emptyList());
            req.setAttribute("error", "Lỗi DB: " + e.getMessage());
        }
        forward(req, resp, "/WEB-INF/jsp/staff/customers.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        String action = req.getParameter("action");
        try {
            CustomerDao dao = new CustomerDao(getServletContext());
            if ("approve".equals(action)) {
                String sdt = req.getParameter("sdt");
                if (sdt == null || sdt.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Thiếu SĐT cần xác nhận.");
                } else if (dao.approveCustomerBySdt(sdt.trim())) {
                    session.setAttribute("successMessage", "Đã xác nhận tài khoản. Username là SĐT, mật khẩu mặc định: tên khách (không dấu, không khoảng trắng, chữ thường).");
                } else {
                    session.setAttribute("errorMessage", "Không thể xác nhận tài khoản (có thể đã được xác nhận trước đó).");
                }
            } else if ("create".equals(action)) {
                String ten = req.getParameter("customerName");
                String sdt = req.getParameter("sdt");
                String email = req.getParameter("email");
                if (ten == null || ten.trim().isEmpty() || sdt == null || sdt.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Thiếu tên hoặc số điện thoại.");
                } else if (dao.existsBySdt(sdt.trim())) {
                    session.setAttribute("errorMessage", "SĐT đã tồn tại.");
                } else {
                    dao.createByAdmin(ten.trim(), sdt.trim(), email != null ? email.trim() : "");
                    session.setAttribute("successMessage", "Tạo tài khoản thành công. Username là SĐT, mật khẩu mặc định: tên khách (không dấu, không khoảng trắng, chữ thường).");
                }
            }
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Lỗi DB: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/staff/customers");
    }
}

