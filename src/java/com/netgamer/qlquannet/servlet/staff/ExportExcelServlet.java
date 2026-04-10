package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.RevenueDao;
import com.netgamer.qlquannet.model.InvoiceSummary;
import com.netgamer.qlquannet.report.ShopInfo;
import com.netgamer.qlquannet.servlet.BaseServlet;
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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Xuất báo cáo doanh thu ra Excel (.xlsx) bằng Apache POI — có header quán, merge cell, style.
 */
public class ExportExcelServlet extends BaseServlet {

    private static final int COLS = 6;

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

            String file = "bao_cao_doanh_thu_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".xlsx";
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(file, StandardCharsets.UTF_8));

            try (Workbook wb = new XSSFWorkbook()) {
                Sheet sh = wb.createSheet("Doanh thu");

                Font fontTitle = wb.createFont();
                fontTitle.setBold(true);
                fontTitle.setFontHeightInPoints((short) 16);

                Font fontBold = wb.createFont();
                fontBold.setBold(true);

                CellStyle titleStyle = wb.createCellStyle();
                titleStyle.setFont(fontTitle);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);
                titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                CellStyle headerBandStyle = wb.createCellStyle();
                headerBandStyle.setFont(fontBold);
                headerBandStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
                headerBandStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Font white = wb.createFont();
                white.setBold(true);
                white.setColor(IndexedColors.WHITE.getIndex());
                headerBandStyle.setFont(white);
                headerBandStyle.setAlignment(HorizontalAlignment.CENTER);
                setThinBorder(headerBandStyle);

                CellStyle labelStyle = wb.createCellStyle();
                labelStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                setThinBorder(labelStyle);

                CellStyle moneyStyle = wb.createCellStyle();
                moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
                setThinBorder(moneyStyle);

                CellStyle totalStyle = wb.createCellStyle();
                totalStyle.setFont(fontBold);
                totalStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                setThinBorder(totalStyle);

                int r = 0;
                // Dòng tiêu đề quán — merge toàn bộ cột
                Row row0 = sh.createRow(r++);
                Cell c0 = row0.createCell(0);
                c0.setCellValue(ShopInfo.TEN_QUAN);
                c0.setCellStyle(titleStyle);
                sh.addMergedRegion(new CellRangeAddress(0, 0, 0, COLS - 1));

                Row row1 = sh.createRow(r++);
                row1.createCell(0).setCellValue("Địa chỉ: " + ShopInfo.DIA_CHI);
                sh.addMergedRegion(new CellRangeAddress(1, 1, 0, COLS - 1));

                Row row2 = sh.createRow(r++);
                row2.createCell(0).setCellValue("SĐT: " + ShopInfo.DIEN_THOAI + "  |  Email: " + ShopInfo.EMAIL);
                sh.addMergedRegion(new CellRangeAddress(2, 2, 0, COLS - 1));

                r++; // dòng trống

                Row rowReport = sh.createRow(r++);
                Cell cr = rowReport.createCell(0);
                cr.setCellValue("BÁO CÁO DOANH THU");
                cr.setCellStyle(titleStyle);
                sh.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, COLS - 1));

                String periodFrom = from != null ? from.toString() : "…";
                String periodTo = to != null ? to.toString() : "…";
                Row rowPeriod = sh.createRow(r++);
                rowPeriod.createCell(0).setCellValue("Từ ngày: " + periodFrom + "     đến ngày: " + periodTo);
                sh.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, COLS - 1));

                r++;

                // Bảng: tiêu đề cột
                Row hrow = sh.createRow(r++);
                String[] heads = {"STT", "Mã HĐ", "Ngày", "Game", "Đồ ăn", "Tổng"};
                for (int i = 0; i < heads.length; i++) {
                    Cell hc = hrow.createCell(i);
                    hc.setCellValue(heads[i]);
                    hc.setCellStyle(headerBandStyle);
                }

                List<InvoiceSummary> list = rr.invoices;
                int stt = 1;
                for (InvoiceSummary inv : list) {
                    Row data = sh.createRow(r++);
                    data.createCell(0).setCellValue(stt++);
                    data.createCell(1).setCellValue(inv.getMaHoaDon());
                    data.createCell(2).setCellValue(inv.getNgay() != null ? inv.getNgay().toString() : "");
                    data.createCell(3).setCellValue(inv.getTongTienGame());
                    data.createCell(4).setCellValue(inv.getTongTienDoAn());
                    data.createCell(5).setCellValue(inv.getTongCong());
                    for (int i = 0; i < COLS; i++) {
                        if (i >= 3) {
                            data.getCell(i).setCellStyle(moneyStyle);
                        } else {
                            data.getCell(i).setCellStyle(labelStyle);
                        }
                    }
                }

                r++;
                Row sumRow = sh.createRow(r);
                Cell cSumLabel = sumRow.createCell(0);
                cSumLabel.setCellValue("Tổng doanh thu");
                cSumLabel.setCellStyle(totalStyle);
                sh.addMergedRegion(new CellRangeAddress(r, r, 0, COLS - 2));
                Cell cSumVal = sumRow.createCell(COLS - 1);
                cSumVal.setCellValue(rr.total);
                cSumVal.setCellStyle(totalStyle);

                for (int i = 0; i < COLS; i++) {
                    sh.autoSizeColumn(i);
                }

                wb.write(resp.getOutputStream());
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi xuất Excel: " + e.getMessage());
        }
    }

    private static void setThinBorder(CellStyle s) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
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
