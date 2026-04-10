<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Khách hàng</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    <div class="grid">
        <section class="card">
            <div class="hd">
                <h2>Danh sách khách hàng</h2>
                <span class="chip">Tổng: <c:out value="${customers.size()}"/></span>
            </div>
            <div class="bd">
                <table>
                    <thead>
                        <tr>
                            <th>Mã KH</th>
                            <th>Tên</th>
                            <th>SĐT</th>
                            <th>Email</th>
                            <th>Loại</th>
                            <th>Số dư</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="kh" items="${customers}">
                            <tr>
                                <td><b>${kh.maKhachHang}</b></td>
                                <td>${kh.tenKhachHang}</td>
                                <td>${kh.sdt}</td>
                                <td>${kh.email}</td>
                                <td>${kh.tenLoaiKhachHang}</td>
                                <td>${kh.soDu}</td>
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

