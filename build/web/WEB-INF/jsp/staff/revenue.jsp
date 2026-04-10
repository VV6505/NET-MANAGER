<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Báo cáo doanh thu — NET 269</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
    <style>
        .rev-page{
            background:linear-gradient(180deg,#f8fbff 0%,#eef5fb 100%);
            color:#16324f;
        }
        .rev-hero,.filter-card,.chart-box,.rev-table-wrap,.stat-card{
            background:#fff;
            border:1px solid rgba(13,110,253,.10);
            box-shadow:0 12px 28px rgba(30,64,175,.08);
        }
        .rev-hero{
            background:linear-gradient(120deg, rgba(13,110,253,.10), rgba(20,184,166,.12));
            border-radius:18px;
            padding:1.25rem 1.5rem;
        }
        .filter-card,.chart-box,.rev-table-wrap,.stat-card{border-radius:16px;}
        .stat-card{
            transition:transform .18s ease, box-shadow .18s ease;
            height:100%;
        }
        .stat-card:hover{
            transform:translateY(-4px);
            box-shadow:0 16px 28px rgba(13,110,253,.14);
        }
        .stat-card .icon{
            width:48px;height:48px;border-radius:14px;
            display:flex;align-items:center;justify-content:center;
            font-size:1.25rem;
        }
        .stat-card .val{font-size:1.35rem;font-weight:800;letter-spacing:.02em;}
        .rev-page .form-control,
        .rev-page .form-select{
            background:#fff !important;
            color:#16324f !important;
            border-color:#cfe0f2 !important;
        }
        .rev-page .form-control:focus,
        .rev-page .form-select:focus{
            border-color:#7bb5ff !important;
            box-shadow:0 0 0 .2rem rgba(13,110,253,.12) !important;
        }
        .btn-rev-primary{
            background:linear-gradient(90deg,#0d6efd,#14b8a6);
            border:none;
            font-weight:700;
        }
        .table.rev-table{margin-bottom:0;color:#1f3b57;}
        .table.rev-table thead th{
            background:linear-gradient(90deg, rgba(13,110,253,.12), rgba(20,184,166,.12));
            border-color:rgba(13,110,253,.08);
            font-weight:700;
            font-size:.85rem;
            text-transform:uppercase;
            letter-spacing:.04em;
        }
        .table.rev-table td{border-color:rgba(13,110,253,.06);vertical-align:middle;}
        .chart-pie-wrap{max-width:420px;margin:0 auto;}
    </style>
</head>
<body class="rev-page">
<jsp:include page="includes/header.jsp" />

<div class="grid">
    <section class="card" style="border:none;background:transparent;box-shadow:none;">
        <div class="bd" style="padding-top:0">
            <div class="rev-hero mb-4">
                <div class="d-flex flex-wrap align-items-center justify-content-between gap-3">
                    <div>
                        <h2 class="mb-1" style="font-weight:800;letter-spacing:.03em;">
                            <i class="fa-solid fa-chart-line text-info me-2"></i>Báo cáo doanh thu
                        </h2>
                        <div class="text-secondary small">Gồm hóa đơn <strong>Đã thanh toán</strong> và <strong>Đã đặt</strong>, giao diện sáng để dễ đọc hơn.</div>
                    </div>
                    <div class="d-flex flex-wrap gap-2">
                        <c:url var="urlExcel" value="/staff/revenue/export/excel">
                            <c:param name="from" value="${from}"/>
                            <c:param name="to" value="${to}"/>
                            <c:param name="customerName" value="${customerName}"/>
                        </c:url>
                        <c:url var="urlPdf" value="/staff/revenue/export/pdf">
                            <c:param name="from" value="${from}"/>
                            <c:param name="to" value="${to}"/>
                            <c:param name="customerName" value="${customerName}"/>
                        </c:url>
                        <a class="btn btn-success btn-sm px-3" href="${urlExcel}">
                            <i class="fa-regular fa-file-excel me-1"></i> Xuất Excel
                        </a>
                        <a class="btn btn-danger btn-sm px-3" href="${urlPdf}">
                            <i class="fa-regular fa-file-pdf me-1"></i> Xuất PDF
                        </a>
                    </div>
                </div>
            </div>

            <div class="filter-card p-3 p-md-4 mb-4">
                <form class="row g-3 align-items-end" method="get" action="${pageContext.request.contextPath}/staff/revenue">
                    <div class="col-12 col-md-3">
                        <label class="form-label small text-secondary mb-1">Từ ngày</label>
                        <input type="date" class="form-control form-control-lg" name="from" value="${from}"/>
                    </div>
                    <div class="col-12 col-md-3">
                        <label class="form-label small text-secondary mb-1">Đến ngày</label>
                        <input type="date" class="form-control form-control-lg" name="to" value="${to}"/>
                    </div>
                    <div class="col-12 col-md-4">
                        <label class="form-label small text-secondary mb-1">Lọc theo khách</label>
                        <select name="customerName" class="form-select form-select-lg">
                            <option value="">— Tất cả khách —</option>
                            <c:forEach var="n" items="${paidCustomerNames}">
                                <option value="${n}" ${customerName == n ? 'selected' : ''}><c:out value="${n}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-12 col-md-2 d-grid gap-2">
                        <button type="submit" class="btn btn-lg btn-rev-primary text-white">
                            <i class="fa-solid fa-filter me-1"></i> Xem báo cáo
                        </button>
                        <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/staff/revenue">Reset</a>
                    </div>
                </form>
            </div>

            <div class="row g-3 mb-4">
                <div class="col-6 col-xl-3">
                    <div class="stat-card p-3">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <div class="small text-secondary text-uppercase">Tổng doanh thu</div>
                                <div class="val mt-1" style="color:#16324f;"><fmt:formatNumber value="${tongDoanhThu}" type="number"/> đ</div>
                            </div>
                            <div class="icon bg-primary bg-opacity-10 text-primary"><i class="fa-solid fa-sack-dollar"></i></div>
                        </div>
                    </div>
                </div>
                <div class="col-6 col-xl-3">
                    <div class="stat-card p-3">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <div class="small text-secondary text-uppercase">Tiền game</div>
                                <div class="val mt-1" style="color:#0d6efd;"><fmt:formatNumber value="${tongTienGame}" type="number"/> đ</div>
                            </div>
                            <div class="icon" style="background:rgba(13,110,253,.12);color:#0d6efd;"><i class="fa-solid fa-gamepad"></i></div>
                        </div>
                    </div>
                </div>
                <div class="col-6 col-xl-3">
                    <div class="stat-card p-3">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <div class="small text-secondary text-uppercase">Tiền đồ ăn</div>
                                <div class="val mt-1" style="color:#14b8a6;"><fmt:formatNumber value="${tongTienDoAn}" type="number"/> đ</div>
                            </div>
                            <div class="icon" style="background:rgba(20,184,166,.12);color:#14b8a6;"><i class="fa-solid fa-burger"></i></div>
                        </div>
                    </div>
                </div>
                <div class="col-6 col-xl-3">
                    <div class="stat-card p-3">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <div class="small text-secondary text-uppercase">Tiền giờ chơi</div>
                                <div class="val mt-1" style="color:#198754;"><fmt:formatNumber value="${tongTienGioChoi}" type="number"/> đ</div>
                            </div>
                            <div class="icon" style="background:rgba(25,135,84,.12);color:#198754;"><i class="fa-solid fa-clock"></i></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-4 mb-4">
                <div class="col-12">
                    <div class="chart-box">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <h5 class="mb-0" style="font-weight:700;color:#16324f;"><i class="fa-solid fa-chart-pie text-info me-2"></i>Tỷ trọng doanh thu theo ngày</h5>
                            <span class="badge text-bg-light border text-secondary">Chart.js</span>
                        </div>
                        <div class="chart-pie-wrap">
                            <canvas id="revChart" height="260"></canvas>
                        </div>
                        <p class="small text-secondary mt-2 mb-0">Biểu đồ tròn thể hiện tỷ trọng doanh thu của từng ngày trong khoảng lọc.</p>
                    </div>
                </div>
            </div>

            <div class="rev-table-wrap">
                <div class="px-3 py-2 border-bottom border-primary border-opacity-10 d-flex justify-content-between align-items-center">
                    <span class="fw-bold"><i class="fa-solid fa-table me-2 text-info"></i>Chi tiết hóa đơn</span>
                    <span class="small text-secondary">Số dòng: ${listHoaDon.size()}</span>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover rev-table table-striped">
                        <thead>
                        <tr>
                            <th>Mã hóa đơn</th>
                            <th>Ngày</th>
                            <th class="text-end">Tiền game</th>
                            <th class="text-end">Tiền đồ ăn</th>
                            <th class="text-end">Tiền giờ</th>
                            <th class="text-end">Tổng tiền</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="hd" items="${listHoaDon}">
                            <tr>
                                <td><b class="text-primary"><c:out value="${hd.maHoaDon}"/></b></td>
                                <td><c:out value="${hd.ngay}"/></td>
                                <td class="text-end"><fmt:formatNumber value="${hd.tongTienGame}" type="number"/></td>
                                <td class="text-end"><fmt:formatNumber value="${hd.tongTienDoAn}" type="number"/></td>
                                <td class="text-end"><fmt:formatNumber value="${hd.tienGioChoi}" type="number"/></td>
                                <td class="text-end fw-semibold"><fmt:formatNumber value="${hd.tongCong}" type="number"/> đ</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty listHoaDon}">
                            <tr><td colspan="6" class="text-center py-4 text-secondary">Chưa có dữ liệu trong khoảng đã chọn.</td></tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </section>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.2/dist/chart.umd.min.js"></script>
<script>
(function () {
    const labels = [
        <c:forEach var="lb" items="${chartLabels}" varStatus="st">
        '<c:out value="${lb}"/>'<c:if test="${!st.last}">,</c:if>
        </c:forEach>
    ];
    const values = [
        <c:forEach var="v" items="${chartData}" varStatus="st">
        ${v}<c:if test="${!st.last}">,</c:if>
        </c:forEach>
    ];
    const ctx = document.getElementById('revChart');
    if (!ctx) return;
    if (!labels.length) {
        ctx.replaceWith(Object.assign(document.createElement('p'), {
            className: 'text-secondary text-center py-5 mb-0',
            textContent: 'Không có điểm dữ liệu để vẽ biểu đồ.'
        }));
        return;
    }
    const pieColors = ['#0d6efd','#14b8a6','#6f42c1','#fd7e14','#20c997','#dc3545','#198754','#6610f2','#0dcaf0','#ffc107'];
    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                label: 'Doanh thu (đ)',
                data: values,
                backgroundColor: labels.map((_, i) => pieColors[i % pieColors.length]),
                borderColor: '#ffffff',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#4a6178',
                        padding: 16
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const value = context.raw || 0;
                            return context.label + ': ' + Number(value).toLocaleString('vi-VN') + ' đ';
                        }
                    }
                }
            }
        }
    });
})();
</script>

<jsp:include page="includes/footer.jsp" />
</body>
</html>
