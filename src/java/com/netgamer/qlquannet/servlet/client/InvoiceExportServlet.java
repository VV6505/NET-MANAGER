package com.netgamer.qlquannet.servlet.client;

import com.netgamer.qlquannet.dao.InvoiceDetailDao;
import com.netgamer.qlquannet.dao.InvoiceDetailDao.InvoiceDetail;
import com.netgamer.qlquannet.model.Customer;
import com.netgamer.qlquannet.model.InvoiceLineItem;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class InvoiceExportServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Customer customer = session != null ? (Customer) session.getAttribute("customer") : null;
        if (customer == null) {
            resp.sendRedirect(req.getContextPath() + "/login?from=/history");
            return;
        }

        String maHoaDon = req.getParameter("maHoaDon");
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu maHoaDon");
            return;
        }

        try {
            InvoiceDetail detail = new InvoiceDetailDao(getServletContext())
                    .getPaidInvoiceDetailForCustomer(maHoaDon.trim(), customer.getMaKhachHang());
            if (detail == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn đã thanh toán");
                return;
            }

            String file = "hoa_don_" + detail.maHoaDon + ".html";
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(file, StandardCharsets.UTF_8));

            resp.getWriter().write(renderHtml(detail));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi DB: " + e.getMessage());
        }
    }

    private String renderHtml(InvoiceDetail d) {
        DecimalFormat df = new DecimalFormat("#,##0");
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html><html lang='vi'><head>")
          .append("<meta charset='utf-8'/>")
          .append("<meta name='viewport' content='width=device-width,initial-scale=1'/>")
          .append("<title>Hóa đơn ").append(esc(d.maHoaDon)).append("</title>")
          .append("<style>")
          .append("body{font-family:Arial,Helvetica,sans-serif;margin:0;background:#f6f7fb;color:#111}")
          .append(".wrap{max-width:820px;margin:24px auto;padding:18px}")
          .append(".bill{background:#fff;border:1px solid #e8e8ef;border-radius:14px;overflow:hidden}")
          .append(".top{padding:18px 18px 10px;border-bottom:1px dashed #d9dbe6}")
          .append(".center{text-align:center}")
          .append(".title{font-weight:900;letter-spacing:.6px;margin:0;font-size:18px;text-transform:uppercase}")
          .append(".addr{opacity:.85;font-size:12px;line-height:1.4;margin-top:6px}")
          .append(".meta{display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-top:12px}")
          .append(".box{border:1px solid #eef0ff;border-radius:12px;padding:10px;background:#fafbff}")
          .append(".k{font-size:11px;opacity:.7;text-transform:uppercase;letter-spacing:.3px}")
          .append(".v{margin-top:4px;font-weight:800}")
          .append(".mono{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,monospace}")
          .append("table{width:100%;border-collapse:collapse}")
          .append("th,td{padding:10px 12px;border-bottom:1px solid #f0f1f6}")
          .append("th{font-size:12px;opacity:.75;background:#fcfcff;text-align:left}")
          .append(".right{text-align:right;white-space:nowrap}")
          .append(".sec{padding:10px 18px 4px;font-weight:900;text-transform:uppercase;letter-spacing:.3px}")
          .append(".totals{padding:12px 18px;display:grid;gap:8px;border-top:1px dashed #d9dbe6}")
          .append(".row{display:flex;justify-content:space-between;gap:10px}")
          .append(".row b{font-size:16px}")
          .append(".thanks{padding:14px 18px;text-align:center;background:#111;color:#fff;font-weight:800}")
          .append("@media print{body{background:#fff}.wrap{margin:0;max-width:none}.bill{border:none}}")
          .append("</style></head><body><div class='wrap'><div class='bill'>");

        sb.append("<div class='top'>")
          .append("<div class='center'>")
          .append("<div class='title'>NET 269 - HÓA ĐƠN THANH TOÁN</div>")
          .append("<div class='addr'>Địa chỉ: 123 Đường Gaming, TP.HCM</div>")
          .append("</div>");

        sb.append("<div class='meta'>")
          .append(box("Mã hóa đơn", "<span class='mono'>" + esc(d.maHoaDon) + "</span>"))
          .append(box("Ngày", "<span class='mono'>" + esc(d.ngay != null ? d.ngay.toString() : "") + "</span>"))
          .append(box("Khách hàng", esc(d.tenKhachHang)))
          .append(box("SĐT / Email", esc(d.sdt) + " • " + esc(d.email)))
          .append("</div></div>");

        if (d.foods != null && !d.foods.isEmpty()) {
            sb.append("<div class='sec'>Dịch vụ • Đồ ăn & thức uống</div>");
            sb.append(tableHead());
            for (InvoiceLineItem it : d.foods) sb.append(row(it, df));
            sb.append("</tbody></table>");
        }
        sb.append("<div class='totals'>")
          .append(totalRow("Tiền thuê máy", df.format(d.tienMay) + " đ"))
          .append(totalRow("Tổng đồ ăn", df.format(d.tongTienDoAn) + " đ"))
          .append("<div class='row'><div class='k'>TỔNG CỘNG</div><div class='right'><b>")
          .append(df.format(d.tongTien)).append(" đ</b></div></div>")
          .append("</div>");

        sb.append("<div class='thanks'>Cảm ơn mọi người và hẹn gặp lại!</div>");
        sb.append("</div></div></body></html>");
        return sb.toString();
    }

    private String tableHead() {
        return "<table><thead><tr>" +
                "<th>Tên dịch vụ</th>" +
                "<th class='right'>SL</th>" +
                "<th class='right'>Đơn giá</th>" +
                "<th class='right'>Thành tiền</th>" +
                "</tr></thead><tbody>";
    }

    private String row(InvoiceLineItem it, DecimalFormat df) {
        return "<tr>" +
                "<td>" + esc(it.getTen()) + "</td>" +
                "<td class='right'>" + it.getSoLuong() + "</td>" +
                "<td class='right'>" + df.format(it.getDonGia()) + " đ</td>" +
                "<td class='right'>" + df.format(it.getThanhTien()) + " đ</td>" +
                "</tr>";
    }

    private String box(String k, String v) {
        return "<div class='box'><div class='k'>" + k + "</div><div class='v'>" + v + "</div></div>";
    }

    private String totalRow(String k, String v) {
        return "<div class='row'><div class='k'>" + esc(k) + "</div><div class='right'>" + esc(v) + "</div></div>";
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}

