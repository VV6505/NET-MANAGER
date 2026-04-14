package com.netgamer.qlquannet.servlet.client;

import com.netgamer.qlquannet.dao.CustomerDao;
import com.netgamer.qlquannet.dao.CartDao;
import com.netgamer.qlquannet.model.Customer;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ClientAuthServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/login".equals(path)) {
            forward(req, resp, "/WEB-INF/jsp/client/login.jsp");
            return;
        }
        if ("/register".equals(path)) {
            forward(req, resp, "/WEB-INF/jsp/client/register.jsp");
            return;
        }
        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/login".equals(path)) {
            String sdt = req.getParameter("username");
            String password = req.getParameter("password");
            if (sdt == null || sdt.trim().isEmpty() || password == null) {
                req.setAttribute("error", "Vui lòng nhập số điện thoại và mật khẩu.");
                forward(req, resp, "/WEB-INF/jsp/client/login.jsp");
                return;
            }
            try {
                CustomerDao dao = new CustomerDao(getServletContext());
                Customer customer = dao.findBySdtAndPassword(sdt.trim(), password);
                if (customer == null) {
                    req.setAttribute("error", "SĐT hoặc mật khẩu không đúng, hoặc tài khoản chưa được admin xác nhận.");
                    forward(req, resp, "/WEB-INF/jsp/client/login.jsp");
                    return;
                }
                HttpSession session = req.getSession(true);
                session.setAttribute("customer", customer);
                session.setAttribute("username", customer.getSdt());
                session.setAttribute("tenKhachHang", customer.getTenKhachHang());
                try {
                    session.setAttribute("cartCount", new CartDao(getServletContext()).countItems(customer.getMaKhachHang()));
                } catch (Exception ignored) {
                }

                String from = req.getParameter("from");
                if (from != null && !from.trim().isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + from);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/home");
                }
                return;
            } catch (Exception e) {
                req.setAttribute("error", "Lỗi DB: " + e.getMessage());
                forward(req, resp, "/WEB-INF/jsp/client/login.jsp");
                return;
            }
        }

        if ("/register".equals(path)) {
            String tenKhachHang = req.getParameter("customerName");
            String sdt = req.getParameter("username");
            String email = req.getParameter("email");
            if (tenKhachHang == null || sdt == null || tenKhachHang.trim().isEmpty() || sdt.trim().isEmpty()) {
                req.setAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
                forward(req, resp, "/WEB-INF/jsp/client/register.jsp");
                return;
            }
            try {
                CustomerDao dao = new CustomerDao(getServletContext());
                String result = dao.submitRegistrationRequest(tenKhachHang.trim(), sdt.trim(), email != null ? email.trim() : "");
                if ("PENDING_EXISTS".equals(result)) {
                    req.setAttribute("error", "Yêu cầu đăng ký của SĐT này đang chờ admin xác nhận.");
                    forward(req, resp, "/WEB-INF/jsp/client/register.jsp");
                    return;
                }
                if ("ACTIVE_EXISTS".equals(result)) {
                    req.setAttribute("error", "SĐT này đã có tài khoản hoạt động, vui lòng đăng nhập.");
                    forward(req, resp, "/WEB-INF/jsp/client/register.jsp");
                    return;
                }
                HttpSession session = req.getSession(true);
                session.setAttribute("message", "Đăng ký thành công. Vui lòng đợi admin xác nhận để đăng nhập.");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            } catch (Exception e) {
                req.setAttribute("error", "Lỗi DB: " + e.getMessage());
                forward(req, resp, "/WEB-INF/jsp/client/register.jsp");
                return;
            }
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}

