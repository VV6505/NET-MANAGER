<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Đăng Ký</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dangky.css">
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
  <div class="register-container">
    <h2>Đăng Ký</h2>
    <p>Chọn loại tài khoản để đăng ký</p>

    <div class="auth-tabs" role="tablist" aria-label="Chọn loại đăng ký">
      <button type="button" class="auth-tab <%= !isAdmin ? "is-active" : "" %>" data-tab="customer">Khách hàng</button>
      <button type="button" class="auth-tab <%= isAdmin ? "is-active" : "" %>" data-tab="admin">Admin</button>
    </div>

    <div class="auth-panels" data-default-tab="<%= isAdmin ? "admin" : "customer" %>">
      <!-- Customer register -->
      <div class="auth-panel <%= isAdmin ? "is-hidden" : "" %>" data-panel="customer">
        <% if (request.getAttribute("error") != null) { %>
          <p class="form-error"><%= request.getAttribute("error") %></p>
        <% } %>
        <form class="register-form" action="${pageContext.request.contextPath}/register" method="post">
          <div class="form-group">
            <div class="label-container">
              <i class="fa fa-user"></i>
              <label for="customerName">Họ và tên</label>
            </div>
            <div class="input-container">
              <input type="text" id="customerName" name="customerName" placeholder="Nguyễn Văn A" required>
            </div>
          </div>
          <div class="form-group">
            <div class="label-container">
              <i class="fa fa-envelope"></i>
              <label for="email">Email</label>
            </div>
            <div class="input-container">
              <input type="email" id="email" name="email" placeholder="email@example.com" required>
            </div>
          </div>
          <div class="form-group">
            <div class="label-container">
              <i class="fa fa-phone"></i>
              <label for="username">Số điện thoại</label>
            </div>
            <div class="input-container">
              <input type="text" id="username" name="username" placeholder="0123456789" pattern="[0-9]{10}" title="Số điện thoại phải có đúng 10 chữ số" required>
            </div>
          </div>
          <div class="auth-hint">
            Sau khi đăng ký, bạn cần chờ admin xác nhận. Tên đăng nhập là <b>SĐT</b>, mật khẩu mặc định là <b>tên khách hàng đã chuẩn hóa</b> (không dấu, không khoảng trắng, chữ thường).
          </div>
          <button class="btn" type="submit">Đăng Ký</button>
        </form>
        <p>Đã có tài khoản? <a href="${pageContext.request.contextPath}/login?tab=customer">Đăng nhập</a></p>
      </div>

      <!-- Admin register (info) -->
      <div class="auth-panel <%= isAdmin ? "" : "is-hidden" %>" data-panel="admin">
        <div class="auth-note">
          Tài khoản <b>Admin/Nhân viên</b> được cấp bởi hệ thống (quản lý quán).
          Nếu bạn đã có mã nhân viên, hãy chuyển sang đăng nhập.
        </div>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/login?tab=admin">
          Đi tới đăng nhập Nhân viên
        </a>
        <p style="margin-top:14px;">Bạn muốn tạo nhân viên mới? Hãy dùng chức năng quản trị ở trang Admin.</p>
      </div>
    </div>
  </div>

  <script src="${pageContext.request.contextPath}/assets/javascript/eye-toggle.js"></script>
  <script>
    (function () {
      const container = document.querySelector('.register-container');
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

