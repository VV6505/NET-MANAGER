<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Khách hàng</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?v=20260413a">
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
                <c:if test="${not empty sessionScope.successMessage}">
                    <div style="padding:8px 12px;background:#e8f8f0;border:1px solid #b7e4c7;border-radius:8px;margin-bottom:10px;">
                        ${sessionScope.successMessage}
                    </div>
                    <c:remove var="successMessage" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.errorMessage}">
                    <div style="padding:8px 12px;background:#fdeaea;border:1px solid #f3bcbc;border-radius:8px;margin-bottom:10px;">
                        ${sessionScope.errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session"/>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/staff/customers" class="form-row" style="margin-bottom:12px;display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
                    <input type="hidden" name="action" value="create">
                    <input type="text" name="customerName" placeholder="Tên khách hàng" required>
                    <input type="text" name="sdt" placeholder="SĐT" required>
                    <input type="email" name="email" placeholder="Email">
                    <button type="submit" class="btn">Tạo tài khoản ngay</button>
                </form>

                <table>
                    <thead>
                        <tr>
                            <th>Mã KH</th>
                            <th>Tên</th>
                            <th>SĐT</th>
                            <th>Email</th>
                            <th>Loại</th>
                            <th>Số dư</th>
                            <th>Trạng thái TK</th>
                            <th>Hành động</th>
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
                                <td>${kh.trangThaiTaiKhoan}</td>
                                <td>
                                    <c:if test="${kh.trangThaiTaiKhoan == 'Chờ xác nhận'}">
                                        <form method="post" action="${pageContext.request.contextPath}/staff/customers">
                                            <input type="hidden" name="action" value="approve">
                                            <input type="hidden" name="sdt" value="${kh.sdt}">
                                            <button type="submit" class="btn ghost">Xác nhận</button>
                                        </form>
                                    </c:if>
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

