<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Nhân viên</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?v=20260413a">
</head>
<body>
<jsp:include page="includes/header.jsp" />

<div class="grid">
    <section class="card">
        <div class="hd">
            <h2>Quản lý nhân viên</h2>
            <c:set var="roleLower" value="${fn:toLowerCase(sessionScope.staffRole)}" />
            <c:set var="canAddEmployee" value="${fn:contains(roleLower, 'quản lý') or fn:contains(roleLower, 'admin')}" />
            <div style="display:flex;gap:10px;align-items:center;">
                <span class="chip">Tổng: <c:out value="${employees.size()}"/></span>
                <c:if test="${canAddEmployee}">
                    <a class="btn" href="${pageContext.request.contextPath}/staff/employees/new">Thêm nhân viên</a>
                </c:if>
            </div>
        </div>
        <div class="bd">
            <table>
                <thead>
                <tr>
                    <th>Mã NV</th>
                    <th>Họ tên</th>
                    <th>Loại</th>
                    <th>SĐT</th>
                    <th>Email</th>
                    <th>Phòng ban</th>
                    <th>Ngày bắt đầu</th>
                    <th>Trạng thái</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="e" items="${employees}">
                    <tr>
                        <td><b>${e.maNhanVien}</b></td>
                        <td>${e.tenNhanVien}</td>
                        <td>${e.tenLoaiNhanVien}</td>
                        <td>${e.sdt}</td>
                        <td>${e.email}</td>
                        <td>${e.phongBan}</td>
                        <td>${e.ngayBatDau}</td>
                        <td>${e.trangThai}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty employees}">
                    <tr><td colspan="8" style="padding:16px;opacity:.8;">Chưa có nhân viên.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </section>
</div>

<jsp:include page="includes/footer.jsp" />
</body>
</html>

