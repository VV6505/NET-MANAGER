<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý khu vực</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?v=20260413a">
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    <div class="grid">
        <section class="card">
            <div class="hd">
                <h2>Danh sách khu vực (KhuVuc)</h2>
                <span class="chip">Tổng: <c:out value="${rooms.size()}"/></span>
            </div>
            <div class="bd">
                <table>
                    <thead>
                        <tr>
                            <th>Mã khu</th>
                            <th>Tên khu</th>
                            <th>Mô tả</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="r" items="${rooms}">
                            <tr>
                                <td><b>${r.maKhu}</b></td>
                                <td>${r.tenKhu}</td>
                                <td>${r.moTa}</td>
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

