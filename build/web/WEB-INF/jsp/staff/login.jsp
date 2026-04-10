<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Login</title>
    <style>
        body { font-family: Arial, sans-serif; background:#1a1a1a; color:#e0e0e0; padding:40px; }
        .card { max-width:420px; margin:0 auto; background:#2c2f33; border-radius:12px; padding:24px; }
        label { display:block; margin-top:12px; }
        input { width:100%; padding:10px; border-radius:8px; border:1px solid #444; background:#23272a; color:#fff; }
        button { margin-top:16px; width:100%; padding:12px; border-radius:8px; border:none; background:#ff4500; color:#000; font-weight:700; cursor:pointer; }
        .err { color:#ffb3b3; margin-top:10px; }
    </style>
</head>
<body>
    <div class="card">
        <h2>Đăng nhập nhân viên/admin</h2>
        <% if (request.getAttribute("error") != null) { %>
            <div class="err"><%= request.getAttribute("error") %></div>
        <% } %>
        <form method="post" action="${pageContext.request.contextPath}/staff/login">
            <label>Mã nhân viên</label>
            <input name="maNhanVien" placeholder="NV0001" required />
            <label>Mật khẩu</label>
            <input type="password" name="password" required />
            <button type="submit">Đăng nhập</button>
        </form>
    </div>
</body>
</html>

