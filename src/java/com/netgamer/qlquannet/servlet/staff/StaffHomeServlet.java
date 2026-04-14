package com.netgamer.qlquannet.servlet.staff;

import com.netgamer.qlquannet.dao.ComputerDao;
import com.netgamer.qlquannet.dao.InvoiceDao;
import com.netgamer.qlquannet.dao.RevenueDao;
import com.netgamer.qlquannet.model.Computer;
import com.netgamer.qlquannet.model.InvoiceSummary;
import com.netgamer.qlquannet.servlet.BaseServlet;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        try {
            ComputerDao computerDao = new ComputerDao(getServletContext());
            InvoiceDao invoiceDao = new InvoiceDao(getServletContext());
            RevenueDao revenueDao = new RevenueDao(getServletContext());

            List<Computer> computers = computerDao.findAll();
            List<InvoiceSummary> recentInvoices = invoiceDao.search(null, null, null);

            LocalDate today = LocalDate.now();
            YearMonth ym = YearMonth.now();
            RevenueDao.RevenueResult rrToday = revenueDao.getPaidInvoices(today, today, null);
            RevenueDao.RevenueResult rrMonth = revenueDao.getPaidInvoices(ym.atDay(1), ym.atEndOfMonth(), null);

            int totalComputers = computers.size();
            int busyComputers = 0;
            int maintenanceComputers = 0;
            Set<String> currentCustomers = new HashSet<>();
            for (Computer c : computers) {
                String status = c.getTrangThai() == null ? "" : c.getTrangThai().toLowerCase();
                if (status.contains("đang thuê") || status.contains("dang thue")) {
                    busyComputers++;
                } else if (status.contains("bảo trì") || status.contains("bao tri")) {
                    maintenanceComputers++;
                }
                if (c.getMaKhachHang() != null && !c.getMaKhachHang().trim().isEmpty()) {
                    currentCustomers.add(c.getMaKhachHang().trim());
                }
            }

            int invoicesToday = rrToday.invoices.size();
            int invoicesMonth = rrMonth.invoices.size();
            int paidInvoices = 0;
            for (InvoiceSummary inv : recentInvoices) {
                String st = inv.getTrangThai() == null ? "" : inv.getTrangThai().toLowerCase();
                if (st.contains("đã thanh toán") || st.contains("da thanh toan")) {
                    paidInvoices++;
                }
            }
            int paymentRate = recentInvoices.isEmpty() ? 0 : (int) Math.round((paidInvoices * 100.0) / recentInvoices.size());
            int machineUtilization = totalComputers == 0 ? 0 : (int) Math.round((busyComputers * 100.0) / totalComputers);
            int maintenanceRate = totalComputers == 0 ? 0 : (int) Math.round((maintenanceComputers * 100.0) / totalComputers);
            int repeatRate = invoicesToday == 0 ? 0 : Math.min(100, (int) Math.round((currentCustomers.size() * 100.0) / invoicesToday));

            DashStats stats = new DashStats();
            stats.soMayDangDung = busyComputers;
            stats.tongSoMay = totalComputers;
            stats.soKhachHienTai = currentCustomers.size();
            stats.tongKhachHomNay = currentCustomers.size();
            stats.soHoaDonHomNay = invoicesToday;
            stats.soHoaDonThang = invoicesMonth;
            stats.doanhThuHomNay = rrToday.total;
            stats.doanhThuThang = rrMonth.total;
            stats.congSuatMay = machineUtilization;
            stats.tyLeQuayLai = repeatRate;
            stats.tyLeThanhToan = paymentRate;
            stats.tyLeBaoTri = maintenanceRate;

            req.setAttribute("dashStats", stats);
            req.setAttribute("danhSachMay", computers);
            req.setAttribute("recentInvoices", recentInvoices);
        } catch (Exception e) {
            req.setAttribute("error", "Không tải được dữ liệu dashboard: " + e.getMessage());
        }
        forward(req, resp, "/WEB-INF/jsp/staff/dashboard.jsp");
    }

    public static class DashStats {
        public int soMayDangDung;
        public int tongSoMay;
        public int soKhachHienTai;
        public int tongKhachHomNay;
        public int soHoaDonHomNay;
        public int soHoaDonThang;
        public double doanhThuHomNay;
        public double doanhThuThang;
        public int congSuatMay;
        public int tyLeQuayLai;
        public int tyLeThanhToan;
        public int tyLeBaoTri;

        public int getSoMayDangDung() { return soMayDangDung; }
        public int getTongSoMay() { return tongSoMay; }
        public int getSoKhachHienTai() { return soKhachHienTai; }
        public int getTongKhachHomNay() { return tongKhachHomNay; }
        public int getSoHoaDonHomNay() { return soHoaDonHomNay; }
        public int getSoHoaDonThang() { return soHoaDonThang; }
        public double getDoanhThuHomNay() { return doanhThuHomNay; }
        public double getDoanhThuThang() { return doanhThuThang; }
        public int getCongSuatMay() { return congSuatMay; }
        public int getTyLeQuayLai() { return tyLeQuayLai; }
        public int getTyLeThanhToan() { return tyLeThanhToan; }
        public int getTyLeBaoTri() { return tyLeBaoTri; }
    }
}

