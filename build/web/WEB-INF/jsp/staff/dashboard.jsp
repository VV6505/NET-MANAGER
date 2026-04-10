<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    <div class="grid">
        <section class="card third">
            <div class="hd"><h2>Khu vực</h2></div>
            <div class="bd">
                Xem danh sách khu vực, mô tả và cấu hình theo từng khu.
                <div style="height:12px"></div>
                <a class="btn ghost" href="${pageContext.request.contextPath}/staff/rooms">Mở trang</a>
            </div>
        </section>

        <section class="card third">
            <div class="hd"><h2>Máy tính</h2></div>
            <div class="bd">
                Theo dõi trạng thái máy và cập nhật nhanh (Còn trống/Đang thuê/Bảo trì).
                <div style="height:12px"></div>
                <a class="btn ghost" href="${pageContext.request.contextPath}/staff/computers">Mở trang</a>
            </div>
        </section>

        <section class="card third">
            <div class="hd"><h2>Khách hàng</h2></div>
            <div class="bd">
                Danh sách khách hàng, thông tin liên hệ, loại KH, số dư.
                <div style="height:12px"></div>
                <a class="btn ghost" href="${pageContext.request.contextPath}/staff/customers">Mở trang</a>
            </div>
        </section>

        <section class="card">
            <div class="hd"><h2>Hóa đơn</h2></div>
            <div class="bd">
                Lọc theo tên khách và khoảng ngày để tra cứu nhanh.
                <div style="height:12px"></div>
                <a class="btn" href="${pageContext.request.contextPath}/staff/invoices">Tra cứu hóa đơn</a>
            </div>
        </section>
    </div>
    <jsp:include page="includes/footer.jsp" />
</body>
</html>

