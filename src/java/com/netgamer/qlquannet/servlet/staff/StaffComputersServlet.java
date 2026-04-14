package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.ComputerDao;
import com.netgamer.qlquannet.dao.CustomerDao;
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
            req.setAttribute("rentCustomers", new CustomerDao(getServletContext()).findActiveForRental());
        } catch (Exception e) {
            req.setAttribute("computers", java.util.Collections.emptyList());
            req.setAttribute("rentCustomers", java.util.Collections.emptyList());
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

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String maMay = req.getParameter("maMay");
        ComputerDao dao = new ComputerDao(getServletContext());
        try {
            if ("/status".equals(pathInfo)) {
                String trangThai = req.getParameter("trangThai");
                if (maMay == null || maMay.trim().isEmpty() || trangThai == null || trangThai.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Thiếu mã máy hoặc trạng thái.");
                } else {
                    dao.updateStatus(maMay.trim(), trangThai.trim());
                    session.setAttribute("successMessage", "Cập nhật trạng thái thành công.");
                }
            } else if ("/start-rental".equals(pathInfo)) {
                String maKhachHang = req.getParameter("maKhachHang");
                String maNhanVien = (String) session.getAttribute("staffUsername");
                if (maMay == null || maMay.trim().isEmpty() || maKhachHang == null || maKhachHang.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Thiếu mã máy hoặc mã khách hàng.");
                } else if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Không xác định được nhân viên đăng nhập.");
                } else {
                    String result = dao.startRental(maMay.trim(), maKhachHang.trim(), maNhanVien.trim());
                    if (result.startsWith("Error")) {
                        session.setAttribute("errorMessage", result);
                    } else {
                        session.setAttribute("successMessage", "Đã bắt đầu phiên thuê cho khách hàng.");
                    }
                }
            } else if ("/end-rental".equals(pathInfo)) {
                String maNhanVien = (String) session.getAttribute("staffUsername");
                if (maMay == null || maMay.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Thiếu mã máy.");
                } else if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
                    session.setAttribute("errorMessage", "Không xác định được nhân viên đăng nhập.");
                } else {
                    String result = dao.endRental(maMay.trim(), maNhanVien.trim());
                    if (result.startsWith("Error")) {
                        session.setAttribute("errorMessage", result);
                    } else {
                        session.setAttribute("successMessage", "Đã kết thúc phiên thuê và chốt tiền.");
                    }
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Lỗi DB: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/staff/computers");
    }
}

