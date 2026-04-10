<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý máy</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    <div class="grid">
        <section class="card">
            <div class="hd">
                <h2>Danh sách máy (MayTinh)</h2>
                <span class="chip">Tổng: <c:out value="${computers.size()}"/></span>
            </div>
            <div class="bd">
                <table>
                    <thead>
                        <tr>
                            <th>Mã máy</th>
                            <th>Tên máy</th>
                            <th>Khu</th>
                            <th>Trạng thái</th>
                            <th>Khách</th>
                            <th>Đổi trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cpt" items="${computers}">
                            <tr>
                                <td><b>${cpt.maMay}</b></td>
                                <td>${cpt.tenMay}</td>
                                <td>${cpt.tenKhu}</td>
                                <td>${cpt.trangThai}</td>
                                <td>${cpt.tenKhachHang}</td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}/staff/computers/status" class="form-row">
                                        <input type="hidden" name="maMay" value="${cpt.maMay}">
                                        <select name="trangThai">
                                            <option value="Còn trống" ${cpt.trangThai == 'Còn trống' ? 'selected' : ''}>Còn trống</option>
                                            <option value="Đang thuê" ${cpt.trangThai == 'Đang thuê' ? 'selected' : ''}>Đang thuê</option>
                                            <option value="Đã đặt trước" ${cpt.trangThai == 'Đã đặt trước' ? 'selected' : ''}>Đã đặt trước</option>
                                            <option value="Đang hư" ${cpt.trangThai == 'Đang hư' ? 'selected' : ''}>Đang hư</option>
                                        </select>
                                        <button class="btn ghost" type="submit">Cập nhật</button>
                                    </form>
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

