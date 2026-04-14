<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ hàng - NET 269</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cart.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/toastify-js/src/toastify.min.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />
    
    <div class="cart-container">
        <h1 class="cart-title">Giỏ hàng của bạn</h1>

        <c:if test="${not empty sessionScope.message}">
            <script src="https://cdn.jsdelivr.net/npm/toastify-js"></script>
            <script>
                window.addEventListener('DOMContentLoaded', function () {
                    Toastify({
                        text: "${sessionScope.message}",
                        duration: 2500,
                        gravity: "top",
                        position: "right",
                        close: true,
                        className: "net-toast"
                    }).showToast();
                });
            </script>
            <c:remove var="message" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.errorMessage}">
            <script src="https://cdn.jsdelivr.net/npm/toastify-js"></script>
            <script>
                window.addEventListener('DOMContentLoaded', function () {
                    Toastify({
                        text: "${sessionScope.errorMessage}",
                        duration: 3000,
                        gravity: "top",
                        position: "right",
                        close: true,
                        className: "net-toast",
                        style: { background: "#e74c3c" }
                    }).showToast();
                });
            </script>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.orderNotice}">
            <div class="order-notice">
                <i class="fas fa-circle-check"></i>
                <span>${sessionScope.orderNotice}</span>
            </div>
            <c:remove var="orderNotice" scope="session"/>
        </c:if>
        
        <c:choose>
            <c:when test="${empty cartItems}">
            <div class="empty-cart-message">
                <i class="fas fa-shopping-cart fa-3x" style="color: #95a5a6; margin-bottom: 20px;"></i>
                <p>Giỏ hàng của bạn đang trống</p>
                <a href="${pageContext.request.contextPath}/home" class="checkout-btn" style="max-width: 200px; margin: 20px auto;">
                    Tiếp tục mua sắm
                </a>
            </div>
            </c:when>
            <c:otherwise>
            <div class="cart-items-container">
                <div class="cart-items-header">
                    <h2 class="cart-section-title">Sản phẩm trong giỏ</h2>
                    <form action="${pageContext.request.contextPath}/cart/clear" method="POST" class="clear-cart-form">
                         <button type="submit" class="clear-cart-btn">Xóa hết giỏ hàng</button>
                    </form>
                </div>
                
                <div class="cart-items">
                    <c:forEach var="item" items="${cartItems}">
                        <div class="cart-item">
                            <img src="${pageContext.request.contextPath}/${item.imageUrl}"
                                 alt="${item.name}"
                                 onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/img/placeholder?type=${item.type}';">
                            <div class="item-details">
                                <h3 class="item-name">
                                    [Dịch vụ] 
                                    ${item.name}
                                </h3>
                                <p class="item-price">
                                    <fmt:formatNumber value="${item.price}" type="number"/>đ
                                </p>
                            </div>
                            <form action="${pageContext.request.contextPath}/cart/update" method="POST" class="quantity-controls">
                                <input type="hidden" name="itemId" value="${item.id}">
                                <button type="submit" name="action" value="decrease" class="quantity-btn">
                                    <i class="fas fa-minus"></i>
                                </button>
                                <input type="number" value="${item.quantity}" min="1" readonly>
                                <button type="submit" name="action" value="increase" class="quantity-btn">
                                    <i class="fas fa-plus"></i>
                                </button>
                                <button type="submit" name="action" value="remove" class="remove-btn" title="Xóa sản phẩm">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </form>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="cart-summary">
                <div class="total-amount" style="margin-top:8px; opacity:.85;">
                    <span class="total-label">Đồ ăn:</span>
                    <span class="total-value"><fmt:formatNumber value="${totalFood}" type="number"/>đ</span>
                </div>
                <h3 class="summary-title">Tổng cộng</h3>
                <div class="total-amount">
                    <span class="total-label">Thành tiền:</span>
                    <span class="total-value"><fmt:formatNumber value="${total}" type="number"/>đ</span>
                </div>
                <div class="checkout-actions">
                    <a href="${pageContext.request.contextPath}/home" class="checkout-btn checkout-btn-secondary">
                        <i class="fas fa-plus"></i> Tiếp tục thêm món
                    </a>
                    <c:choose>
                        <c:when test="${not empty sessionScope.username}">
                        <c:if test="${not sessionScope.orderConfirmed}">
                        <form action="${pageContext.request.contextPath}/cart/checkout" method="POST" style="margin-top: 10px;">
                            <button type="submit" class="checkout-btn">
                                <i class="fas fa-credit-card"></i> Xác nhận đặt món
                            </button>
                        </form>
                        </c:if>
                        </c:when>
                        <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login?from=/cart" class="checkout-btn" style="margin-top: 10px;">
                            <i class="fas fa-sign-in-alt"></i> Đăng nhập để đặt hàng
                        </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            </c:otherwise>
        </c:choose>
    </div>

    <jsp:include page="includes/footer.jsp" />

    <c:if test="${not empty sessionScope.lastInvoiceId}">
        <script>
            window.addEventListener('DOMContentLoaded', function () {
                const invoiceId = "${sessionScope.lastInvoiceId}";
                const ctx = "${pageContext.request.contextPath}";
                const overlay = document.createElement('div');
                overlay.className = 'invoice-modal-overlay';
                overlay.innerHTML =
                    '<div class="invoice-modal" role="dialog" aria-modal="true" aria-label="Xuất hóa đơn">' +
                    '  <div class="invoice-modal-title">Đặt món thành công</div>' +
                    '  <div class="invoice-modal-text">Bạn có muốn xuất hóa đơn không?</div>' +
                    '  <div class="invoice-modal-actions">' +
                    '    <a class="invoice-modal-btn primary" href="' + ctx + '/invoice?maHoaDon=' + encodeURIComponent(invoiceId) + '">' +
                    '      Xuất hóa đơn' +
                    '    </a>' +
                    '    <button type="button" class="invoice-modal-btn ghost">Để sau</button>' +
                    '  </div>' +
                    '</div>';
                document.body.appendChild(overlay);

                const close = () => {
                    overlay.remove();
                };
                overlay.addEventListener('click', (e) => {
                    if (e.target === overlay) close();
                });
                overlay.querySelector('button.ghost')?.addEventListener('click', close);
                document.addEventListener('keydown', (e) => {
                    if (e.key === 'Escape') close();
                }, { once: true });
            });
        </script>
        <c:remove var="lastInvoiceId" scope="session"/>
    </c:if>
</body>
</html>