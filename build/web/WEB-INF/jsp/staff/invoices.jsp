<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hóa đơn</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?v=20260413a">
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    <div class="grid">
        <section class="card">
            <div class="hd">
                <h2>Tra cứu hóa đơn</h2>
                <span class="chip">Kết quả: <c:out value="${invoices.size()}"/></span>
            </div>
            <div class="bd">
                <form class="form-row" method="get" action="${pageContext.request.contextPath}/staff/invoices">
                    <div>
                        <div class="label">Tên khách</div>
                        <input name="customerName" value="${customerName}" placeholder="Nguyễn Văn A"/>
                    </div>
                    <div>
                        <div class="label">Từ ngày (yyyy-MM-dd)</div>
                        <input type="date" name="from" value="${from}"/>
                    </div>
                    <div>
                        <div class="label">Đến ngày (yyyy-MM-dd)</div>
                        <input type="date" name="to" value="${to}"/>
                    </div>
                    <button class="btn" type="submit">Lọc</button>
                    <a class="btn ghost" href="${pageContext.request.contextPath}/staff/invoices">Reset</a>
                </form>

                <div style="height:12px"></div>

                <table>
                    <thead>
                        <tr>
                            <th>Mã HD</th>
                            <th>Ngày</th>
                            <th>Trạng thái</th>
                            <th>Khách hàng</th>
                            <th>Số giờ chơi</th>
                            <th>Tổng tiền</th>
                            <th>Bill</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="hd" items="${invoices}">
                            <tr>
                                <td><b>${hd.maHoaDon}</b></td>
                                <td>${hd.ngay}</td>
                                <td>${hd.trangThai}</td>
                                <td>${hd.tenKhachHang}</td>
                                <td>${hd.soGioChoi}</td>
                                <td>${hd.tongTien}</td>
                                <td>
                                    <a class="btn ghost" href="${pageContext.request.contextPath}/staff/invoices/export?maHoaDon=${hd.maHoaDon}">
                                        Xuất bill
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
    <jsp:include page="includes/footer.jsp" />
</body>
</html>

