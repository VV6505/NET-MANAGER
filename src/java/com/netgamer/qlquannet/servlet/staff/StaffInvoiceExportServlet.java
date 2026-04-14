package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.StaffInvoiceBillDao;
import com.netgamer.qlquannet.dao.StaffInvoiceBillDao.BillDetail;
import com.netgamer.qlquannet.model.InvoiceLineItem;
import com.netgamer.qlquannet.report.ShopInfo;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StaffInvoiceExportServlet extends BaseServlet {
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        String maHoaDon = req.getParameter("maHoaDon");
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu maHoaDon");
            return;
        }

        try {
            BillDetail d = new StaffInvoiceBillDao(getServletContext()).getByInvoiceId(maHoaDon.trim());
            if (d == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn");
                return;
            }
            String file = "bill_" + d.maHoaDon + ".html";
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html; charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(file, StandardCharsets.UTF_8));
            resp.getWriter().write(renderHtml(d));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi xuất bill: " + e.getMessage());
        }
    }

    private String renderHtml(BillDetail d) {
        DecimalFormat df = new DecimalFormat("#,##0");
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html><html lang='vi'><head><meta charset='utf-8'><title>Bill ")
          .append(esc(d.maHoaDon))
          .append("</title><style>")
          .append("body{font-family:Arial,sans-serif;background:#f3f5fa;color:#172b4d;line-height:1.45} .wrap{max-width:900px;margin:20px auto;background:#fff;border:1px solid #dbe3ef;border-radius:12px;padding:18px;box-shadow:0 10px 24px rgba(23,43,77,.08)}")
          .append("table{width:100%;border-collapse:collapse} th,td{padding:9px 10px;border-bottom:1px solid #e9eef5} th{text-align:left;background:#f7faff;color:#2d4f7d;font-size:12px}")
          .append(".r{text-align:right}.k{color:#6b7b94;font-size:12px}.row{display:flex;justify-content:space-between;gap:12px;margin:6px 0}")
          .append("h2{color:#102a43;font-size:28px;letter-spacing:.2px} h3{color:#1f3b63;margin:14px 0 6px;font-size:22px}")
          .append("hr{border:none;border-top:1px solid #e2e8f0;margin:10px 0} b{color:#0f172a} .thanks{margin-top:14px;padding-top:10px;border-top:1px dashed #cbd5e1;text-align:center;color:#334e68;font-weight:600}")
          .append("</style></head><body><div class='wrap'>");
        sb.append("<h2 style='margin:0 0 6px;'>").append(esc(ShopInfo.TEN_QUAN)).append(" - BILL THANH TOÁN</h2>");
        sb.append("<div class='k'>").append(esc(ShopInfo.DIA_CHI)).append(" | ").append(esc(ShopInfo.DIEN_THOAI)).append("</div>");
        sb.append("<hr/>");
        sb.append("<div class='row'><div><b>Mã hóa đơn:</b> ").append(esc(d.maHoaDon)).append("</div><div><b>Ngày:</b> ").append(esc(d.ngay != null ? d.ngay.toString() : "")).append("</div></div>");
        sb.append("<div class='row'><div><b>Khách:</b> ").append(esc(d.tenKhachHang)).append(" (").append(esc(d.maKhachHang)).append(")</div><div><b>SĐT:</b> ").append(esc(d.sdt)).append("</div></div>");
        sb.append("<div class='row'><div><b>Máy:</b> ").append(esc(d.maMay)).append(" - ").append(esc(d.tenMay)).append("</div><div><b>Trạng thái:</b> ").append(esc(d.trangThai)).append("</div></div>");
        sb.append("<div class='row'><div><b>Giờ vào:</b> ").append(esc(d.thoiGianVao != null ? d.thoiGianVao.format(DT) : "")).append("</div><div><b>Giờ ra:</b> ").append(esc(d.thoiGianRa != null ? d.thoiGianRa.format(DT) : "")).append("</div></div>");
        sb.append("<div class='row'><div><b>Số giờ chơi:</b> ").append(d.soGioChoi).append("</div><div><b>Email:</b> ").append(esc(d.email)).append("</div></div>");
        sb.append("<h3>Dịch vụ đồ ăn</h3><table><thead><tr><th>Tên món</th><th class='r'>SL</th><th class='r'>Đơn giá</th><th class='r'>Thành tiền</th></tr></thead><tbody>");
        if (d.foods != null) {
            for (InvoiceLineItem it : d.foods) {
                sb.append("<tr><td>").append(esc(it.getTen())).append("</td><td class='r'>").append(it.getSoLuong())
                  .append("</td><td class='r'>").append(df.format(it.getDonGia())).append("đ</td><td class='r'>")
                  .append(df.format(it.getThanhTien())).append("đ</td></tr>");
            }
        }
        sb.append("</tbody></table><hr/>");
        sb.append("<div class='row'><div>Tiền dịch vụ đồ ăn</div><div><b>").append(df.format(d.tongTienDoAn)).append("đ</b></div></div>");
        sb.append("<div class='row'><div>Tiền thuê máy</div><div><b>").append(df.format(d.tienMay)).append("đ</b></div></div>");
        sb.append("<div class='row'><div><b>TỔNG THANH TOÁN</b></div><div><b>").append(df.format(d.tongTien)).append("đ</b></div></div>");
        sb.append("<div class='thanks'>Cảm ơn quý khách. hẹn gặp lại!</div>");
        sb.append("</div></body></html>");
        return sb.toString();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
