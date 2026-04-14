<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NET 269 - Phòng Livestream</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <!-- Thêm Slick Slider CSS -->
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.css"/>
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick-theme.css"/>
    <!-- Thêm Toastify CSS -->
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/toastify-js/src/toastify.min.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <!-- Toast notification -->
    <c:if test="${not empty sessionScope.message}">
        <script>
            window.addEventListener('DOMContentLoaded', function () {
                const headerEl = document.querySelector('.header');
                const headerH = headerEl ? headerEl.getBoundingClientRect().height : 70;
                const offsetY = Math.round(headerH + 12);
                Toastify({
                    text: "${sessionScope.message}",
                    duration: 2500,
                    gravity: "top",
                    position: "right",
                    offset: { x: 18, y: offsetY },
                    close: true,
                    className: "net-toast",
                    backgroundColor: "linear-gradient(90deg, #2ecc71, #27ae60)"
                }).showToast();
            });
        </script>
        <c:remove var="message" scope="session"/>
    </c:if>

    <div class="banner parallax">
        <div class="banner-background"></div>
        <img src="${pageContext.request.contextPath}/assets/images/banner.jpg"
             alt="Livestream Banner"
             onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/img/placeholder?type=game';">
        <div class="banner-overlay"></div>
        <div class="info">
            <h2>NET 269 PHÒNG LIVESTREAM</h2>
            <p>Chơi game, livestream, kết nối bạn bè!</p>
            <a href="#" class="banner-btn">Bắt đầu ngay</a>
        </div>
    </div>

    <!-- Phần giới thiệu -->
    <section class="intro-section">
            <div class="intro-content">
                <h2 class="section-title">GIỚI THIỆU VỀ NET 269</h2>
                <p>Chào mừng bạn đến với <strong>NET 269 - Phòng Livestream</strong>, nơi hội tụ của những người đam mê game, livestream và kết nối cộng đồng! Chúng tôi cung cấp không gian giải trí hiện đại với các tựa game hot nhất, dịch vụ đặt đồ ăn thức uống tiện lợi, và nền tảng livestream để bạn chia sẻ những khoảnh khắc đỉnh cao. Hãy tham gia ngay để trải nghiệm và trở thành một phần của cộng đồng game thủ sôi động!</p>
                <div class="intro-features">
                    <div class="feature-item">
                        <i class="fas fa-gamepad"></i>
                        <h4>Game Đỉnh Cao</h4>
                        <p>Khám phá bộ sưu tập game mới nhất, từ hành động đến chiến thuật.</p>
                    </div>
                    <div class="feature-item">
                        <i class="fas fa-video"></i>
                        <h4>Livestream Chuyên Nghiệp</h4>
                        <p>Phát trực tiếp và kết nối với hàng ngàn người xem.</p>
                    </div>
                    <div class="feature-item">
                        <i class="fas fa-utensils"></i>
                        <h4>Đồ Ăn & Thức Uống</h4>
                        <p>Đặt món yêu thích ngay tại chỗ, tiện lợi và nhanh chóng.</p>
                    </div>
                </div>
                <a href="#" class="intro-btn">Tham gia ngay</a>
            </div>
    </section>

    <h2 class="section-title">
        <c:choose>
            <c:when test="${not empty searchQuery}">
                KẾT QUẢ TÌM KIẾM: "${searchQuery}"
            </c:when>
            <c:otherwise>
                GAMES MỚI CẬP NHẬT
            </c:otherwise>
        </c:choose>
    </h2>
    <div class="game-list game-carousel">
        <c:forEach var="game" items="${dsGame}">
            <div class="game-card">
                <div class="game-image">
                    <img src="${pageContext.request.contextPath}/${game.hinhAnh}"
                         alt="${game.tenGame}"
                         onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/img/placeholder?type=game';" />
                </div>
                <div class="game-info">
                    <h4>${game.tenGame}</h4>
                    <p>Thể loại: ${game.theLoai}</p>
                    <p class="game-description">${game.moTa != null ? game.moTa : 'Game hấp dẫn'}</p>

                    <div class="game-actions">
                        <button type="button" class="play-btn" disabled>Chơi ngay</button>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
    <c:if test="${not empty searchQuery and empty dsGame}">
        <div class="search-empty" style="max-width:1200px;margin:12px auto 24px;">
            Không tìm thấy game phù hợp.
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.username}">
        <h2 class="section-title">ĐẶT ĐỒ ĂN & THỨC UỐNG</h2>
        <div class="food-list">
            <div class="food-grid">
                <c:forEach var="food" items="${dsFood}">
                    <div class="food-card">
                        <div class="food-image">
                            <img src="${pageContext.request.contextPath}/${food.hinhAnh}"
                                 alt="${food.tenDoAn}"
                                 onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/img/placeholder?type=food';" />
                        </div>
                        <div class="food-info">
                            <h4>${food.tenDoAn}</h4>
                            <p>Thể loại: ${food.loaiDoAn}</p>
                            <p class="price">${food.giaDoAn}đ</p>
                            <form action="${pageContext.request.contextPath}/cart/add" method="POST">
                                <input type="hidden" name="id" value="${food.maDoAn}">
                                <input type="hidden" name="redirect" value="home">
                                <button type="submit" class="play-btn">Thêm vào giỏ</button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
        <c:if test="${not empty searchQuery and empty dsFood}">
            <div class="search-empty" style="max-width:1200px;margin:12px auto 24px;">
                Không tìm thấy đồ ăn/thức uống phù hợp.
            </div>
        </c:if>
    </c:if>

    <jsp:include page="includes/footer.jsp" />

    <!-- Thêm jQuery và Slick Slider JS -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
    <!-- Thêm Toastify JS -->
    <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/toastify-js"></script>
    <script>
        $(document).ready(function(){
            $('.game-carousel').slick({
                slidesToShow: 5,
                slidesToScroll: 5,
                autoplay: true,
                autoplaySpeed: 3000,
                dots: true,
                arrows: true,
                variableWidth: false,
                adaptiveHeight: false,
                responsive: [
                    {
                        breakpoint: 1024,
                        settings: {
                            slidesToShow: 3,
                            slidesToScroll: 3
                        }
                    },
                    {
                        breakpoint: 600,
                        settings: {
                            slidesToShow: 2,
                            slidesToScroll: 2
                        }
                    },
                    {
                        breakpoint: 480,
                        settings: {
                            slidesToShow: 1,
                            slidesToScroll: 1
                        }
                    }
                ]
            });
        });
    </script>

    <script>
    function showLoginMessage() {
        // Lưu URL hiện tại vào session storage
        sessionStorage.setItem('returnUrl', window.location.href);
        
        // Hiển thị thông báo
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-success';
        alertDiv.textContent = 'Vui lòng đăng nhập để được vào giỏ hàng và đặt món!';
        document.body.appendChild(alertDiv);
        
        // Chuyển hướng sau 2 giây
        setTimeout(() => {
            window.location.href = '${pageContext.request.contextPath}/login';
        }, 2000);
    }

    // Kiểm tra và hiển thị thông báo từ session nếu có
    <% if (session.getAttribute("message") != null) { %>
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-success';
        alertDiv.textContent = '<%= session.getAttribute("message") %>';
        document.body.appendChild(alertDiv);
        <% session.removeAttribute("message"); %>
    <% } %>
    </script>
</body>
</html>