<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm nhân viên</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?v=20260413a">
</head>
<body>
<jsp:include page="includes/header.jsp" />

<div class="grid">
    <section class="card">
        <div class="hd">
            <h2>Thêm nhân viên mới</h2>
            <a class="btn ghost" href="${pageContext.request.contextPath}/staff/employees">Quay lại</a>
        </div>
        <div class="bd">
            <c:if test="${not empty requestScope.error}">
                <div class="msg err">${requestScope.error}</div>
            </c:if>

            <form class="form-row" method="post" action="${pageContext.request.contextPath}/staff/employees/create">
                <div>
                    <div class="label">Mã nhân viên (gợi ý)</div>
                    <input name="maNhanVien" value="${suggestId}" placeholder="NV0006" />
                </div>
                <div>
                    <div class="label">Họ tên *</div>
                    <input name="tenNhanVien" placeholder="Nguyễn Văn X" required />
                </div>
                <div>
                    <div class="label">Giới tính</div>
                    <select name="gioiTinh">
                        <option value="Nam">Nam</option>
                        <option value="Nữ">Nữ</option>
                        <option value="Khác">Khác</option>
                    </select>
                </div>
                <div>
                    <div class="label">Ngày sinh</div>
                    <input name="ngaySinh" placeholder="1995-01-01" />
                </div>
                <div>
                    <div class="label">CMND/CCCD</div>
                    <input name="cmnd" placeholder="123456789012" />
                </div>
                <div>
                    <div class="label">Ngày cấp</div>
                    <input name="ngayCap" placeholder="2015-01-01" />
                </div>
                <div style="width:100%">
                    <div class="label">Nơi cấp</div>
                    <input name="noiCap" placeholder="Công an ..." style="width:100%" />
                </div>

                <div>
                    <div class="label">SĐT *</div>
                    <input name="sdt" placeholder="0123456789" required />
                </div>
                <div>
                    <div class="label">Email *</div>
                    <input name="email" placeholder="nv@example.com" required />
                </div>
                <div style="width:100%">
                    <div class="label">Địa chỉ</div>
                    <input name="diaChi" placeholder="123 Đường..." style="width:100%" />
                </div>
                <div>
                    <div class="label">Phòng ban</div>
                    <input name="phongBan" placeholder="Thu ngân" />
                </div>
                <div>
                    <div class="label">Ngày bắt đầu</div>
                    <input name="ngayBatDau" placeholder="2026-04-09" />
                </div>
                <div>
                    <div class="label">Lương cơ bản</div>
                    <input name="luongCoBan" placeholder="6000000" />
                </div>
                <div>
                    <div class="label">Loại nhân viên *</div>
                    <select name="maLoaiNhanVien" required>
                        <c:forEach var="t" items="${types}">
                            <option value="${t.maLoaiNhanVien}">${t.tenLoaiNhanVien}</option>
                        </c:forEach>
                    </select>
                </div>
                <div>
                    <div class="label">Trạng thái</div>
                    <select name="trangThai">
                        <option value="Đang làm việc">Đang làm việc</option>
                        <option value="Nghỉ phép">Nghỉ phép</option>
                        <option value="Đã nghỉ việc">Đã nghỉ việc</option>
                    </select>
                </div>
                <div>
                    <div class="label">Mật khẩu *</div>
                    <input type="password" name="matKhau" required />
                </div>

                <div style="width:100%;display:flex;gap:10px;justify-content:flex-end;align-items:center;margin-top:6px;">
                    <button class="btn" type="submit">Tạo nhân viên</button>
                    <a class="btn ghost" href="${pageContext.request.contextPath}/staff/employees">Hủy</a>
                </div>
            </form>
        </div>
    </section>
</div>

<jsp:include page="includes/footer.jsp" />
</body>
</html>

