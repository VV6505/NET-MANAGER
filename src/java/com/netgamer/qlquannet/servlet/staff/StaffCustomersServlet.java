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
}

