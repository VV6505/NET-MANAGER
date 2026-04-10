<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<header class="header">
    <div class="logo">
        <h1>NET 269</h1>
    </div>
    <nav class="nav-menu">
        <ul>
            <li><a href="${pageContext.request.contextPath}/home">TRANG CHỦ</a></li>
            <li><a href="${pageContext.request.contextPath}/history">LỊCH SỬ SỬ DỤNG</a></li>
            <li><a href="#">ĐỊA ĐIỂM</a></li>
            <li><a href="#">GAMES MỚI</a></li>
            <li><a href="#">DỊCH VỤ</a></li>
            <li><a href="#">FACEBOOK</a></li>
        </ul>
    </nav>
    <form class="search-bar" action="${pageContext.request.contextPath}/home" method="get">
        <input type="text" name="q" value="${fn:escapeXml(param.q)}" placeholder="Tìm kiếm...">
        <button type="submit" aria-label="Tìm kiếm"><i class="fas fa-search"></i></button>
    </form>
    <div class="auth-links">
        <c:choose>
            <c:when test="${not empty sessionScope.username}">
                <a href="${pageContext.request.contextPath}/cart" class="cart-icon">
                    <i class="fas fa-shopping-cart"></i>
                    <c:if test="${sessionScope.cartCount != null && sessionScope.cartCount > 0}">
                        <span class="cart-count" id="cart-count">${sessionScope.cartCount}</span>
                    </c:if>
                </a>
                <div class="user-profile">
                    <a href="${pageContext.request.contextPath}/profile" class="avatar">
                        <c:choose>
                            <c:when test="${not empty sessionScope.tenKhachHang}">
                                ${fn:substring(sessionScope.tenKhachHang, 0, 1)}
                            </c:when>
                            <c:otherwise>K</c:otherwise>
                        </c:choose>
                    </a>
                    <div class="welcome-text">
                        <span>Xin chào,</span>
                        <span class="username">${sessionScope.tenKhachHang}</span>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/logout" class="auth-btn">Đăng xuất</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/cart" class="cart-icon">
                    <i class="fas fa-shopping-cart"></i>
                </a>
                <a href="${pageContext.request.contextPath}/login" class="auth-btn">Đăng nhập</a>
                <a href="${pageContext.request.contextPath}/register" class="auth-btn">Đăng ký</a>
            </c:otherwise>
        </c:choose>
    </div>
</header>