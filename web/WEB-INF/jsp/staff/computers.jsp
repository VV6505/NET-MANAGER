<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Máy tính — NET 269</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?v=20260413a">
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    <div class="grid">
        <section class="card">
            <div class="hd">
                <h2>🖥️ Danh sách máy tính</h2>
                <span class="chip">Tổng: <c:out value="${computers.size()}"/> máy</span>
            </div>
            <div class="bd" style="padding:0; overflow-x:auto;">
                <table>
                    <thead>
                        <tr>
                            <th>Mã máy</th>
                            <th>Tên máy</th>
                            <th>Khu</th>
                            <th>Trạng thái</th>
                            <th>Khách đang dùng</th>
                            <th>Đổi trạng thái</th>
                            <th>Bắt đầu / Kết thúc</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cpt" items="${computers}">
                            <tr>
                                <td><b>${cpt.maMay}</b></td>
                                <td>${cpt.tenMay}</td>
                                <td><span class="badge badge-gray">${cpt.tenKhu}</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${cpt.trangThai == 'Còn trống'}">
                                            <span class="status-badge status-trong">Còn trống</span>
                                        </c:when>
                                        <c:when test="${cpt.trangThai == 'Đang thuê'}">
                                            <span class="status-badge status-thue">Đang thuê</span>
                                        </c:when>
                                        <c:when test="${cpt.trangThai == 'Đã đặt trước'}">
                                            <span class="status-badge status-dat">Đã đặt trước</span>
                                        </c:when>
                                        <c:when test="${cpt.trangThai == 'Đang hư'}">
                                            <span class="status-badge status-hu">Đang hư</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge" style="background:#f1f4fa;color:var(--text-muted);border-color:var(--border);">${cpt.trangThai}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty cpt.tenKhachHang}">
                                            ${cpt.tenKhachHang}
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color:var(--text-muted);">—</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/staff/computers/status"
                                          class="form-row">
                                        <input type="hidden" name="maMay" value="${cpt.maMay}">
                                        <select name="trangThai">
                                            <option value="Còn trống"    ${cpt.trangThai == 'Còn trống'    ? 'selected' : ''}>Còn trống</option>
                                            <option value="Đang thuê"    ${cpt.trangThai == 'Đang thuê'    ? 'selected' : ''}>Đang thuê</option>
                                            <option value="Đã đặt trước" ${cpt.trangThai == 'Đã đặt trước' ? 'selected' : ''}>Đã đặt trước</option>
                                            <option value="Đang hư"      ${cpt.trangThai == 'Đang hư'      ? 'selected' : ''}>Đang hư</option>
                                        </select>
                                        <button class="btn ghost" type="submit">Lưu</button>
                                    </form>
                                </td>
                                <td class="actions-cell">
                                    <div class="action-group">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/staff/computers/start-rental"
                                              class="form-row">
                                            <input type="hidden" name="maMay" value="${cpt.maMay}">
                                            <select name="maKhachHang" style="min-width:180px;" required>
                                                <option value="">Chọn khách hàng…</option>
                                                <c:forEach var="kh" items="${rentCustomers}">
                                                    <option value="${kh.maKhachHang}">
                                                        ${kh.tenKhachHang} (${kh.sdt})
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <button class="btn" type="submit">Bắt đầu</button>
                                        </form>
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/staff/computers/end-rental"
                                              class="form-row">
                                            <input type="hidden" name="maMay" value="${cpt.maMay}">
                                            <button class="btn ghost" type="submit">Kết thúc thuê</button>
                                        </form>
                                    </div>
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

