package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.RevenueDao;
import com.netgamer.qlquannet.model.InvoiceSummary;
import com.netgamer.qlquannet.report.ShopInfo;
import com.netgamer.qlquannet.servlet.BaseServlet;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
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

public class ExportPDFServlet extends BaseServlet {

    private static final BaseColor C_NAVY       = new BaseColor(27, 42, 74);
    private static final BaseColor C_NAVY_LIGHT = new BaseColor(38, 58, 100);
    private static final BaseColor C_TEAL       = new BaseColor(13, 115, 119);
    private static final BaseColor C_GOLD       = new BaseColor(245, 166, 35);
    private static final BaseColor C_WHITE      = BaseColor.WHITE;
    private static final BaseColor C_STRIPE     = new BaseColor(247, 249, 252);
    private static final BaseColor C_MINT       = new BaseColor(232, 245, 233);
    private static final BaseColor C_GREEN_DARK = new BaseColor(27, 94, 32);
    private static final BaseColor C_BORDER     = new BaseColor(189, 202, 220);
    private static final BaseColor C_TEXT_DARK  = new BaseColor(30, 40, 60);
    private static final BaseColor C_TEXT_MUTED = new BaseColor(84, 98, 122);
    private static final BaseColor C_SUB_HDR    = new BaseColor(210, 224, 245);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("staffUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff/login");
            return;
        }
        if (!canViewRevenue(session)) {
            session.setAttribute("errorMessage", "Bạn không có quyền xem doanh thu.");
            resp.sendRedirect(req.getContextPath() + "/staff");
            return;
        }

        LocalDate from = parseDate(req.getParameter("from"));
        LocalDate to   = parseDate(req.getParameter("to"));
        String month   = req.getParameter("month");
        if (month != null && month.matches("\\d{4}-\\d{2}")) {
            try {
                java.time.YearMonth ym = java.time.YearMonth.parse(month);
                from = ym.atDay(1);
                to   = ym.atEndOfMonth();
            } catch (Exception ignored) {}
        }
        String customerName = req.getParameter("customerName");

        try {
            RevenueDao.RevenueResult rr =
                    new RevenueDao(getServletContext()).getPaidInvoices(from, to, customerName);

            String fileName = "bao_cao_doanh_thu_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
                    + ".pdf";
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition",
                    "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

            BaseFont bf = resolveBaseFont();
            Font fShopName  = new Font(bf, 22, Font.BOLD, C_WHITE);
            Font fShopAddr  = new Font(bf, 10, Font.NORMAL, C_SUB_HDR);
            Font fReportLbl = new Font(bf, 13, Font.BOLD, C_GOLD);
            Font fPeriod    = new Font(bf, 11, Font.NORMAL, C_NAVY_LIGHT);
            Font fColHdr    = new Font(bf, 10, Font.BOLD, C_WHITE);
            Font fData      = new Font(bf, 10, Font.NORMAL, C_TEXT_DARK);
            Font fTotalLbl  = new Font(bf, 11, Font.BOLD, C_GREEN_DARK);
            Font fTotalVal  = new Font(bf, 12, Font.BOLD, C_GREEN_DARK);
            Font fFooter    = new Font(bf, 9, Font.ITALIC, C_TEXT_MUTED);
            Font fSig       = new Font(bf, 10, Font.ITALIC, C_TEXT_DARK);

            Document doc = new Document(PageSize.A4, 30, 30, 30, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, resp.getOutputStream());
            writer.setPageEvent(new FooterEvent(bf, fFooter));
            doc.open();

            PdfPTable banner = new PdfPTable(1);
            banner.setWidthPercentage(100);
            banner.setSpacingAfter(0f);

            PdfPCell cTop = noBorderCell();
            cTop.setBackgroundColor(C_NAVY);
            cTop.setPaddingTop(18f);
            cTop.setPaddingBottom(4f);
            cTop.setPaddingLeft(20f);
            Paragraph pShop = new Paragraph(ShopInfo.TEN_QUAN, fShopName);
            pShop.setAlignment(Element.ALIGN_CENTER);
            cTop.addElement(pShop);
            banner.addCell(cTop);

            PdfPCell cBot = noBorderCell();
            cBot.setBackgroundColor(C_NAVY_LIGHT);
            cBot.setPaddingTop(6f);
            cBot.setPaddingBottom(10f);
            Paragraph pAddr = new Paragraph(ShopInfo.DIA_CHI
                    + "   |   " + ShopInfo.DIEN_THOAI
                    + "   |   " + ShopInfo.EMAIL, fShopAddr);
            pAddr.setAlignment(Element.ALIGN_CENTER);
            cBot.addElement(pAddr);
            banner.addCell(cBot);
            doc.add(banner);

            PdfPTable titleBar = new PdfPTable(1);
            titleBar.setWidthPercentage(100);
            titleBar.setSpacingBefore(0f);
            titleBar.setSpacingAfter(12f);
            PdfPCell cTitle = noBorderCell();
            cTitle.setBackgroundColor(new BaseColor(240, 244, 255));
            cTitle.setPaddingTop(10f);
            cTitle.setPaddingBottom(4f);
            Paragraph pRep = new Paragraph("BÁO CÁO DOANH THU", fReportLbl);
            pRep.setAlignment(Element.ALIGN_CENTER);
            cTitle.addElement(pRep);

            String pFrom = from != null ? from.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "…";
            String pTo   = to   != null ? to.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "…";
            Paragraph pPeriod = new Paragraph(
                    "Kỳ báo cáo:  " + pFrom + "  ─  " + pTo
                    + "        Ngày xuất: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    fPeriod);
            pPeriod.setAlignment(Element.ALIGN_CENTER);
            pPeriod.setSpacingBefore(4f);
            pPeriod.setSpacingAfter(10f);
            cTitle.addElement(pPeriod);
            titleBar.addCell(cTitle);
            doc.add(titleBar);

            PdfPTable table = new PdfPTable(new float[]{0.6f, 1.6f, 1.3f, 1.4f, 1.3f, 1.6f});
            table.setWidthPercentage(100);
            table.setSpacingAfter(16f);
            table.setHeaderRows(1);

            addHdr(table, "STT", fColHdr);
            addHdr(table, "Mã Hoá Đơn", fColHdr);
            addHdr(table, "Ngày", fColHdr);
            addHdr(table, "Tiền Máy (đ)", fColHdr);
            addHdr(table, "Đồ Ăn (đ)", fColHdr);
            addHdr(table, "Tổng Cộng (đ)", fColHdr);

            List<InvoiceSummary> list = rr.invoices;
            int stt = 1;
            for (InvoiceSummary inv : list) {
                boolean stripe = (stt % 2 == 0);
                BaseColor bg = stripe ? C_STRIPE : C_WHITE;
                addData(table, String.valueOf(stt++), fData, Element.ALIGN_CENTER, bg);
                addData(table, inv.getMaHoaDon(), fData, Element.ALIGN_LEFT, bg);
                addData(table, inv.getNgay() != null
                        ? inv.getNgay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "",
                        fData, Element.ALIGN_CENTER, bg);
                addData(table, fmt(inv.getTienGioChoi()), fData, Element.ALIGN_RIGHT, bg);
                addData(table, fmt(inv.getTongTienDoAn()), fData, Element.ALIGN_RIGHT, bg);
                addData(table, fmt(inv.getTongCong()), fData, Element.ALIGN_RIGHT, bg);
            }
            doc.add(table);

            PdfPTable totalTbl = new PdfPTable(new float[]{4.3f, 1.5f});
            totalTbl.setWidthPercentage(100);
            totalTbl.setSpacingAfter(24f);
            PdfPCell cTlbl = new PdfPCell(new Phrase("✦  TỔNG DOANH THU", fTotalLbl));
            cTlbl.setBackgroundColor(C_MINT);
            cTlbl.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cTlbl.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cTlbl.setBorderColor(C_BORDER);
            cTlbl.setBorderWidth(1f);
            cTlbl.setPadding(8f);
            totalTbl.addCell(cTlbl);

            PdfPCell cTval = new PdfPCell(new Phrase(fmt(rr.total) + " đ", fTotalVal));
            cTval.setBackgroundColor(C_MINT);
            cTval.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cTval.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cTval.setBorderColor(C_BORDER);
            cTval.setBorderWidth(1f);
            cTval.setPadding(8f);
            totalTbl.addCell(cTval);
            doc.add(totalTbl);

            PdfPTable divider = new PdfPTable(1);
            divider.setWidthPercentage(100);
            divider.setSpacingAfter(20f);
            PdfPCell cDiv = noBorderCell();
            cDiv.setBackgroundColor(C_TEAL);
            cDiv.setFixedHeight(2f);
            divider.addCell(cDiv);
            doc.add(divider);

            PdfPTable sigTbl = new PdfPTable(new float[]{1f, 1f});
            sigTbl.setWidthPercentage(100);
            PdfPCell cSigLeft = noBorderCell();
            Paragraph pNote = new Paragraph("Người lập báo cáo\n\n\n\n________________________", fSig);
            pNote.setAlignment(Element.ALIGN_CENTER);
            cSigLeft.addElement(pNote);
            sigTbl.addCell(cSigLeft);

            PdfPCell cSigRight = noBorderCell();
            Paragraph pMgr = new Paragraph("Quản lý xác nhận\n\n\n\n________________________", fSig);
            pMgr.setAlignment(Element.ALIGN_CENTER);
            cSigRight.addElement(pMgr);
            sigTbl.addCell(cSigRight);
            doc.add(sigTbl);

            doc.close();
        } catch (DocumentException e) {
            throw new IOException(e);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Lỗi xuất PDF: " + e.getMessage());
        }
    }

    private static void addHdr(PdfPTable t, String text, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBackgroundColor(C_TEAL);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setBorderColor(new BaseColor(5, 80, 84));
        c.setBorderWidth(1f);
        c.setPaddingTop(8f);
        c.setPaddingBottom(8f);
        t.addCell(c);
    }

    private static void addData(PdfPTable t, String text, Font f, int align, BaseColor bg) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBackgroundColor(bg);
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setBorderColor(C_BORDER);
        c.setBorderWidth(0.8f);
        c.setPaddingTop(5f);
        c.setPaddingBottom(5f);
        c.setPaddingLeft(6f);
        c.setPaddingRight(6f);
        t.addCell(c);
    }

    private static PdfPCell noBorderCell() {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }

    private static String fmt(double v) {
        return String.format("%,.0f", v);
    }

    private static BaseFont resolveBaseFont() throws Exception {
        String windir = System.getenv("WINDIR");
        String[] candidates = {
            "C:/Windows/Fonts/arial.ttf",
            windir != null ? windir + "/Fonts/arial.ttf"  : null,
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
            if (s == null || s.trim().isEmpty()) return null;
            return LocalDate.parse(s.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean canViewRevenue(HttpSession session) {
        String role = (String) session.getAttribute("staffRole");
        if (role == null) return false;
        String r = role.toLowerCase();
        return r.contains("quản lý") || r.contains("thu ngân");
    }

    private static class FooterEvent extends PdfPageEventHelper {
        private final BaseFont bf;

        FooterEvent(BaseFont bf, Font unused) {
            this.bf = bf;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            String footer = "NET 269  |  Trang " + writer.getPageNumber();
            float x = (document.left() + document.right()) / 2;
            float y = document.bottom() - 20;
            cb.beginText();
            cb.setFontAndSize(bf, 9);
            cb.setColorFill(new GrayColor(0.5f));
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, footer, x, y, 0);
            cb.endText();

            cb.setLineWidth(0.8f);
            cb.setColorStroke(new BaseColor(13, 115, 119));
            cb.moveTo(document.left(), y + 12);
            cb.lineTo(document.right(), y + 12);
            cb.stroke();
        }
    }
}
