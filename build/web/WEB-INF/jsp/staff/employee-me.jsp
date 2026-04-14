<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thông tin cá nhân</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?v=20260413a">
</head>
<body>
<jsp:include page="includes/header.jsp" />

<div class="grid">
    <section class="card">
        <div class="hd">
            <h2>Thông tin tài khoản nhân viên</h2>
        </div>
        <div class="bd">
            <c:choose>
                <c:when test="${not empty employee}">
                    <table>
                        <tbody>
                            <tr><th style="width:220px;">Mã nhân viên</th><td><b>${employee.maNhanVien}</b></td></tr>
                            <tr><th>Họ tên</th><td>${employee.tenNhanVien}</td></tr>
                            <tr><th>Loại nhân viên</th><td>${employee.tenLoaiNhanVien}</td></tr>
                            <tr><th>Số điện thoại</th><td>${employee.sdt}</td></tr>
                            <tr><th>Email</th><td>${employee.email}</td></tr>
                            <tr><th>Phòng ban</th><td>${employee.phongBan}</td></tr>
                            <tr><th>Ngày bắt đầu</th><td>${employee.ngayBatDau}</td></tr>
                            <tr><th>Trạng thái</th><td>${employee.trangThai}</td></tr>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="msg err">Không tải được thông tin nhân viên.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</div>

<jsp:include page="includes/footer.jsp" />
</body>
</html>
