<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thông tin cá nhân - NET 269</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/profile.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    
    <div class="profile-container">
        <div class="profile-header">
            <h1>Thông tin cá nhân</h1>
        </div>
        
        <div class="profile-info">
            <div class="info-group">
                <span class="info-label">Số điện thoại:</span>
                <span class="info-value">${customer.sdt}</span>
            </div>
            
            <div class="info-group">
                <span class="info-label">Tên khách hàng:</span>
                <span class="info-value">${customer.tenKhachHang}</span>
            </div>
            
            <div class="info-group">
                <span class="info-label">Email:</span>
                <span class="info-value">${customer.email}</span>
            </div>
            
            <div class="info-group">
                <span class="info-label">Loại khách hàng:</span>
                <span class="info-value">${customer.tenLoaiKhachHang}</span>
            </div>
            
            <div class="info-group">
                <span class="info-label">Số giờ chơi:</span>
                <span class="info-value">${customer.soGioChoi} giờ</span>
            </div>
            
            <div class="info-group">
                <span class="info-label">Số dư tài khoản:</span>
                <span class="info-value">${customer.soDu} VNĐ</span>
            </div>
        </div>
        
        <button class="edit-button" onclick="location.href='${pageContext.request.contextPath}/profile/edit'">
            <i class="fas fa-edit"></i> Chỉnh sửa thông tin
        </button>
    </div>
    
    <jsp:include page="includes/footer.jsp" />
</body>
</html> 