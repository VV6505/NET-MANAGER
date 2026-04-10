package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StaffHomeServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        forward(req, resp, "/WEB-INF/jsp/staff/dashboard.jsp");
    }
}

