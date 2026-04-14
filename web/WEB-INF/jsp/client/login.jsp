<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Đăng Nhập</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dangnhap.css">
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.1.1/css/all.css" crossorigin="anonymous">
</head>
<body>
  <%
    String tab = request.getParameter("tab");
    Object activeTabAttr = request.getAttribute("activeTab");
    if (activeTabAttr != null) tab = String.valueOf(activeTabAttr);
    if (tab == null || tab.trim().isEmpty()) tab = "customer";
    boolean isAdmin = "admin".equalsIgnoreCase(tab);
  %>
  <div class="login-container">
    <h2>Đăng Nhập</h2>
    <p>Chọn loại tài khoản để đăng nhập</p>

    <div class="auth-tabs" role="tablist" aria-label="Chọn loại đăng nhập">
      <button type="button" class="auth-tab <%= !isAdmin ? "is-active" : "" %>" data-tab="customer">Khách hàng</button>
      <button type="button" class="auth-tab <%= isAdmin ? "is-active" : "" %>" data-tab="admin">Nhân viên</button>
    </div>

    <div class="auth-panels" data-default-tab="<%= isAdmin ? "admin" : "customer" %>">
      <!-- Customer login -->
      <div class="auth-panel" data-panel="customer" style="<%= isAdmin ? "display:none;" : "" %>">
        <% if (request.getAttribute("error") != null) { %>
          <p class="form-error"><%= request.getAttribute("error") %></p>
        <% } %>
        <form class="login-form" action="${pageContext.request.contextPath}/login" method="post">
          <div class="auth-hint">
            Đăng nhập bằng SĐT và mật khẩu mặc định: tên khách hàng đã chuẩn hóa (không dấu, không khoảng trắng, chữ thường), sau khi tài khoản đã được admin xác nhận.
          </div>
          <input type="hidden" name="from" value="<%= request.getParameter("from") != null ? request.getParameter("from") : "" %>">
          <div class="form-group">
            <div class="label-container">
              <i class="fa fa-user"></i>
              <label for="username">Số điện thoại</label>
            </div>
            <div class="input-container">
              <input type="text" id="username" name="username" placeholder="0123456789" required>
            </div>
          </div>
          <div class="form-group">
            <div class="label-container">
                <i class="fa fa-key"></i>
                <label for="password">Mật khẩu</label>
            </div>
            <div class="input-container">
              <input type="password" class="password-field" name="password" placeholder="Password" required>
              <i class="fa fa-eye toggle-password"></i>
            </div>
          </div>
          <button class="btn" type="submit">Đăng Nhập</button>
        </form>
        <p>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register?tab=customer">Đăng ký</a></p>
      </div>

      <!-- Admin login -->
      <div class="auth-panel" data-panel="admin" style="<%= isAdmin ? "" : "display:none;" %>">
        <% if (request.getAttribute("adminError") != null) { %>
          <p class="form-error"><%= request.getAttribute("adminError") %></p>
        <% } %>
        <form class="login-form" action="${pageContext.request.contextPath}/staff/login" method="post">
          <div class="form-group">
            <div class="label-container">
              <i class="fa fa-user-shield"></i>
              <label for="maNhanVien">Mã nhân viên</label>
            </div>
            <div class="input-container">
              <input type="text" id="maNhanVien" name="maNhanVien" placeholder="NV0001" required>
            </div>
          </div>
          <div class="form-group">
            <div class="label-container">
              <i class="fa fa-key"></i>
              <label for="passwordAdmin">Mật khẩu</label>
            </div>
            <div class="input-container">
              <input type="password" id="passwordAdmin" class="password-field" name="password" placeholder="Password" required>
              <i class="fa fa-eye toggle-password"></i>
            </div>
          </div>
          <button class="btn" type="submit">Đăng Nhập</button>
        </form>
        <p>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register?tab=admin">Đăng ký</a></p>
      </div>
    </div>
  </div>

  <script src="${pageContext.request.contextPath}/assets/javascript/eye-toggle.js"></script>
  <script>
    (function () {
      const container = document.querySelector('.login-container');
      if (!container) return;
      const tabs = Array.from(container.querySelectorAll('.auth-tab'));
      const panels = Array.from(container.querySelectorAll('.auth-panel'));
      function setTab(tab) {
        tabs.forEach(b => b.classList.toggle('is-active', b.dataset.tab === tab));
        panels.forEach(p => p.style.display = (p.dataset.panel === tab ? '' : 'none'));
        const url = new URL(window.location.href);
        url.searchParams.set('tab', tab);
        window.history.replaceState({}, '', url.toString());
      }
      tabs.forEach(b => b.addEventListener('click', () => setTab(b.dataset.tab)));
      const defaultTab = (document.querySelector('.auth-panels')?.dataset.defaultTab) || 'customer';
      setTab(defaultTab);
    })();
  </script>
</body>
</html>

