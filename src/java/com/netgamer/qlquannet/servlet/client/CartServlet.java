package com.netgamer.qlquannet.servlet.client;

import com.netgamer.qlquannet.dao.CartDao;
import com.netgamer.qlquannet.dao.CartDao.CartState;
import com.netgamer.qlquannet.model.Customer;
import com.netgamer.qlquannet.model.CartDbItem;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CartServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Customer customer = session != null ? (Customer) session.getAttribute("customer") : null;
        if (customer == null) {
            req.setAttribute("cartItems", java.util.Collections.emptyList());
            req.setAttribute("total", 0);
            forward(req, resp, "/WEB-INF/jsp/client/cart.jsp");
            return;
        }

        try {
            CartDao dao = new CartDao(getServletContext());
            CartState st = dao.getTempCart(customer.getMaKhachHang());
            req.setAttribute("cartItems", st.items);
            req.setAttribute("total", st.tongTien);
            req.setAttribute("totalFood", st.tongTienDoAn);
            session.setAttribute("cartCount", dao.countItems(customer.getMaKhachHang()));
        } catch (Exception e) {
            req.setAttribute("cartItems", java.util.Collections.emptyList());
            req.setAttribute("total", 0);
            session.setAttribute("errorMessage", "Lỗi DB: " + e.getMessage());
        }
        forward(req, resp, "/WEB-INF/jsp/client/cart.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // /add, /update, /clear, /checkout
        if (pathInfo == null) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        HttpSession session = req.getSession(false);
        Customer customer = session != null ? (Customer) session.getAttribute("customer") : null;
        if (customer == null) {
            resp.sendRedirect(req.getContextPath() + "/login?from=/cart");
            return;
        }

        try {
            CartDao dao = new CartDao(getServletContext());
            String maKhachHang = customer.getMaKhachHang();

            switch (pathInfo) {
                case "/add": {
                    String id = req.getParameter("id");
                    String redirect = req.getParameter("redirect"); // home | cart (default)
                    if (id != null && !id.trim().isEmpty()) {
                        CartState st = dao.getTempCart(maKhachHang);
                        int current = findQty(st.items, id.trim());
                        String result = dao.setItemQuantity(maKhachHang, id.trim(), current + 1);
                        if (result != null && result.startsWith("Error")) {
                            session.setAttribute("errorMessage", result);
                        } else {
                            session.setAttribute("message", "Đã thêm vào giỏ hàng.");
                            session.setAttribute("orderConfirmed", Boolean.FALSE);
                        }
                    }
                    if ("home".equalsIgnoreCase(redirect)) {
                        resp.sendRedirect(req.getContextPath() + "/home");
                        return;
                    }
                    break;
                }
                case "/update": {
                    String id = req.getParameter("itemId");
                    String action = req.getParameter("action");
                    if (id != null && action != null) {
                        CartState st = dao.getTempCart(maKhachHang);
                        int current = findQty(st.items, id.trim());
                        int next = current;
                        if ("increase".equals(action)) next = current + 1;
                        if ("decrease".equals(action)) next = Math.max(1, current - 1);
                        if ("remove".equals(action)) next = 0;
                        String result = dao.setItemQuantity(maKhachHang, id.trim(), next);
                        if (result != null && result.startsWith("Error")) {
                            session.setAttribute("errorMessage", result);
                        } else {
                            session.setAttribute("orderConfirmed", Boolean.FALSE);
                        }
                    }
                    break;
                }
                case "/clear": {
                    String result = dao.clearCart(maKhachHang);
                    if (result != null && result.startsWith("Error")) {
                        session.setAttribute("errorMessage", result);
                    } else {
                        session.setAttribute("message", "Đã xóa giỏ hàng.");
                        session.setAttribute("orderConfirmed", Boolean.FALSE);
                    }
                    break;
                }
                case "/checkout": {
                    CartState st = dao.getTempCart(maKhachHang);
                    session.setAttribute("message", "Đã xác nhận món ăn trong phiên thuê hiện tại.");
                    session.setAttribute("orderNotice", "Bạn đã đặt món thành công. Tạm tính dịch vụ đồ ăn: " + String.format("%,.0f", st.tongTienDoAn) + "đ");
                    session.setAttribute("orderConfirmed", Boolean.TRUE);
                    break;
                }
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
            }

            session.setAttribute("cartCount", dao.countItems(maKhachHang));
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Lỗi DB: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private int findQty(List<CartDbItem> items, String id) {
        if (items == null) return 0;
        for (CartDbItem it : items) {
            if (id.equals(it.getId())) return it.getQuantity();
        }
        return 0;
    }
}

