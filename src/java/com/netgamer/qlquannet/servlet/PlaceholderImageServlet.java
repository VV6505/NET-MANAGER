package com.netgamer.qlquannet.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PlaceholderImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        String label = "NET 269";
        String accent = "#7c5cff";
        if ("food".equalsIgnoreCase(type)) {
            label = "FOOD";
            accent = "#2dd4bf";
        } else if ("game".equalsIgnoreCase(type)) {
            label = "GAME";
            accent = "#7c5cff";
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("image/svg+xml");

        String svg =
                "<svg xmlns='http://www.w3.org/2000/svg' width='960' height='540' viewBox='0 0 960 540'>" +
                "<defs>" +
                "<linearGradient id='bg' x1='0' y1='0' x2='1' y2='1'>" +
                "<stop offset='0' stop-color='#0b1220'/>" +
                "<stop offset='1' stop-color='#121f3a'/>" +
                "</linearGradient>" +
                "</defs>" +
                "<rect width='960' height='540' fill='url(#bg)'/>" +
                "<circle cx='760' cy='120' r='140' fill='" + accent + "' opacity='0.22'/>" +
                "<circle cx='180' cy='420' r='180' fill='" + accent + "' opacity='0.14'/>" +
                "<rect x='210' y='160' width='540' height='220' rx='28' fill='rgba(255,255,255,0.06)' stroke='rgba(255,255,255,0.10)'/>" +
                "<text x='480' y='285' font-size='48' text-anchor='middle' fill='rgba(232,238,252,0.92)' font-family='Arial, sans-serif' font-weight='800'>" + label + "</text>" +
                "<text x='480' y='330' font-size='16' text-anchor='middle' fill='rgba(169,180,208,0.9)' font-family='Arial, sans-serif'>Image not found</text>" +
                "</svg>";

        byte[] bytes = svg.getBytes(StandardCharsets.UTF_8);
        resp.setContentLength(bytes.length);
        resp.getOutputStream().write(bytes);
    }
}

