<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HisUse - NET 269</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/profile.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/toastify-js/src/toastify.min.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="history-page">
        <div class="history-wrap">
            <div class="history-head">
                <div>
                    <h2 class="history-title">Lịch sử sử dụng</h2>
                    <p class="history-subtitle">Theo dõi phiên sử dụng máy và hóa đơn của bạn.</p>
                </div>
            <div style="display:flex;gap:10px;align-items:center;flex-wrap:wrap;justify-content:flex-end;">
                <a class="history-page-btn" href="${pageContext.request.contextPath}/history/export">Xuất CSV (đã thanh toán)</a>
            </div>
            </div>

            <c:if test="${not empty requestScope.error}">
                <div class="history-alert history-alert-error">${requestScope.error}</div>
            </c:if>

            <c:if test="${not empty latestMaHoaDon}">
                <div class="history-card">
                    <div class="history-card-head">
                        <h3 class="history-card-title">
                            Chi tiết hóa đơn mới nhất
                            <span class="history-badge history-badge-neutral">${latestMaHoaDon}</span>
                        </h3>
                    </div>

                    <c:if test="${not empty latestFoods}">
                        <h4 class="history-section-title">Đồ ăn</h4>
                        <div class="history-table-wrap">
                            <table class="history-table">
                                <thead>
                                <tr>
                                    <th>Tên món</th>
                                    <th class="col-num">Số lượng</th>
                                    <th class="col-money">Đơn giá</th>
                                    <th class="col-money">Thành tiền</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="it" items="${latestFoods}">
                                    <tr>
                                        <td>${it.ten}</td>
                                        <td class="col-num">${it.soLuong}</td>
                                        <td class="col-money"><fmt:formatNumber value="${it.donGia}" type="number"/>đ</td>
                                        <td class="col-money"><fmt:formatNumber value="${it.thanhTien}" type="number"/>đ</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>

                    <c:if test="${not empty latestGames}">
                        <h4 class="history-section-title">Game</h4>
                        <div class="history-table-wrap">
                            <table class="history-table">
                                <thead>
                                <tr>
                                    <th>Tên game</th>
                                    <th class="col-num">Số lượng</th>
                                    <th class="col-money">Đơn giá</th>
                                    <th class="col-money">Thành tiền</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="it" items="${latestGames}">
                                    <tr>
                                        <td>${it.ten}</td>
                                        <td class="col-num">${it.soLuong}</td>
                                        <td class="col-money"><fmt:formatNumber value="${it.donGia}" type="number"/>đ</td>
                                        <td class="col-money"><fmt:formatNumber value="${it.thanhTien}" type="number"/>đ</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                </div>
            </c:if>

            <div class="history-card">
                <div class="history-card-head">
                    <h3 class="history-card-title">Danh sách phiên sử dụng</h3>
                </div>
                <div class="history-table-wrap">
                    <table class="history-table history-table-usage">
                        <thead>
                        <tr>
                            <th>Mã sử dụng</th>
                            <th>Máy</th>
                            <th>Khu</th>
                            <th>Loại máy</th>
                            <th>Vào</th>
                            <th>Ra</th>
                            <th>Hóa đơn</th>
                            <th>Trạng thái</th>
                            <th class="col-money">Tổng tiền</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="h" items="${history}">
                            <tr>
                                <td data-label="Mã sử dụng"><span class="mono">${h.maSuDung}</span></td>
                                <td data-label="Máy">${h.maMay}</td>
                                <td data-label="Khu">${h.tenKhu}</td>
                                <td data-label="Loại máy">${h.tenLoaiMay}</td>
                                <td data-label="Vào"><fmt:formatDate value="${h.thoiGianVao}" pattern="yyyy-MM-dd HH:mm"/></td>
                                <td data-label="Ra"><fmt:formatDate value="${h.thoiGianRa}" pattern="yyyy-MM-dd HH:mm"/></td>
                                <td data-label="Hóa đơn">
                                    <c:set var="st" value="${fn:toLowerCase(h.trangThai)}" />
                                    <c:choose>
                                        <c:when test="${not empty h.maHoaDon and fn:contains(st,'thanh') and fn:contains(st,'toan')}">
                                            <a class="mono" href="${pageContext.request.contextPath}/invoice?maHoaDon=${h.maHoaDon}">
                                                ${h.maHoaDon}
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="mono">${h.maHoaDon}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td data-label="Trạng thái">
                                    <c:choose>
                                        <c:when test="${fn:contains(st,'thanh') and fn:contains(st,'toan')}">
                                            <span class="history-badge history-badge-ok">${h.trangThai}</span>
                                        </c:when>
                                        <c:when test="${fn:contains(st,'chua') or fn:contains(st,'tạm') or fn:contains(st,'tam')}">
                                            <span class="history-badge history-badge-warn">${h.trangThai}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="history-badge history-badge-neutral">${h.trangThai}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td data-label="Tổng tiền" class="col-money"><fmt:formatNumber value="${h.tongTien}" type="number"/>đ</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty history}">
                            <tr><td colspan="9" class="history-empty">Chưa có lịch sử.</td></tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>

                <div class="history-pager">
                    <c:set var="p" value="${page}" />
                    <c:set var="s" value="${size}" />
                    <a class="history-page-btn ${p<=1?'is-disabled':''}"
                       href="${pageContext.request.contextPath}/history?page=${p-1}&size=${s}">Trang trước</a>
                    <div class="history-page-info">Trang <b>${p}</b></div>
                    <a class="history-page-btn"
                       href="${pageContext.request.contextPath}/history?page=${p+1}&size=${s}">Trang sau</a>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>

