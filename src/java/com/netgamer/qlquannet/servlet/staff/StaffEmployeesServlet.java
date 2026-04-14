package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.StaffDao;
import com.netgamer.qlquannet.model.Employee;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StaffEmployeesServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        String pathInfo = req.getPathInfo(); // null, /new
        try {
            StaffDao dao = new StaffDao(getServletContext());
            if (!isAdmin(session)) {
                String maNhanVien = (String) session.getAttribute("staffUsername");
                req.setAttribute("employee", dao.findEmployeeById(maNhanVien));
                forward(req, resp, "/WEB-INF/jsp/staff/employee-me.jsp");
                return;
            }
            if (pathInfo != null && pathInfo.endsWith("/new")) {
                if (!isAdmin(session)) {
                    session.setAttribute("errorMessage", "Chỉ admin/quản lý mới được thêm nhân viên.");
                    resp.sendRedirect(req.getContextPath() + "/staff/employees");
                    return;
                }
                req.setAttribute("types", dao.getStaffTypes());
                req.setAttribute("suggestId", dao.nextEmployeeId());
                forward(req, resp, "/WEB-INF/jsp/staff/employees-new.jsp");
                return;
            }
            req.setAttribute("employees", dao.listEmployees());
            forward(req, resp, "/WEB-INF/jsp/staff/employees.jsp");
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi DB: " + e.getMessage());
            forward(req, resp, "/WEB-INF/jsp/staff/employees.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        if (!isAdmin(session)) {
            session.setAttribute("errorMessage", "Chỉ admin/quản lý mới được thêm nhân viên.");
            resp.sendRedirect(req.getContextPath() + "/staff/employees");
            return;
        }
        String pathInfo = req.getPathInfo(); // /create
        if (pathInfo == null || !pathInfo.endsWith("/create")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Employee in = new Employee();
        in.setMaNhanVien(req.getParameter("maNhanVien"));
        in.setTenNhanVien(req.getParameter("tenNhanVien"));
        in.setGioiTinh(req.getParameter("gioiTinh"));
        in.setCmnd(req.getParameter("cmnd"));
        in.setNoiCap(req.getParameter("noiCap"));
        in.setSdt(req.getParameter("sdt"));
        in.setEmail(req.getParameter("email"));
        in.setDiaChi(req.getParameter("diaChi"));
        in.setPhongBan(req.getParameter("phongBan"));
        in.setMatKhau(req.getParameter("matKhau"));
        in.setLuongCoBan(req.getParameter("luongCoBan"));
        in.setMaLoaiNhanVien(req.getParameter("maLoaiNhanVien"));
        in.setTrangThai(req.getParameter("trangThai"));

        try {
            String ns = req.getParameter("ngaySinh");
            if (ns != null && !ns.trim().isEmpty()) in.setNgaySinh(LocalDate.parse(ns.trim()));
        } catch (Exception ignored) {
        }
        try {
            String nc = req.getParameter("ngayCap");
            if (nc != null && !nc.trim().isEmpty()) in.setNgayCap(LocalDate.parse(nc.trim()));
        } catch (Exception ignored) {
        }
        try {
            String nbd = req.getParameter("ngayBatDau");
            if (nbd != null && !nbd.trim().isEmpty()) in.setNgayBatDau(LocalDate.parse(nbd.trim()));
        } catch (Exception ignored) {
        }

        if (in.getTenNhanVien() == null || in.getTenNhanVien().trim().isEmpty()
                || in.getSdt() == null || in.getSdt().trim().isEmpty()
                || in.getEmail() == null || in.getEmail().trim().isEmpty()
                || in.getMatKhau() == null || in.getMatKhau().trim().isEmpty()
                || in.getMaLoaiNhanVien() == null || in.getMaLoaiNhanVien().trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đầy đủ: tên, SĐT, email, mật khẩu, loại nhân viên.");
            try {
                StaffDao dao = new StaffDao(getServletContext());
                req.setAttribute("types", dao.getStaffTypes());
                req.setAttribute("suggestId", dao.nextEmployeeId());
            } catch (Exception ignored) {
            }
            forward(req, resp, "/WEB-INF/jsp/staff/employees-new.jsp");
            return;
        }

        try {
            StaffDao dao = new StaffDao(getServletContext());
            Employee created = dao.createEmployee(in);
            session.setAttribute("successMessage", "Đã thêm nhân viên: " + (created != null ? created.getMaNhanVien() : ""));
            resp.sendRedirect(req.getContextPath() + "/staff/employees");
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi DB: " + e.getMessage());
            try {
                StaffDao dao = new StaffDao(getServletContext());
                req.setAttribute("types", dao.getStaffTypes());
                req.setAttribute("suggestId", dao.nextEmployeeId());
            } catch (Exception ignored) {
            }
            forward(req, resp, "/WEB-INF/jsp/staff/employees-new.jsp");
        }
    }

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("staffRole");
        if (role == null) return false;
        String r = role.toLowerCase();
        return r.contains("quản lý") || r.contains("admin");
    }
}

