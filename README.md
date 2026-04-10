# QLQUANNET (Servlet + JSP, tách KH/ADMIN)

Project `QLQUANNET/` đã được sắp xếp lại đúng kiểu **NetBeans Web (Ant)**:

- **Code/logic Java**: `src/java/`
- **Giao diện JSP + static assets**: `web/`

Trong `web/` đã tách rõ 2 giao diện:

- **Khách hàng**: `web/WEB-INF/jsp/client/`
- **Nhân viên/admin**: `web/WEB-INF/jsp/staff/`

## Thư viện cần thiết

Do JSP đang dùng JSTL (`c:forEach`, `c:if`, ...), project đã có:

- `web/WEB-INF/lib/jstl-1.2.jar`

## Routing (Servlet)

Map URL nằm trong `web/WEB-INF/web.xml`.

### Client

- `/` hoặc `/home` → `HomeServlet` → `WEB-INF/jsp/client/home.jsp`
- `/login`, `/register`, `/logout` → `ClientAuthServlet`
- `/cart` + `/cart/add|update|clear|checkout` → `CartServlet`
- `/profile` + `/profile/edit` → `ProfileServlet`
- `/open-app` → `OpenAppServlet`

### Staff/Admin

- `/staff/login`, `/staff/logout` → `StaffAuthServlet`
- `/staff` → `StaffHomeServlet` → `WEB-INF/jsp/staff/dashboard.jsp`
- `/staff/rooms` → `StaffRoomsServlet`
- `/staff/computers` → `StaffComputersServlet`
- `/staff/customers` → `StaffCustomersServlet`
- `/staff/invoices` → `StaffInvoicesServlet`

## Ghi chú

- Hiện tại các Servlet đang để **demo** (forward JSP + set dữ liệu list rỗng / session cơ bản) để bạn chạy UI ngay theo cấu trúc chuẩn.
- Nếu bạn muốn mình “đổ logic” đầy đủ từ BE (SQL Server + stored procedure) vào project Servlet này (DAO/JDBC + service), mình sẽ tiếp tục port lần lượt theo từng chức năng: auth, catalog, order, rooms/computers, invoices.

