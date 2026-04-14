<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard — NET 269</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?v=20260413a">
    <style>
        .metric-card{padding:16px}
        .metric-card .num{font-size:30px;font-weight:900;line-height:1;color:#111827}
        .metric-card .lbl{font-size:12px;color:#6b7280;text-transform:uppercase;letter-spacing:.04em;margin-top:6px}
        .metric-card .sub{font-size:13px;color:#374151;margin-top:4px}
        .metric-top{display:flex;align-items:center;justify-content:space-between}
        .metric-ico{font-size:24px}
        .metric-c1{border-top:3px solid #10b981}
        .metric-c2{border-top:3px solid #3b82f6}
        .metric-c3{border-top:3px solid #f59e0b}
        .metric-c4{border-top:3px solid #ef4444}
        .machine-wrap{display:grid;grid-template-columns:repeat(auto-fill,minmax(70px,1fr));gap:10px}
        .machine-box{border:1px solid var(--line);border-radius:12px;padding:8px;text-align:center;background:#fff}
        .machine-id{font-weight:800;font-size:13px}
        .machine-st{font-size:11px;margin-top:4px}
        .st-free{color:#1f9d5c}
        .st-busy{color:#c66a0a}
        .st-maint{color:#365fc9}
        .quick-links{display:grid;grid-template-columns:1fr 1fr;gap:10px}
        .quick-link{display:flex;gap:10px;align-items:center;padding:12px;border:1px solid var(--line);border-radius:12px;text-decoration:none;background:#fff}
        .quick-link:hover{background:#f8fafc}
        .quick-link b{display:block}
        .quick-link span{font-size:12px;color:#6b7280}
        .bars{display:flex;flex-direction:column;gap:12px}
        .bar .head{display:flex;justify-content:space-between;font-size:13px;margin-bottom:6px}
        .bar .track{height:8px;background:#eef2f7;border-radius:999px;overflow:hidden}
        .bar .fill{height:100%;border-radius:999px}
        @media (max-width:980px){.quick-links{grid-template-columns:1fr}}
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    <div class="grid">

        <section class="card" style="grid-column:span 3">
            <div class="metric-card metric-c1">
                <div class="metric-top"><span class="metric-ico">🖥️</span></div>
                <div class="num">${empty dashStats.soMayDangDung ? '—' : dashStats.soMayDangDung}</div>
                <div class="lbl">Máy đang thuê</div>
                <div class="sub">Tổng máy: ${empty dashStats.tongSoMay ? '—' : dashStats.tongSoMay}</div>
            </div>
        </section>
        <section class="card" style="grid-column:span 3">
            <div class="metric-card metric-c2">
                <div class="metric-top"><span class="metric-ico">👤</span></div>
                <div class="num">${empty dashStats.soKhachHienTai ? '—' : dashStats.soKhachHienTai}</div>
                <div class="lbl">Khách đang chơi</div>
                <div class="sub">Hôm nay: ${empty dashStats.tongKhachHomNay ? '—' : dashStats.tongKhachHomNay} lượt</div>
            </div>
        </section>
        <section class="card" style="grid-column:span 3">
            <div class="metric-card metric-c3">
                <div class="metric-top"><span class="metric-ico">🧾</span></div>
                <div class="num">${empty dashStats.soHoaDonHomNay ? '—' : dashStats.soHoaDonHomNay}</div>
                <div class="lbl">Hóa đơn hôm nay</div>
                <div class="sub">Tháng này: ${empty dashStats.soHoaDonThang ? '—' : dashStats.soHoaDonThang}</div>
            </div>
        </section>
        <section class="card" style="grid-column:span 3">
            <div class="metric-card metric-c4">
                <div class="metric-top"><span class="metric-ico">💰</span></div>
                <div class="num">
                    <c:choose>
                        <c:when test="${not empty dashStats.doanhThuHomNay}">
                            <fmt:formatNumber value="${dashStats.doanhThuHomNay}" pattern="#,##0"/>đ
                        </c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </div>
                <div class="lbl">Doanh thu hôm nay</div>
                <div class="sub">
                    Tháng:
                    <c:choose>
                        <c:when test="${not empty dashStats.doanhThuThang}">
                            <b><fmt:formatNumber value="${dashStats.doanhThuThang}" pattern="#,##0"/>đ</b>
                        </c:when>
                        <c:otherwise><b>—</b></c:otherwise>
                    </c:choose>
                </div>
            </div>
        </section>

        <section class="card" style="grid-column:span 8">
            <div class="hd">
                <h2>🖥️ Sơ đồ máy</h2>
                <a class="btn ghost" href="${pageContext.request.contextPath}/staff/computers">Quản lý máy →</a>
            </div>
            <div class="bd">
                <div class="machine-wrap">
                    <c:choose>
                        <c:when test="${not empty danhSachMay}">
                            <c:forEach var="may" items="${danhSachMay}" end="11">
                                <c:set var="stRaw" value="${may.trangThai}" />
                                <c:set var="stLower" value="${stRaw != null ? stRaw.toLowerCase() : ''}" />
                                <c:set var="stCls" value="st-free" />
                                <c:set var="stTxt" value="${empty stRaw ? 'Còn trống' : stRaw}" />
                                <c:if test="${stLower.contains('đang thuê') or stLower.contains('dang thue')}">
                                    <c:set var="stCls" value="st-busy" />
                                    <c:set var="stTxt" value="Đang thuê" />
                                </c:if>
                                <c:if test="${stLower.contains('bảo trì') or stLower.contains('bao tri')}">
                                    <c:set var="stCls" value="st-maint" />
                                    <c:set var="stTxt" value="Bảo trì" />
                                </c:if>
                                <div class="machine-box">
                                    <div class="machine-id">${may.maMay}</div>
                                    <div class="machine-st ${stCls}">${stTxt}</div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="i" begin="1" end="12">
                                <c:set var="r" value="${i % 4}" />
                                <div class="machine-box">
                                    <div class="machine-id">M${i < 10 ? '0' : ''}${i}</div>
                                    <div class="machine-st ${r == 0 ? 'st-maint' : r <= 2 ? 'st-busy' : 'st-free'}">
                                        ${r == 0 ? 'Bảo trì' : r <= 2 ? 'Đang thuê' : 'Còn trống'}
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div style="margin-top:10px;display:flex;gap:18px;font-size:13px;color:#4b5563">
                    <span><b style="color:#1f9d5c">●</b> Còn trống</span>
                    <span><b style="color:#c66a0a">●</b> Đang thuê</span>
                    <span><b style="color:#365fc9">●</b> Bảo trì</span>
                </div>
            </div>
        </section>

        <section class="card" style="grid-column:span 4">
            <div class="hd">
                <h2>⚡ Truy cập nhanh</h2>
            </div>
            <div class="bd">
                <div class="quick-links">
                    <a class="quick-link" href="${pageContext.request.contextPath}/staff/rooms"><span>🗺️</span><div><b>Khu vực</b><span>Cấu hình theo khu</span></div></a>
                    <a class="quick-link" href="${pageContext.request.contextPath}/staff/computers"><span>🖥️</span><div><b>Máy tính</b><span>Cập nhật trạng thái máy</span></div></a>
                    <a class="quick-link" href="${pageContext.request.contextPath}/staff/customers"><span>👤</span><div><b>Khách hàng</b><span>Danh sách & liên hệ</span></div></a>
                    <a class="quick-link" href="${pageContext.request.contextPath}/staff/invoices"><span>🧾</span><div><b>Hóa đơn</b><span>Tra cứu & xuất bill</span></div></a>
                </div>
            </div>
        </section>

        <section class="card" style="grid-column:span 8">
            <div class="hd">
                <h2>🧾 Hóa đơn gần đây</h2>
                <a class="btn ghost" href="${pageContext.request.contextPath}/staff/invoices">Xem tất cả →</a>
            </div>
            <div class="bd">
                <table>
                    <thead>
                        <tr>
                            <th>Khách</th>
                            <th>Mã HĐ</th>
                            <th>Số giờ</th>
                            <th>Trạng thái</th>
                            <th style="text-align:right">Tổng tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty recentInvoices}">
                                <c:forEach var="hd" items="${recentInvoices}" end="7">
                                    <tr>
                                        <td><b>${empty hd.tenKhachHang ? hd.maKhachHang : hd.tenKhachHang}</b></td>
                                        <td>${hd.maHoaDon}</td>
                                        <td>${hd.soGioChoi}h</td>
                                        <td><span class="badge-gray">${hd.trangThai}</span></td>
                                        <td style="text-align:right"><b><fmt:formatNumber value="${hd.tongTien}" pattern="#,##0"/>đ</b></td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr><td><b>Nguyễn Văn A</b></td><td>HD0001</td><td>3.5h</td><td><span class="badge-gray">Đã thanh toán</span></td><td style="text-align:right"><b>95,000đ</b></td></tr>
                                <tr><td><b>Trần Thị B</b></td><td>HD0002</td><td>2h</td><td><span class="badge-gray">Tạm</span></td><td style="text-align:right"><b>40,000đ</b></td></tr>
                                <tr><td><b>Lê Hoàng C</b></td><td>HD0003</td><td>5h</td><td><span class="badge-gray">Đã thanh toán</span></td><td style="text-align:right"><b>130,000đ</b></td></tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </section>

        <section class="card" style="grid-column:span 4">
            <div class="hd">
                <h2>📊 Thống kê sử dụng</h2>
            </div>
            <div class="bd">
                <div class="bars">
                    <div class="bar">
                        <div class="head"><span>Công suất máy</span><b>${empty dashStats.congSuatMay ? 65 : dashStats.congSuatMay}%</b></div>
                        <div class="track"><div class="fill" style="width:${empty dashStats.congSuatMay ? 65 : dashStats.congSuatMay}%;background:#10b981"></div></div>
                    </div>
                    <div class="bar">
                        <div class="head"><span>Tỷ lệ khách quay lại</span><b>${empty dashStats.tyLeQuayLai ? 48 : dashStats.tyLeQuayLai}%</b></div>
                        <div class="track"><div class="fill" style="width:${empty dashStats.tyLeQuayLai ? 48 : dashStats.tyLeQuayLai}%;background:#3b82f6"></div></div>
                    </div>
                    <div class="bar">
                        <div class="head"><span>Tỷ lệ thanh toán</span><b>${empty dashStats.tyLeThanhToan ? 82 : dashStats.tyLeThanhToan}%</b></div>
                        <div class="track"><div class="fill" style="width:${empty dashStats.tyLeThanhToan ? 82 : dashStats.tyLeThanhToan}%;background:#f59e0b"></div></div>
                    </div>
                    <div class="bar">
                        <div class="head"><span>Tỷ lệ bảo trì</span><b>${empty dashStats.tyLeBaoTri ? 10 : dashStats.tyLeBaoTri}%</b></div>
                        <div class="track"><div class="fill" style="width:${empty dashStats.tyLeBaoTri ? 10 : dashStats.tyLeBaoTri}%;background:#ef4444"></div></div>
                    </div>
                </div>
            </div>
        </section>
    </div>
    <jsp:include page="includes/footer.jsp" />
</body>
</html>

