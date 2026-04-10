package com.netgamer.qlquannet.servlet.client;

import com.netgamer.qlquannet.dao.CatalogDao;
import com.netgamer.qlquannet.dao.CartDao;
import com.netgamer.qlquannet.model.Food;
import com.netgamer.qlquannet.model.Game;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HomeServlet extends BaseServlet {

    private static String norm(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        // bỏ dấu tiếng Việt + đưa về lowercase để tìm gần đúng
        n = n.replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT).trim();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            CatalogDao dao = new CatalogDao(getServletContext());
            List<Game> games = dao.getAllGames();
            List<Food> foods = dao.getAllFoods();

            String qRaw = req.getParameter("q");
            String q = qRaw != null ? qRaw.trim() : "";
            if (!q.isEmpty()) {
                String nq = norm(q);
                games = games.stream()
                        .filter(g -> norm(g.getTenGame()).contains(nq) || norm(g.getTheLoai()).contains(nq))
                        .collect(Collectors.toList());
                foods = foods.stream()
                        .filter(f -> norm(f.getTenDoAn()).contains(nq) || norm(f.getLoaiDoAn()).contains(nq))
                        .collect(Collectors.toList());
                req.setAttribute("searchQuery", q);
            }

            req.setAttribute("dsGame", games);
            req.setAttribute("dsFood", foods);

            HttpSession session = req.getSession(false);
            if (session != null) {
                Object c = session.getAttribute("customer");
                if (c instanceof com.netgamer.qlquannet.model.Customer) {
                    String maKhachHang = ((com.netgamer.qlquannet.model.Customer) c).getMaKhachHang();
                    try {
                        session.setAttribute("cartCount", new CartDao(getServletContext()).countItems(maKhachHang));
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception e) {
            // Nếu DB lỗi thì vẫn render được trang + báo lỗi
            req.setAttribute("dbError", e.getMessage());
            req.setAttribute("dsGame", java.util.Collections.emptyList());
            req.setAttribute("dsFood", java.util.Collections.emptyList());
        }
        forward(req, resp, "/WEB-INF/jsp/client/home.jsp");
    }
}

