package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.RevenueDao;
import com.netgamer.qlquannet.model.InvoiceSummary;
import com.netgamer.qlquannet.report.ShopInfo;
import com.netgamer.qlquannet.servlet.BaseServlet;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Xuất báo cáo doanh thu PDF bằng iText 5 — khung viền bảng, chữ ký quản lý, font Unicode (Arial Windows).
 */
public class ExportPDFServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }

        LocalDate from = parseDate(req.getParameter("from"));
        LocalDate to = parseDate(req.getParameter("to"));
        String customerName = req.getParameter("customerName");

        try {
            RevenueDao.RevenueResult rr = new RevenueDao(getServletContext()).getPaidInvoices(from, to, customerName);

            String file = "bao_cao_doanh_thu_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(file, StandardCharsets.UTF_8));

            BaseFont bf = resolveVietnameseBaseFont();
            Font titleFont = new Font(bf, 18, Font.BOLD, new BaseColor(0, 90, 140));
            Font subFont = new Font(bf, 11, Font.NORMAL, BaseColor.DARK_GRAY);
            Font bold11 = new Font(bf, 11, Font.BOLD);
            Font normal11 = new Font(bf, 10, Font.NORMAL);

            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, resp.getOutputStream());
            doc.open();

            // Viền ngoài “khung” báo cáo (bảng 1 ô bọc nội dung) — bonus
            PdfPTable outer = new PdfPTable(1);
            outer.setWidthPercentage(100);
            PdfPCell frame = new PdfPCell();
            frame.setBorderWidth(2f);
            frame.setBorderColor(new BaseColor(0, 120, 90));
            frame.setPadding(14f);

            Paragraph pShop = new Paragraph(ShopInfo.TEN_QUAN, titleFont);
            pShop.setAlignment(Element.ALIGN_CENTER);
            frame.addElement(pShop);

            Paragraph pAddr = new Paragraph(ShopInfo.DIA_CHI + "\nSĐT: " + ShopInfo.DIEN_THOAI, subFont);
            pAddr.setAlignment(Element.ALIGN_CENTER);
            pAddr.setSpacingAfter(10f);
            frame.addElement(pAddr);

            Paragraph pRep = new Paragraph("BÁO CÁO DOANH THU", new Font(bf, 14, Font.BOLD, BaseColor.BLACK));
            pRep.setAlignment(Element.ALIGN_CENTER);
            pRep.setSpacingAfter(6f);
            frame.addElement(pRep);

            String periodFrom = from != null ? from.toString() : "…";
            String periodTo = to != null ? to.toString() : "…";
            Paragraph pPer = new Paragraph("Từ ngày: " + periodFrom + "   đến ngày: " + periodTo, normal11);
            pPer.setAlignment(Element.ALIGN_CENTER);
            pPer.setSpacingAfter(12f);
            frame.addElement(pPer);

            PdfPTable table = new PdfPTable(new float[]{0.6f, 1.2f, 1.2f, 1.2f, 1.2f, 1.4f});
            table.setWidthPercentage(100);
            table.setSpacingBefore(4f);
            table.setSpacingAfter(16f);

            addHeaderCell(table, "STT", bold11);
            addHeaderCell(table, "Mã HĐ", bold11);
            addHeaderCell(table, "Ngày", bold11);
            addHeaderCell(table, "Game", bold11);
            addHeaderCell(table, "Đồ ăn", bold11);
            addHeaderCell(table, "Tổng", bold11);

            List<InvoiceSummary> list = rr.invoices;
            int stt = 1;
            for (InvoiceSummary inv : list) {
                addBodyCell(table, String.valueOf(stt++), normal11, Element.ALIGN_CENTER);
                addBodyCell(table, inv.getMaHoaDon(), normal11, Element.ALIGN_LEFT);
                addBodyCell(table, inv.getNgay() != null ? inv.getNgay().toString() : "", normal11, Element.ALIGN_CENTER);
                addBodyCell(table, formatMoney(inv.getTongTienGame()), normal11, Element.ALIGN_RIGHT);
                addBodyCell(table, formatMoney(inv.getTongTienDoAn()), normal11, Element.ALIGN_RIGHT);
                addBodyCell(table, formatMoney(inv.getTongCong()), normal11, Element.ALIGN_RIGHT);
            }

            frame.addElement(table);

            Paragraph pTotal = new Paragraph("Tổng doanh thu: " + formatMoney(rr.total) + " đ", bold11);
            pTotal.setSpacingAfter(18f);
            frame.addElement(pTotal);

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            frame.addElement(new Paragraph("Ngày xuất báo cáo: " + now, normal11));

            Paragraph sig = new Paragraph("\n\n_________________________\nChữ ký quản lý", normal11);
            sig.setAlignment(Element.ALIGN_RIGHT);
            sig.setSpacingBefore(24f);
            frame.addElement(sig);

            outer.addCell(frame);
            doc.add(outer);
            doc.close();
        } catch (DocumentException e) {
            throw new IOException(e);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi xuất PDF: " + e.getMessage());
        }
    }

    private static void addHeaderCell(PdfPTable table, String text, Font f) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(new BaseColor(0, 110, 130));
        cell.setBorderWidth(1f);
        cell.setBorderColor(BaseColor.WHITE);
        cell.setPadding(6f);
        table.addCell(cell);
    }

    private static void addBodyCell(PdfPTable table, String text, Font f, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderWidth(0.8f);
        cell.setBorderColor(new BaseColor(180, 190, 200));
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private static String formatMoney(double v) {
        return String.format("%,.0f", v);
    }

    /**
     * Ưu tiên Arial.ttf trên Windows để hiển thị tiếng Việt; fallback Helvetica (có thể lỗi dấu).
     */
    private static BaseFont resolveVietnameseBaseFont() throws Exception {
        String windir = System.getenv("WINDIR");
        String[] candidates = {
            "C:/Windows/Fonts/arial.ttf",
            windir != null ? windir + "/Fonts/arial.ttf" : null,
            "C:/Windows/Fonts/tahoma.ttf",
            windir != null ? windir + "/Fonts/tahoma.ttf" : null
        };
        for (String path : candidates) {
            if (path != null && new java.io.File(path).isFile()) {
                return BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
        }
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    }

    private LocalDate parseDate(String s) {
        try {
            if (s == null || s.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(s.trim());
        } catch (Exception ignored) {
            return null;
        }
    }
}
