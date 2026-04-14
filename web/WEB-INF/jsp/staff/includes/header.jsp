<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="layout">
    <aside class="sidebar">
        <div class="brand">
            <div class="logo">⚡</div>
            <div>
                <div class="title">NET 269 Admin</div>
                <div class="sub">Quản lý quán NET</div>
            </div>
        </div>

        <nav class="nav">
            <c:set var="p" value="${pageContext.request.requestURI}" />
            <c:set var="roleLower" value="${fn:toLowerCase(sessionScope.staffRole)}" />
            <c:set var="canViewRevenue" value="${fn:contains(roleLower, 'quản lý') or fn:contains(roleLower, 'thu ngân')}" />
            <c:set var="canManageEmployees" value="${fn:contains(roleLower, 'quản lý') or fn:contains(roleLower, 'admin')}" />

            <a href="${pageContext.request.contextPath}/staff"
               class="${p.endsWith('/staff') || p.endsWith('/staff/') ? 'active' : ''}">
                <span class="dot"></span> Tổng quan
            </a>
            <a href="${pageContext.request.contextPath}/staff/rooms"
               class="${p.contains('/staff/rooms') ? 'active' : ''}">
                <span class="dot"></span> Khu vực
            </a>
            <a href="${pageContext.request.contextPath}/staff/computers"
               class="${p.contains('/staff/computers') ? 'active' : ''}">
                <span class="dot"></span> Máy tính
            </a>
            <a href="${pageContext.request.contextPath}/staff/customers"
               class="${p.contains('/staff/customers') ? 'active' : ''}">
                <span class="dot"></span> Khách hàng
            </a>
            <a href="${pageContext.request.contextPath}/staff/employees"
               class="${p.contains('/staff/employees') ? 'active' : ''}">
                <span class="dot"></span>
                ${canManageEmployees ? 'Nhân viên' : 'Thông tin cá nhân'}
            </a>
            <a href="${pageContext.request.contextPath}/staff/invoices"
               class="${p.contains('/staff/invoices') ? 'active' : ''}">
                <span class="dot"></span> Hóa đơn
            </a>
            <c:if test="${canViewRevenue}">
                <a href="${pageContext.request.contextPath}/staff/revenue"
                   class="${p.contains('/staff/revenue') ? 'active' : ''}">
                    <span class="dot"></span> Doanh thu
                </a>
            </c:if>
        </nav>

        <div class="footer">
            <div>Đang đăng nhập: <b>${sessionScope.staffName}</b></div>
            <div class="sub">${sessionScope.staffRole}</div>
            <a class="logout" href="${pageContext.request.contextPath}/staff/logout">Đăng xuất</a>
        </div>
    </aside>

    <main class="main">
        <div class="topbar">
            <div class="hello">👋 Xin chào, <strong>${sessionScope.staffName}</strong></div>
            <span class="chip">${sessionScope.staffRole}</span>
        </div>

        <c:if test="${not empty sessionScope.successMessage}">
            <div class="msg ok">✓ ${sessionScope.successMessage}</div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="msg err">✕ ${sessionScope.errorMessage}</div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <div class="msg err">✕ ${requestScope.error}</div>
        </c:if>

