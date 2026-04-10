<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chỉnh sửa thông tin - NET 269</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/profile.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    
    <div class="profile-container">
        <div class="profile-header">
            <h1>Chỉnh sửa thông tin cá nhân</h1>
        </div>
        
        <form action="${pageContext.request.contextPath}/profile" method="POST" class="edit-form">
            <div class="form-group">
                <label for="sdt">Số điện thoại:</label>
                <input type="text" id="sdt" value="${customer.sdt}" readonly>
            </div>
            
            <div class="form-group">
                <label for="tenKhachHang">Tên khách hàng:</label>
                <input type="text" id="tenKhachHang" name="tenKhachHang" value="${customer.tenKhachHang}" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" value="${customer.email}" required>
            </div>
            
            <div class="form-group">
                <label for="matKhau">Mật khẩu mới (để trống nếu không đổi):</label>
                <input type="password" id="matKhau" name="matKhau">
            </div>
            
            <div class="form-group">
                <label for="xacNhanMatKhau">Xác nhận mật khẩu mới:</label>
                <input type="password" id="xacNhanMatKhau" name="xacNhanMatKhau">
            </div>
            
            <div class="button-group">
                <button type="submit" class="save-button">
                    <i class="fas fa-save"></i> Lưu thay đổi
                </button>
                <button type="button" class="cancel-button" onclick="location.href='${pageContext.request.contextPath}/profile'">
                    <i class="fas fa-times"></i> Hủy
                </button>
            </div>
        </form>
    </div>
    
    <jsp:include page="includes/footer.jsp" />
</body>
</html> 