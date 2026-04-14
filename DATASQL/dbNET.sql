-- Xóa database nếu tồn tại
IF EXISTS (SELECT * FROM sys.databases WHERE name = N'dbNET')
BEGIN
    -- Đóng tất cả các kết nối đến cơ sở dữ liệu
    EXECUTE sp_MSforeachdb 'IF ''?'' = ''dbNET'' 
    BEGIN 
        DECLARE @sql AS NVARCHAR(MAX) = ''USE [?]; ALTER DATABASE [?] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;''
        EXEC (@sql)
    END'
    -- Xóa tất cả các kết nối tới cơ sở dữ liệu (thực hiện qua hệ thống master)
    USE master;
    DROP DATABASE dbNET;
END
GO

-- Tạo database mới
CREATE DATABASE dbNET;
GO

USE dbNET;
GO

-- Bảng LoaiNhanVien
CREATE TABLE LoaiNhanVien
(
    maLoaiNhanVien CHAR(6),
    tenLoaiNhanVien NVARCHAR(20),
    heSoLuong VARCHAR(5),
    moTa VARCHAR(50),
    CONSTRAINT PK_maLoaiNhanVien PRIMARY KEY(maLoaiNhanVien)
);
GO

-- Bảng NhanVien
CREATE TABLE NhanVien
(
    maNhanVien CHAR(6),
    tenNhanVien NVARCHAR(40),
    ngaySinh DATE,
    gioiTinh NVARCHAR(10),
    cmnd VARCHAR(12),
    ngayCap DATE,
    noiCap NVARCHAR(100),
    sdt VARCHAR(11),
    email VARCHAR(50),
    diaChi NVARCHAR(200),
    phongBan NVARCHAR(50),
    matKhau VARCHAR(20),
    ngayBatDau DATE,
    luongCoBan VARCHAR(10),
    maLoaiNhanVien CHAR(6),
    trangThai NVARCHAR(20) DEFAULT N'Đang làm việc',
    avatar VARBINARY(MAX),
    CONSTRAINT UQ_NhanVien_SDT UNIQUE(sdt),
    CONSTRAINT UQ_NhanVien_Email UNIQUE(email),
    CONSTRAINT UQ_NhanVien_CMND UNIQUE(cmnd),
    CONSTRAINT CK_NhanVien_SDT CHECK (sdt like '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'
                                    or sdt like '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CK_NhanVien_Email CHECK (email LIKE '%_@__%.__%'),
    CONSTRAINT CK_NhanVien_CMND CHECK (cmnd like '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'
                                     or cmnd like '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
    CONSTRAINT CK_NhanVien_GioiTinh CHECK (gioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    CONSTRAINT CK_NhanVien_TrangThai CHECK (trangThai IN (N'Đang làm việc', N'Nghỉ phép', N'Đã nghỉ việc')),
    CONSTRAINT PK_maNhanVien PRIMARY KEY(maNhanVien),
    CONSTRAINT FK_maLoaiNhanVien FOREIGN KEY(maLoaiNhanVien) REFERENCES LoaiNhanVien(maLoaiNhanVien)
                                 ON UPDATE CASCADE
                                 ON DELETE CASCADE
);
GO

-- Bảng LuongNhanVien
CREATE TABLE LuongNhanVien
(
    maPhieuLuong CHAR(6),
    maNhanVien CHAR(6),
    tongGioLam VARCHAR(10),
    thanhTien VARCHAR(10),
    CONSTRAINT PK_maPhieuLuong PRIMARY KEY(maPhieuLuong),
    CONSTRAINT FK_maNhanVien FOREIGN KEY(maNhanVien) REFERENCES NhanVien(maNhanVien)
                             ON UPDATE CASCADE
                             ON DELETE CASCADE
);
GO

-- Bảng LoaiKhachHang
CREATE TABLE LoaiKhachHang (
    maLoaiKhachHang CHAR(6) PRIMARY KEY,
    tenLoaiKhachHang NVARCHAR(40),
    moTa NVARCHAR(100)
);
GO

-- Bảng KhachHang
CREATE TABLE KhachHang (
    maKhachHang CHAR(6) PRIMARY KEY,
    tenKhachHang NVARCHAR(40),
    sdt VARCHAR(10),
    email VARCHAR(50) UNIQUE,
    matKhau VARCHAR(20),
    trangThaiTaiKhoan NVARCHAR(20) NOT NULL DEFAULT N'Đang hoạt động',
    soGioChoi DECIMAL(10,2),
    soDu DECIMAL(10,2),
    maLoaiKhachHang CHAR(6),
	CONSTRAINT CK_SDT_KH CHECK (SDT LIKE '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'),
	CONSTRAINT UQ_SDT_KH UNIQUE (SDT),
	CONSTRAINT CK_EMAIL_KH CHECK (Email LIKE '%_@__%.__%'),
    CONSTRAINT CK_TrangThaiTaiKhoan_KH CHECK (trangThaiTaiKhoan IN (N'Chờ xác nhận', N'Đang hoạt động', N'Đã khóa')),
    CONSTRAINT FK_maLKH_KH FOREIGN KEY (maLoaiKhachHang) REFERENCES LoaiKhachHang(maLoaiKhachHang) ON UPDATE CASCADE ON DELETE CASCADE
);
GO

-- Bảng KhuVuc
CREATE TABLE KhuVuc (
    maKhu CHAR(6) PRIMARY KEY,
    tenKhu NVARCHAR(40),
    moTa NVARCHAR(100)
);
GO

-- Bảng LoaiMay
CREATE TABLE LoaiMay (
    maLoaiMay CHAR(6) PRIMARY KEY,
    tenLoaiMay NVARCHAR(40),
    moTa NVARCHAR(100)
);
GO

-- Bảng MayTinh
CREATE TABLE MayTinh
(
    maMay CHAR(6),
    tenMay NVARCHAR(50),
    maKhu CHAR(6),
    cpu NVARCHAR(50),
    gpu NVARCHAR(50),
    ram NVARCHAR(20),
    ssd NVARCHAR(20),
    trangThai NVARCHAR(20),
    giaGio DECIMAL(10,2),
    maKhachHang CHAR(6),
    maLoaiMay CHAR(6),
    CONSTRAINT CK_trangThai CHECK (trangThai IN (N'Đang thuê', N'Còn trống', N'Đã đặt trước', N'Đang hư')),

    CONSTRAINT PK_maMay PRIMARY KEY(maMay),
    CONSTRAINT FK_maKhu FOREIGN KEY(maKhu) REFERENCES KhuVuc(maKhu)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
    CONSTRAINT FK_maLoaiMay FOREIGN KEY(maLoaiMay) REFERENCES LoaiMay(maLoaiMay)
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
    CONSTRAINT FK_maKhachHang FOREIGN KEY(maKhachHang) REFERENCES KhachHang(maKhachHang)
                            ON UPDATE CASCADE
                            ON DELETE No Action

);
GO

-- Bảng DichVu
CREATE TABLE DichVu (
    maDichVu CHAR(6),
    tenDichVu NVARCHAR(50),
    CONSTRAINT PK_DichVu PRIMARY KEY (maDichVu)
);
GO

-- Bảng TheLoaiGame
CREATE TABLE TheLoaiGame (
    maTheLoai CHAR(6),
    tenTheLoai NVARCHAR(30),
    moTa NVARCHAR(100),
    CONSTRAINT PK_TheLoaiGame PRIMARY KEY (maTheLoai)
);
GO

-- Bảng Loại Đồ Ăn
CREATE TABLE LoaiDoAn (
    maLoaiDoAn CHAR(6),
    tenLoaiDoAn NVARCHAR(30),
    moTa NVARCHAR(100),

    CONSTRAINT PK_LoaiDoAn PRIMARY KEY (maLoaiDoAn)
);
GO

-- Bảng Game
CREATE TABLE Game (
    maGame CHAR(6),
    tenGame NVARCHAR(50),
    giaGame DECIMAL(10,2),
    maTheLoai CHAR(6),
    maDichVu CHAR(6),
    hinhAnh NVARCHAR(200),

    CONSTRAINT PK_Game PRIMARY KEY (maGame),
    CONSTRAINT FK_Game_TheLoai FOREIGN KEY (maTheLoai) REFERENCES TheLoaiGame(maTheLoai)
                                ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_Game_DichVu FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
                               ON UPDATE CASCADE ON DELETE CASCADE
);
GO

-- Bảng Đồ Ăn
CREATE TABLE DoAn (
    maDoAn CHAR(6),
    tenDoAn NVARCHAR(50),
    giaDoAn DECIMAL(10,2),
    maLoaiDoAn CHAR(6),
    maDichVu CHAR(6),
    hinhAnh NVARCHAR(200),

    CONSTRAINT PK_DoAn PRIMARY KEY (maDoAn),
    CONSTRAINT FK_DoAn_LoaiDoAn FOREIGN KEY (maLoaiDoAn) REFERENCES LoaiDoAn(maLoaiDoAn)
                                 ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_DoAn_DichVu FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
                               ON UPDATE CASCADE ON DELETE CASCADE
);
GO

-- Bảng LichSuSuDung
CREATE TABLE LichSuSuDung (
    maSuDung CHAR(6) PRIMARY KEY,
    maKhachHang CHAR(6),
    maMay CHAR(6),
    thoiGianVao DATETIME,
    thoiGianRa DATETIME,
	CONSTRAINT FK_LichSuSuDung_KhachHang FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang)
                                ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT FK_LichSuSuDung_MayTinh FOREIGN KEY (maMay) REFERENCES MayTinh(maMay)
                                ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- Bảng HoaDon
CREATE TABLE HoaDon (
    maHoaDon CHAR(6) PRIMARY KEY,
    maNhanVien CHAR(6),
    maKhachHang CHAR(6),
    maSuDung CHAR(6) NULL,
    ngay DATE,
    ngayDat DATETIME,
    soGioChoi INT CHECK (soGioChoi >= 0),
    tongTienDoAn DECIMAL(10,2) DEFAULT 0,
    tongTienGame DECIMAL(10,2) DEFAULT 0,
    tongTien DECIMAL(10,2) DEFAULT 0,
    trangThai NVARCHAR(20) CHECK (trangThai IN (N'Tạm', N'Đã đặt', N'Đã thanh toán')),
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (maSuDung) REFERENCES LichSuSuDung(maSuDung) ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

-- Bảng Hóa Đơn Game
CREATE TABLE HoaDonGame (
    maHDGame CHAR(6),
    maGame CHAR(6),
    soLuong INT,
    donGia MONEY,
    thanhTien DECIMAL(10,2),
    PRIMARY KEY (maHDGame, maGame),
    FOREIGN KEY (maHDGame) REFERENCES HoaDon(maHoaDon) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (maGame) REFERENCES Game(maGame) ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

-- Bảng Hóa Đơn Đồ Ăn
CREATE TABLE HoaDonDoAn (
    maHDDoAn CHAR(6),
    maDoAn CHAR(6),
    soLuong INT,
    donGia DECIMAL(10,2),
    thanhTien DECIMAL(10,2),
    PRIMARY KEY (maHDDoAn, maDoAn),
    FOREIGN KEY (maHDDoAn) REFERENCES HoaDon(maHoaDon) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (maDoAn) REFERENCES DoAn(maDoAn) ON UPDATE CASCADE ON DELETE CASCADE
);
GO

-- Thêm dữ liệu cho bảng LoaiNhanVien
INSERT INTO LoaiNhanVien (maLoaiNhanVien, tenLoaiNhanVien, heSoLuong, moTa)
VALUES 
    ('LNV001', N'Quản lý', '2.0', N'Quản lý toàn bộ hoạt động của quán'),
    ('LNV002', N'Nhân viên thu ngân', '1.5', N'Thu ngân và quản lý hóa đơn'),
    ('LNV003', N'Nhân viên kỹ thuật', '1.5', N'Bảo trì và sửa chữa máy tính'),
    ('LNV004', N'Nhân viên phục vụ', '1.2', N'Phục vụ đồ ăn và thức uống');

-- Thêm dữ liệu cho bảng NhanVien
INSERT INTO NhanVien (maNhanVien, tenNhanVien, ngaySinh, gioiTinh, cmnd, ngayCap, noiCap, 
                     sdt, email, diaChi, phongBan, matKhau, ngayBatDau, luongCoBan, maLoaiNhanVien, trangThai)
VALUES 
    ('NV0001', N'Nguyễn Văn A', '1990-01-01', N'Nam', '123456789012', '2010-01-01', N'Công an TP. Đà Nẵng',
     '0123456789', 'admin@example.com', N'123 Đường ABC, Quận XYZ, TP. Đà Nẵng', N'Quản trị hệ thống',
     '123456', '2023-01-01', '8000000', 'LNV001', N'Đang làm việc'),
     
    ('NV0002', N'Trần Thị B', '1992-05-15', N'Nữ', '987654321012', '2012-05-15', N'Công an TP. Hồ Chí Minh',
     '0987654321', 'thungan@example.com', N'456 Đường DEF, Quận 1, TP. Hồ Chí Minh', N'Thu ngân',
     '123456', '2023-02-01', '6000000', 'LNV002', N'Đang làm việc'),
     
    ('NV0003', N'Lê Văn C', '1988-08-20', N'Nam', '456789123012', '2008-08-20', N'Công an TP. Hà Nội',
     '0909090909', 'kythuat@example.com', N'789 Đường GHI, Quận Đống Đa, TP. Hà Nội', N'Kỹ thuật',
     '123456', '2023-03-01', '6000000', 'LNV003', N'Đang làm việc'),
     
    ('NV0004', N'Phạm Thị D', '1995-03-10', N'Nữ', '789123456012', '2015-03-10', N'Công an TP. Cần Thơ',
     '0911111111', 'phucvu@example.com', N'321 Đường JKL, Quận Ninh Kiều, TP. Cần Thơ', N'Phục vụ',
     '123456', '2023-04-01', '5000000', 'LNV004', N'Đang làm việc'),
     
    ('NV0005', N'Hoàng Văn E', '1993-11-25', N'Nam', '321654987012', '2013-11-25', N'Công an TP. Hải Phòng',
     '0922222222', 'phucvu2@example.com', N'654 Đường MNO, Quận Hồng Bàng, TP. Hải Phòng', N'Phục vụ',
     '123456', '2023-05-01', '5000000', 'LNV004', N'Đang làm việc');

-- Bảng LoaiKhachHang (maLoaiKhachHang, tenLoaiKhachHang, moTa)
INSERT INTO LoaiKhachHang (maLoaiKhachHang, tenLoaiKhachHang, moTa) VALUES 
('LK0001', N'Khách thường', N'Không ưu đãi'),
('LK0002', N'Thành viên', N'Ưu đãi tích điểm');

-- Bảng KhachHang (maKhachHang, tenKhachHang, sdt, email, matKhau, soGioChoi, soDu, maLoaiKhachHang)
INSERT INTO KhachHang (maKhachHang, tenKhachHang, sdt, email, matKhau, soGioChoi, soDu, maLoaiKhachHang) VALUES 
('KH0001', N'Phạm Văn C', '0123456789', 'phamc@example.com', 'abc123', 2.5, 100000, 'LK0001'),
('KH0002', N'Lê Thị D', '0987654321', 'led@example.com', 'xyz789', 2.0, 200000, 'LK0001'),
('KH0003', N'Lê Minh Tuấn', '0912345673', 'leminhtuan3@example.com', 'tuan789', 1.30, 200000.00, 'LK0001'),
-- 1 khách hàng VIP (LK0002)
('KH0004', N'Trần Quốc Bảo', '0912345686', 'tranquocbao16@example.com', 'bao123', 3.5, 500000.00, 'LK0002')

GO


-- Bảng KhuVuc (maKhu, tenKhu, moTa)
INSERT INTO KhuVuc (maKhu, tenKhu, moTa) VALUES
('KV0001', N'Khu A', N'Khu máy tính thường'),
('KV0002', N'Khu B', N'Khu máy tính cao cấp'),
('KV0003', N'Khu VIP', N'Khu máy tính VIP'),
('KV0004', N'Khu Gaming', N'Khu máy tính chuyên game'),
('KV0005', N'Khu Workstation', N'Khu máy tính làm việc');
GO

-- Bảng LoaiMay (maLoaiMay, tenLoaiMay, moTa)
INSERT INTO LoaiMay (maLoaiMay, tenLoaiMay, moTa) VALUES
('LM0001', N'Máy thường', N'Máy tính cấu hình cơ bản'),
('LM0002', N'Máy cao cấp', N'Máy tính cấu hình cao'),
('LM0004', N'Máy Gaming', N'Máy tính chuyên game'),
('LM0005', N'Máy Workstation', N'Máy tính làm việc chuyên nghiệp'),
('LM0003', N'Máy VIP', N'Máy tính cao cấp dành cho khách VIP');
GO

-- Bảng MayTinh (maMay, maKhu, cpu, gpu, ram, ssd, trangThai, giaGio, maLoaiMay)
INSERT INTO MayTinh (maMay, tenMay, maKhu, cpu, gpu, ram, ssd, trangThai, giaGio, maLoaiMay) VALUES
-- Tầng 1: 30 máy Bình Thường
('MT0001', N'Máy 01', 'KV0001', N'Intel Core i3-12100', N'Intel UHD Graphics 730', N'8GB DDR4', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0002', N'Máy 02', 'KV0001', N'Intel Core i3-12100', N'Intel UHD Graphics 730', N'8GB DDR4', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0003', N'Máy 03', 'KV0001', N'Intel Core i3-12100', N'Intel UHD Graphics 730', N'8GB DDR4', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0004', N'Máy 04', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0005', N'Máy 05', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0006', N'Máy 06', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0007', N'Máy 07', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0008', N'Máy 08', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0009', N'Máy 09', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0010', N'Máy 10', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0011', N'Máy 11', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0012', N'Máy 12', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0013', N'Máy 13', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0014', N'Máy 14', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),
('MT0015', N'Máy 15', 'KV0001', N'Intel i3-10100F', N'GTX 1050', N'8GB', N'256GB', N'Còn trống', 10000, 'LM0001'),

-- Khu 2: 5 máy cao cấp
('MT0016', N'Máy 16', 'KV0002', N'Intel i5-11400F', N'RTX 2060', N'16GB', N'512GB', N'Còn trống', 15000, 'LM0002'),
('MT0017', N'Máy 17', 'KV0002', N'Intel i5-11400F', N'RTX 2060', N'16GB', N'512GB', N'Còn trống', 15000, 'LM0002'),
('MT0018', N'Máy 18', 'KV0002', N'Intel i5-11400F', N'RTX 2060', N'16GB', N'512GB', N'Còn trống', 15000, 'LM0002'),
('MT0019', N'Máy 19', 'KV0002', N'Intel i5-11400F', N'RTX 2060', N'16GB', N'512GB', N'Còn trống', 15000, 'LM0002'),
('MT0020', N'Máy 20', 'KV0002', N'Intel i5-11400F', N'RTX 2060', N'16GB', N'512GB', N'Còn trống', 15000, 'LM0002'),

-- Khu 3: 5 máy VIP 
('MT0021', N'Máy 21', 'KV0003', N'Intel i7-12700K', N'RTX 3080', N'32GB', N'1TB', N'Còn trống', 25000, 'LM0003'),
('MT0022', N'Máy 22', 'KV0003', N'Intel i7-12700K', N'RTX 3080', N'32GB', N'1TB', N'Còn trống', 25000, 'LM0003'),
('MT0023', N'Máy 23', 'KV0003', N'Intel Core i5-13400', N'NVIDIA GTX 1650', N'16GB DDR4', N'512GB', N'Còn trống', 12000, 'LM0003'),
('MT0024', N'Máy 24', 'KV0003', N'Intel Core i5-13400', N'NVIDIA GTX 1650', N'16GB DDR4', N'512GB', N'Còn trống', 12000, 'LM0003'),
('MT0025', N'Máy 25', 'KV0003', N'Intel Core i5-13400', N'NVIDIA GTX 1650', N'16GB DDR4', N'512GB', N'Còn trống', 12000, 'LM0003'),

-- Khu 4: 2 máy Gmaming
('MT0026', N'Máy 26', 'KV0004', N'Intel Xeon W-2295', N'NVIDIA Quadro RTX 5000', N'128GB DDR4', N'2TB', N'Còn trống', 25000, 'LM0004'),
('MT0027', N'Máy 27', 'KV0004', N'Intel Xeon W-2295', N'NVIDIA Quadro RTX 5000', N'128GB DDR4', N'2TB', N'Còn trống', 25000, 'LM0004'),

-- Khu 5: 3 máy làm việc
('MT0028', N'Máy 28', 'KV0005', N'AMD Ryzen 9 5900X', N'NVIDIA RTX 3080', N'32GB DDR4', N'1TB', N'Còn trống', 16000, 'LM0005'),
('MT0029', N'Máy 29', 'KV0005', N'Intel Core i9-12900K', N'NVIDIA RTX 4090', N'64GB DDR5', N'2TB', N'Còn trống', 22000, 'LM0005'),
('MT0030', N'Máy 30', N'KV0005', N'Intel Core i9-12900K', N'NVIDIA RTX 4090', N'64GB DDR5', N'2TB', N'Còn trống', 22000, 'LM0005');


-- Bảng DichVu (maDichVu, tenDichVu)
INSERT INTO DichVu (maDichVu, tenDichVu) VALUES 
('DV0001', N'Dịch vụ máy tính'),
('DV0002', N'Dịch vụ đồ ăn');

-- Bảng TheLoaiGame (maTheLoai, tenTheLoai, moTa)
INSERT INTO TheLoaiGame (maTheLoai, tenTheLoai, moTa) VALUES 
('TL0001', N'MOBA', N'Game đấu trường trận chiến'),
('TL0002', N'FPS', N'Game bắn súng'),
('TL0003', N'Battle Royale', N'Game sinh tồn'),
('TL0004', N'Sports', N'Game thể thao');

-- Bảng Game (maGame, tenGame, giaGame, maTheLoai, maDichVu)
INSERT INTO Game (maGame, tenGame, giaGame, maTheLoai, maDichVu) VALUES 
('G00001', N'Liên Minh', 0, 'TL0001', 'DV0001'),
('G00002', N'CS:GO', 0, 'TL0002', 'DV0001'),
('G00003', N'Valorant', 0, 'TL0002', 'DV0001'),
('G00004', N'FIFA Online 4', 0, 'TL0004', 'DV0001'),
('G00005', N'Dota 2', 0, 'TL0001', 'DV0001'),
('G00006', N'Apex Legends', 0, 'TL0003', 'DV0001'),
('G00007', N'PUBG', 0, 'TL0003', 'DV0001'),
('G00008', N'Genshin Impact', 0, 'TL0001', 'DV0001'),
('G00009', N'Overwatch 2', 0, 'TL0002', 'DV0001'),
('G00010', N'Call of Duty: Warzone', 0, 'TL0003', 'DV0001');

-- Bảng LoaiDoAn (maLoaiDoAn, tenLoaiDoAn, moTa)
INSERT INTO LoaiDoAn (maLoaiDoAn, tenLoaiDoAn, moTa) VALUES 
('LDA001', N'Đồ ăn nhanh', N'Các món ăn nhanh'),
('LDA002', N'Thức uống', N'Các loại nước giải khát'),
('LDA003', N'Món chính', N'Các món ăn chính');

-- Bảng DoAn (maDoAn, tenDoAn, giaDoAn, maLoaiDoAn, maDichVu)
INSERT INTO DoAn (maDoAn, tenDoAn, giaDoAn, maLoaiDoAn, maDichVu) VALUES 
('DA0001', N'Pizza Hải Sản', 89000, 'LDA001', 'DV0002'),
('DA0002', N'Coca Cola', 15000, 'LDA002', 'DV0002'),
('DA0003', N'Hamburger Gà', 45000, 'LDA001', 'DV0002'),
('DA0004', N'Trà Sữa Trân Châu', 25000, 'LDA002', 'DV0002'),
('DA0005', N'Mì Ý Sốt Cà', 55000, 'LDA001', 'DV0002'),
('DA0006', N'Gà Rán KFC', 75000, 'LDA001', 'DV0002'),
('DA0007', N'Pepsi', 15000, 'LDA002', 'DV0002'),
('DA0008', N'Bánh Mì Thịt Nướng', 25000, 'LDA001', 'DV0002'),
('DA0009', N'Nước Ép Cam', 20000, 'LDA002', 'DV0002'),
('DA0010', N'Phở Bò', 45000, 'LDA003', 'DV0002');

-- Cập nhật đường dẫn hình ảnh cho Game
UPDATE Game SET hinhAnh = 'assets/images/games/lmht.jpg' WHERE tenGame = N'Liên Minh';
UPDATE Game SET hinhAnh = 'assets/images/games/csgo.jpg' WHERE tenGame = N'CS:GO';
UPDATE Game SET hinhAnh = 'assets/images/games/valorant.jpg' WHERE tenGame = N'Valorant';
UPDATE Game SET hinhAnh = 'assets/images/games/fifa4.jpg' WHERE tenGame = N'FIFA Online 4';
UPDATE Game SET hinhAnh = 'assets/images/games/dota2.jpg' WHERE tenGame = N'Dota 2';
UPDATE Game SET hinhAnh = 'assets/images/games/apex.jpg' WHERE tenGame = N'Apex Legends';
UPDATE Game SET hinhAnh = 'assets/images/games/pubg.jpg' WHERE tenGame = N'PUBG';
UPDATE Game SET hinhAnh = 'assets/images/games/genshin.jpg' WHERE tenGame = N'Genshin Impact';
UPDATE Game SET hinhAnh = 'assets/images/games/overwatch2.jpg' WHERE tenGame = N'Overwatch 2';
UPDATE Game SET hinhAnh = 'assets/images/games/warzone.jpg' WHERE tenGame = N'Call of Duty: Warzone';
GO

-- Cập nhật đường dẫn hình ảnh cho DoAn
UPDATE DoAn SET hinhAnh = 'assets/images/foods/food1.jpg' WHERE maDoAn = 'DA0001';
UPDATE DoAn SET hinhAnh = 'assets/images/foods/food2.jpg' WHERE maDoAn = 'DA0002';
UPDATE DoAn SET hinhAnh = 'assets/images/foods/food3.jpg' WHERE maDoAn = 'DA0003';
UPDATE DoAn SET hinhAnh = 'assets/images/foods/food4.jpg' WHERE maDoAn = 'DA0004';
UPDATE DoAn SET hinhAnh = 'assets/images/foods/food5.jpg' WHERE maDoAn = 'DA0005';
UPDATE DoAn SET hinhAnh = 'assets/images/foods/food6.jpg' WHERE maDoAn = 'DA0006';
UPDATE DoAn SET hinhAnh = 'assets/images/foods/food7.jpg' WHERE maDoAn = 'DA0007';
UPDATE DoAn SET hinhAnh = 'assets/images/foods/food8.jpg' WHERE maDoAn = 'DA0008';
UPDATE DoAn SET hinhAnh = 'assets/images/foods/food9.jpg' WHERE maDoAn = 'DA0009';
UPDATE DoAn SET hinhAnh = 'assets/images/foods/food10.jpg' WHERE maDoAn = 'DA0010';
GO

IF COL_LENGTH('HoaDon', 'tienMay') IS NULL
BEGIN
    ALTER TABLE HoaDon ADD tienMay DECIMAL(10,2) NOT NULL CONSTRAINT DF_HoaDon_tienMay DEFAULT 0;
END
GO

IF COL_LENGTH('KhachHang', 'trangThaiTaiKhoan') IS NULL
BEGIN
    ALTER TABLE KhachHang ADD trangThaiTaiKhoan NVARCHAR(20) NOT NULL CONSTRAINT DF_KhachHang_TrangThaiTaiKhoan DEFAULT N'Đang hoạt động';
END
GO

-- Cập nhật stored procedure để lấy thêm hinhAnh
DROP PROCEDURE IF EXISTS sp_GetAllGames;
GO

CREATE PROCEDURE sp_GetAllGames
AS
BEGIN
    SELECT g.maGame, g.tenGame, g.giaGame, tl.tenTheLoai, tl.moTa, g.hinhAnh
    FROM Game g
    INNER JOIN TheLoaiGame tl ON g.maTheLoai = tl.maTheLoai
    ORDER BY g.tenGame;
END
GO

DROP PROCEDURE IF EXISTS sp_GetAllFoods;
GO

CREATE PROCEDURE sp_GetAllFoods
AS
BEGIN
    SELECT d.maDoAn, d.tenDoAn, d.giaDoAn, l.tenLoaiDoAn, l.moTa, d.hinhAnh
    FROM DoAn d
    INNER JOIN LoaiDoAn l ON d.maLoaiDoAn = l.maLoaiDoAn
    ORDER BY d.tenDoAn;
END
GO

-- Stored procedure tính thành tiền đồ ăn
DROP PROCEDURE IF EXISTS sp_TinhThanhTienDoAn;
GO

CREATE PROCEDURE sp_TinhThanhTienDoAn
    @maHDDoAn CHAR(6)
AS
BEGIN
    UPDATE HoaDonDoAn
    SET thanhTien = ct.soLuong * d.giaDoAn
    FROM HoaDonDoAn ct
    INNER JOIN DoAn d ON ct.maDoAn = d.maDoAn
    WHERE ct.maHDDoAn = @maHDDoAn;

    -- Trả về tổng thành tiền của hóa đơn
    SELECT SUM(thanhTien) as TongThanhTien
    FROM HoaDonDoAn
    WHERE maHDDoAn = @maHDDoAn;
END
GO

-- Stored procedure tính thành tiền game
DROP PROCEDURE IF EXISTS sp_TinhThanhTienGame;
GO

CREATE PROCEDURE sp_TinhThanhTienGame
    @maHDGame CHAR(6)
AS
BEGIN
    UPDATE HoaDonGame
    SET thanhTien = ct.soLuong * g.giaGame
    FROM HoaDonGame ct
    INNER JOIN Game g ON ct.maGame = g.maGame
    WHERE ct.maHDGame = @maHDGame;

    -- Trả về tổng thành tiền của hóa đơn
    SELECT SUM(thanhTien) as TongThanhTien
    FROM HoaDonGame
    WHERE maHDGame = @maHDGame;
END
GO

-- Stored procedure tạo hóa đơn tạm (giỏ hàng)
DROP PROCEDURE IF EXISTS sp_TaoHoaDonTam;
GO

CREATE PROCEDURE sp_TaoHoaDonTam
    @maKhachHang CHAR(6)
AS
BEGIN
    DECLARE @maHoaDon CHAR(6)
    
    -- Tạo mã hóa đơn mới
    SELECT @maHoaDon = 'HD' + RIGHT('0000' + CAST(ISNULL(MAX(CAST(SUBSTRING(maHoaDon, 3, 4) AS INT)), 0) + 1 AS VARCHAR(4)), 4)
    FROM HoaDon
    
    -- Tạo hóa đơn mới với trạng thái tạm
    INSERT INTO HoaDon (maHoaDon, maKhachHang, ngay, trangThai)
    VALUES (@maHoaDon, @maKhachHang, GETDATE(), N'Tạm')
    
    -- Trả về mã hóa đơn
    SELECT @maHoaDon AS maHoaDon
END
GO

-- Stored procedure cập nhật số lượng trong giỏ
DROP PROCEDURE IF EXISTS sp_CapNhatSoLuongGio;
GO

CREATE PROCEDURE sp_CapNhatSoLuongGio
    @maKhachHang CHAR(6),
    @maItem CHAR(6),
    @loai NVARCHAR(10),
    @soLuong INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        DECLARE @maHoaDon CHAR(6)
        -- Kiểm tra hóa đơn phiên thuê đang mở của khách hàng
        SELECT @maHoaDon = maHoaDon 
        FROM HoaDon 
        WHERE maKhachHang = @maKhachHang AND trangThai IN (N'Tạm', N'Đã đặt')

        IF @maHoaDon IS NULL
        BEGIN
            SELECT N'Error: Khách hàng chưa có phiên thuê máy đang mở' AS Result
            RETURN
        END

        IF @loai = 'food'
        BEGIN
            IF @soLuong > 0
            BEGIN
                MERGE HoaDonDoAn AS target
                USING (SELECT @maHoaDon AS maHDDoAn, @maItem AS maDoAn, @soLuong AS soLuong, 
                              giaDoAn AS donGia, @soLuong * giaDoAn AS thanhTien
                       FROM DoAn WHERE maDoAn = @maItem) AS source
                ON (target.maHDDoAn = source.maHDDoAn AND target.maDoAn = source.maDoAn)
                WHEN MATCHED THEN
                    UPDATE SET soLuong = source.soLuong,
                               thanhTien = source.thanhTien
                WHEN NOT MATCHED THEN
                    INSERT (maHDDoAn, maDoAn, soLuong, donGia, thanhTien)
                    VALUES (source.maHDDoAn, source.maDoAn, source.soLuong, 
                           source.donGia, source.thanhTien);
            END
            ELSE
            BEGIN
                DELETE FROM HoaDonDoAn
                WHERE maHDDoAn = @maHoaDon AND maDoAn = @maItem
            END
        END

        -- Cập nhật tổng tiền hóa đơn theo tiền đồ ăn + tiền máy đã có
        UPDATE HoaDon
        SET tongTienDoAn = ISNULL((SELECT SUM(thanhTien) FROM HoaDonDoAn WHERE maHDDoAn = @maHoaDon), 0),
            tongTienGame = 0,
            tongTien = ISNULL((SELECT SUM(thanhTien) FROM HoaDonDoAn WHERE maHDDoAn = @maHoaDon), 0) + ISNULL(tienMay, 0),
            ngay = GETDATE()
        WHERE maHoaDon = @maHoaDon

        SELECT 'Success' AS Result, @maHoaDon AS MaHoaDon
    END TRY
    BEGIN CATCH
        SELECT 'Error: ' + ERROR_MESSAGE() AS Result
    END CATCH
END
GO

-- Stored procedure xác nhận đặt hàng
DROP PROCEDURE IF EXISTS sp_XacNhanDatHang;
GO

CREATE PROCEDURE sp_XacNhanDatHang
    @MaHoaDon CHAR(6)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        -- Kiểm tra hóa đơn tồn tại
        IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon = @MaHoaDon)
        BEGIN
            SELECT 'Error: Hóa đơn không tồn tại' AS Result
            RETURN
        END

        BEGIN TRANSACTION
            -- Cập nhật tổng tiền đồ ăn
            UPDATE HoaDon
            SET tongTienDoAn = ISNULL((
                SELECT SUM(thanhTien)
                FROM HoaDonDoAn
                WHERE maHDDoAn = @MaHoaDon
            ), 0)
            WHERE maHoaDon = @MaHoaDon

            -- Cập nhật tổng tiền game
            UPDATE HoaDon
            SET tongTienGame = ISNULL((
                SELECT SUM(thanhTien)
                FROM HoaDonGame
                WHERE maHDGame = @MaHoaDon
            ), 0)
            WHERE maHoaDon = @MaHoaDon

            -- Cập nhật tổng tiền và trạng thái
            UPDATE HoaDon
            SET tongTien = ISNULL(tongTienDoAn, 0) + ISNULL(tongTienGame, 0),
                trangThai = N'Đã đặt',
                ngayDat = GETDATE(),
                maNhanVien = NULL  -- Đặt NULL cho đơn hàng từ web
            WHERE maHoaDon = @MaHoaDon

        COMMIT TRANSACTION
        SELECT 'Success' AS Result
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION
        SELECT 'Error: ' + ERROR_MESSAGE() AS Result
    END CATCH
END
GO

-- Stored procedure thêm đồ ăn vào giỏ hàng
DROP PROCEDURE IF EXISTS sp_ThemDoAnVaoGio;
GO

CREATE PROCEDURE sp_ThemDoAnVaoGio
    @maHoaDon CHAR(6),
    @maDoAn CHAR(6),
    @soLuong INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        -- Kiểm tra xem đã có trong giỏ chưa
        IF EXISTS (SELECT 1 FROM HoaDonDoAn WHERE maHDDoAn = @maHoaDon AND maDoAn = @maDoAn)
        BEGIN
            -- Nếu có rồi thì cập nhật số lượng
            UPDATE HoaDonDoAn
            SET soLuong = soLuong + @soLuong,
                thanhTien = (soLuong + @soLuong) * donGia
            WHERE maHDDoAn = @maHoaDon AND maDoAn = @maDoAn
        END
        ELSE
        BEGIN
            -- Nếu chưa có thì thêm mới
            INSERT INTO HoaDonDoAn (maHDDoAn, maDoAn, soLuong, donGia, thanhTien)
            SELECT @maHoaDon, @maDoAn, @soLuong, giaDoAn, @soLuong * giaDoAn
            FROM DoAn WHERE maDoAn = @maDoAn
        END
        SELECT 'Success' AS Result
    END TRY
    BEGIN CATCH
        SELECT 'Error: ' + ERROR_MESSAGE() AS Result
    END CATCH
END
GO

-- Stored procedure thêm game vào giỏ hàng
DROP PROCEDURE IF EXISTS sp_ThemGameVaoGio;
GO

CREATE PROCEDURE sp_ThemGameVaoGio
    @maHoaDon CHAR(6),
    @maGame CHAR(6),
    @soLuong INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        -- Kiểm tra xem đã có trong giỏ chưa
        IF EXISTS (SELECT 1 FROM HoaDonGame WHERE maHDGame = @maHoaDon AND maGame = @maGame)
        BEGIN
            -- Nếu có rồi thì cập nhật số lượng
            UPDATE HoaDonGame
            SET soLuong = soLuong + @soLuong,
                thanhTien = (soLuong + @soLuong) * donGia
            WHERE maHDGame = @maHoaDon AND maGame = @maGame
        END
        ELSE
        BEGIN
            -- Nếu chưa có thì thêm mới
            INSERT INTO HoaDonGame (maHDGame, maGame, soLuong, donGia, thanhTien)
            SELECT @maHoaDon, @maGame, @soLuong, giaGame, @soLuong * giaGame
            FROM Game WHERE maGame = @maGame
        END
        SELECT 'Success' AS Result
    END TRY
    BEGIN CATCH
        SELECT 'Error: ' + ERROR_MESSAGE() AS Result
    END CATCH
END
GO

-- Stored Procedure để tạo hóa đơn
GO
CREATE PROCEDURE sp_CreateHoaDon
    @MaKhachHang CHAR(6),
    @MaNhanVien CHAR(6) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @MaHoaDon CHAR(6)
    DECLARE @NgayHienTai DATETIME = GETDATE()
    
    -- Tạo mã hóa đơn mới
    SELECT @MaHoaDon = 'HD' + RIGHT('0000' + CAST((ISNULL(MAX(CAST(RIGHT(maHoaDon, 4) AS INT)), 0) + 1) AS VARCHAR(4)), 4)
    FROM HoaDon
    
    -- Thêm hóa đơn mới
    INSERT INTO HoaDon (maHoaDon, maNhanVien, maKhachHang, ngay, ngayDat)
    VALUES (@MaHoaDon, @MaNhanVien, @MaKhachHang, CAST(@NgayHienTai AS DATE), @NgayHienTai)
    
    -- Trả về mã hóa đơn vừa tạo
    SELECT @MaHoaDon AS MaHoaDon
END
GO

-- Stored Procedure để thêm hóa đơn game
CREATE PROCEDURE sp_ThemHoaDonGame
    @MaHoaDon CHAR(6),
    @MaGame CHAR(6),
    @SoLuong INT
AS
BEGIN
    SET NOCOUNT ON;
    
    INSERT INTO HoaDonGame (maHDGame, maGame, soLuong)
    VALUES (@MaHoaDon, @MaGame, @SoLuong)
END
GO

-- Stored Procedure để thêm hóa đơn đồ ăn
CREATE PROCEDURE sp_ThemHoaDonDoAn
    @MaHoaDon CHAR(6),
    @MaDoAn CHAR(6),
    @SoLuong INT
AS
BEGIN
    SET NOCOUNT ON;
    
    INSERT INTO HoaDonDoAn (maHDDoAn, maDoAn, soLuong)
    VALUES (@MaHoaDon, @MaDoAn, @SoLuong)
END
GO

-- Stored Procedure để lấy tổng tiền của giỏ hàng
DROP PROCEDURE IF EXISTS sp_LayTongTien;
GO

CREATE PROCEDURE sp_LayTongTien
    @MaHoaDon CHAR(6)
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @TongTien DECIMAL(10,2);
    
    -- Tính tổng tiền từ đồ ăn
    SELECT @TongTien = ISNULL(SUM(thanhTien), 0)
    FROM HoaDonDoAn
    WHERE maHDDoAn = @MaHoaDon;
    
    -- Cộng thêm tổng tiền từ game
    SELECT @TongTien = @TongTien + ISNULL(SUM(thanhTien), 0)
    FROM HoaDonGame
    WHERE maHDGame = @MaHoaDon;
    
    -- Trả về tổng tiền
    SELECT @TongTien AS TongTien;
END
GO

CREATE PROCEDURE sp_CapNhatSoLuongDoAn
    @MaHDDoAn CHAR(6),
    @MaDoAn CHAR(6),
    @SoLuong INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        IF @SoLuong > 0
        BEGIN
            UPDATE HoaDonDoAn
            SET SoLuong = @SoLuong,
                ThanhTien = @SoLuong * DonGia
            WHERE MaHDDoAn = @MaHDDoAn 
            AND MaDoAn = @MaDoAn;

            -- Cập nhật tổng tiền trong HoaDon
            UPDATE HoaDon
            SET tongTienDoAn = (SELECT SUM(ThanhTien) FROM HoaDonDoAn WHERE MaHDDoAn = @MaHDDoAn),
                tongTien = (SELECT SUM(ThanhTien) FROM HoaDonDoAn WHERE MaHDDoAn = @MaHDDoAn) + 
                          ISNULL((SELECT SUM(ThanhTien) FROM HoaDonGame WHERE MaHDGame = @MaHDDoAn), 0)
            WHERE MaHoaDon = @MaHDDoAn;
        END
        SELECT 'Success' AS Result;
    END TRY
    BEGIN CATCH
        SELECT 'Error: ' + ERROR_MESSAGE() AS Result;
    END CATCH
END
GO

-- Thêm stored procedure để xóa món ăn khỏi hóa đơn
CREATE PROCEDURE sp_XoaDoAnKhoiHoaDon
    @MaHDDoAn CHAR(6),
    @MaDoAn CHAR(6)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        -- Xóa món ăn khỏi HoaDonDoAn
        DELETE FROM HoaDonDoAn
        WHERE MaHDDoAn = @MaHDDoAn 
        AND MaDoAn = @MaDoAn;

        -- Cập nhật tổng tiền trong HoaDon
        UPDATE HoaDon
        SET tongTienDoAn = ISNULL((SELECT SUM(ThanhTien) FROM HoaDonDoAn WHERE MaHDDoAn = @MaHDDoAn), 0),
            tongTien = ISNULL((SELECT SUM(ThanhTien) FROM HoaDonDoAn WHERE MaHDDoAn = @MaHDDoAn), 0) + 
                      ISNULL((SELECT SUM(ThanhTien) FROM HoaDonGame WHERE MaHDGame = @MaHDDoAn), 0)
        WHERE MaHoaDon = @MaHDDoAn;

        SELECT 'Success' AS Result;
    END TRY
    BEGIN CATCH
        SELECT 'Error: ' + ERROR_MESSAGE() AS Result;
    END CATCH
END
GO

IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ThemMonAnTheoKhachHang')
    DROP PROCEDURE sp_ThemMonAnTheoKhachHang
GO

CREATE PROCEDURE sp_ThemMonAnTheoKhachHang
    @maKhachHang CHAR(6),
    @maDoAn CHAR(6),
    @soLuong INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
            DECLARE @maHoaDon CHAR(6)
            
            -- Kiểm tra xem khách hàng có hóa đơn nào chưa thanh toán không
            SELECT TOP 1 @maHoaDon = maHoaDon
            FROM HoaDon 
            WHERE maKhachHang = @maKhachHang 
            AND trangThai NOT IN (N'Đã thanh toán')
            ORDER BY ngayDat DESC

            -- Nếu không có hóa đơn chưa thanh toán, tạo hóa đơn mới
            IF @maHoaDon IS NULL
            BEGIN
                -- Tạo mã hóa đơn mới
                SELECT @maHoaDon = 'HD' + RIGHT('0000' + 
                    CAST(ISNULL(MAX(CAST(SUBSTRING(maHoaDon, 3, 4) AS INT)), 0) + 1 AS VARCHAR(4)), 4)
                FROM HoaDon

                -- Tạo hóa đơn mới
                INSERT INTO HoaDon (maHoaDon, maKhachHang, ngay, ngayDat, trangThai, tongTienDoAn, tongTien)
                VALUES (@maHoaDon, @maKhachHang, GETDATE(), GETDATE(), N'Tạm', 0, 0)
            END

            -- Thêm hoặc cập nhật món ăn vào hóa đơn
            MERGE HoaDonDoAn AS target
            USING (SELECT @maHoaDon AS maHDDoAn, @maDoAn AS maDoAn, @soLuong AS soLuong, 
                        giaDoAn AS donGia, @soLuong * giaDoAn AS thanhTien
                    FROM DoAn WHERE maDoAn = @maDoAn) AS source
            ON (target.maHDDoAn = source.maHDDoAn AND target.maDoAn = source.maDoAn)
            WHEN MATCHED THEN
                UPDATE SET soLuong = target.soLuong + source.soLuong,
                        donGia = source.donGia,
                        thanhTien = (target.soLuong + source.soLuong) * source.donGia
            WHEN NOT MATCHED THEN
                INSERT (maHDDoAn, maDoAn, soLuong, donGia, thanhTien)
                VALUES (source.maHDDoAn, source.maDoAn, source.soLuong, source.donGia, source.thanhTien);

            -- Cập nhật tổng tiền trong HoaDon
            UPDATE HoaDon
            SET tongTienDoAn = (SELECT ISNULL(SUM(thanhTien), 0) FROM HoaDonDoAn WHERE maHDDoAn = @maHoaDon),
                tongTien = (SELECT ISNULL(SUM(thanhTien), 0) FROM HoaDonDoAn WHERE maHDDoAn = @maHoaDon) + 
                          ISNULL((SELECT SUM(thanhTien) FROM HoaDonGame WHERE maHDGame = @maHoaDon), 0)
            WHERE maHoaDon = @maHoaDon;

            -- Trả về mã hóa đơn để client có thể sử dụng
            SELECT @maHoaDon AS MaHoaDon;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        SELECT 'Error: ' + ERROR_MESSAGE() AS Result;
    END CATCH
END
GO

-- Stored procedure để kiểm tra và lấy hóa đơn tạm của khách hàng
CREATE PROCEDURE sp_KiemTraHoaDonTam
    @maKhachHang CHAR(6)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        -- Kiểm tra xem khách hàng có hóa đơn tạm nào trong ngày không
        DECLARE @maHoaDon CHAR(6)
        
        SELECT TOP 1 @maHoaDon = maHoaDon
        FROM HoaDon 
        WHERE maKhachHang = @maKhachHang 
        AND CAST(ngayDat AS DATE) = CAST(GETDATE() AS DATE)
        AND trangThai = N'Tạm'
        ORDER BY ngayDat DESC

        IF @maHoaDon IS NOT NULL
        BEGIN
            -- Trả về thông tin hóa đơn tạm và chi tiết
            SELECT 
                hd.maHoaDon,
                hd.maKhachHang,
                hd.ngayDat,
                hd.tongTienDoAn,
                hd.tongTien,
                hdda.maDoAn,
                da.tenDoAn,
                hdda.soLuong,
                hdda.donGia,
                hdda.thanhTien
            FROM HoaDon hd
            LEFT JOIN HoaDonDoAn hdda ON hd.maHoaDon = hdda.maHDDoAn
            LEFT JOIN DoAn da ON hdda.maDoAn = da.maDoAn
            WHERE hd.maHoaDon = @maHoaDon
        END
        ELSE
        BEGIN
            -- Trả về null nếu không có hóa đơn tạm
            SELECT NULL AS maHoaDon
        END
    END TRY
    BEGIN CATCH
        SELECT 'Error: ' + ERROR_MESSAGE() AS Result;
    END CATCH
END
GO

-- Stored procedure để cập nhật trạng thái hóa đơn
CREATE PROCEDURE sp_CapNhatTrangThaiHoaDon
    @maHoaDon CHAR(6),
    @trangThai NVARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        UPDATE HoaDon
        SET trangThai = @trangThai,
            ngayDat = CASE 
                WHEN @trangThai = N'Đã đặt' THEN GETDATE()
                ELSE ngayDat
            END
        WHERE maHoaDon = @maHoaDon

        SELECT 'Success' AS Result
    END TRY
    BEGIN CATCH
        SELECT 'Error: ' + ERROR_MESSAGE() AS Result
    END CATCH
END
GO

GO
-- Stored procedure để xóa toàn bộ giỏ hàng
CREATE PROCEDURE sp_XoaGioHang
    @maKhachHang CHAR(6)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
            -- Tìm hóa đơn tạm của khách hàng trong ngày
            DECLARE @maHoaDon CHAR(6)
            SELECT TOP 1 @maHoaDon = maHoaDon
            FROM HoaDon 
            WHERE maKhachHang = @maKhachHang 
            AND CAST(ngayDat AS DATE) = CAST(GETDATE() AS DATE)
            AND trangThai = N'Tạm'
            ORDER BY ngayDat DESC

            IF @maHoaDon IS NOT NULL
            BEGIN
                -- Xóa tất cả các món trong hóa đơn tạm
                DELETE FROM HoaDonDoAn WHERE maHDDoAn = @maHoaDon
                
                -- Xóa hóa đơn tạm
                DELETE FROM HoaDon WHERE maHoaDon = @maHoaDon
                
                SELECT 'Success' AS Result
            END
            ELSE
            BEGIN
                SELECT 'No cart found' AS Result
            END
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SELECT 'Error: ' + ERROR_MESSAGE() AS Result;
    END CATCH
END

-- Stored procedure để xử lý đặt món từ web và tạo lịch sử sử dụng
DROP PROCEDURE IF EXISTS sp_DatMonTuWeb;
GO

CREATE PROCEDURE sp_DatMonTuWeb
    @maKhachHang CHAR(6),
    @maHoaDon CHAR(6)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
            DECLARE @maSuDung CHAR(6)
            
            -- Tạo mã sử dụng mới cho lần đặt này
            SELECT @maSuDung = 'LS' + RIGHT('0000' + 
                CAST(ISNULL(MAX(CAST(SUBSTRING(maSuDung, 3, 4) AS INT)), 0) + 1 AS VARCHAR(4)), 4)
            FROM LichSuSuDung;

            -- Lấy thời gian đặt từ hóa đơn
            DECLARE @thoiGianDat DATETIME
            SELECT @thoiGianDat = ngayDat
            FROM HoaDon
            WHERE maHoaDon = @maHoaDon;

            -- Thêm lịch sử sử dụng mới với thời gian đặt của hóa đơn
            INSERT INTO LichSuSuDung (maSuDung, maKhachHang, thoiGianVao)
            VALUES (@maSuDung, @maKhachHang, @thoiGianDat);

            -- Cập nhật mã sử dụng vào hóa đơn và đánh dấu đã đặt
            UPDATE HoaDon
            SET maSuDung = @maSuDung,
                trangThai = N'Đã đặt'
            WHERE maHoaDon = @maHoaDon
            AND maKhachHang = @maKhachHang;

            -- Trả về thông tin để client có thể sử dụng
            SELECT 
                @maSuDung AS MaSuDung,
                'Success' AS Result,
                @thoiGianDat AS ThoiGianDat,
                @maHoaDon AS MaHoaDon;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        SELECT ERROR_MESSAGE() AS Result;
    END CATCH
END
GO

-- Stored procedure để lấy thông tin lịch sử sử dụng và hóa đơn của khách hàng
DROP PROCEDURE IF EXISTS sp_GetLichSuSuDungKhachHang;
GO

CREATE PROCEDURE sp_GetLichSuSuDungKhachHang
    @maKhachHang CHAR(6),
    @pageNumber INT = 1,
    @pageSize INT = 10
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tính vị trí bắt đầu
    DECLARE @Offset INT = (@pageNumber - 1) * @pageSize;
    
    -- Lấy tổng số bản ghi
    SELECT COUNT(*) AS TotalRecords
    FROM LichSuSuDung lssd 
    WHERE lssd.maKhachHang = @maKhachHang;
    
    -- Lấy danh sách lịch sử sử dụng và thông tin hóa đơn
    SELECT 
        lssd.maSuDung,
        lssd.maMay,
        kv.tenKhu,
        lm.tenLoaiMay,
        lssd.thoiGianVao,
        lssd.thoiGianRa,
        hd.maHoaDon,
        hd.tongTienDoAn,
        hd.tongTienGame,
        hd.tongTien,
        hd.trangThai
    FROM LichSuSuDung lssd
    LEFT JOIN MayTinh mt ON lssd.maMay = mt.maMay
    LEFT JOIN KhuVuc kv ON mt.maKhu = kv.maKhu
    LEFT JOIN LoaiMay lm ON mt.maLoaiMay = lm.maLoaiMay
    LEFT JOIN HoaDon hd ON lssd.maSuDung = hd.maSuDung
    WHERE lssd.maKhachHang = @maKhachHang
    ORDER BY lssd.thoiGianVao DESC
    OFFSET @Offset ROWS
    FETCH NEXT @pageSize ROWS ONLY;

    -- Lấy chi tiết đồ ăn của hóa đơn mới nhất
    SELECT 
        hd.maHoaDon,
        da.tenDoAn,
        hdda.soLuong,
        hdda.donGia,
        hdda.thanhTien
    FROM LichSuSuDung lssd
    JOIN HoaDon hd ON lssd.maSuDung = hd.maSuDung
    JOIN HoaDonDoAn hdda ON hd.maHoaDon = hdda.maHDDoAn
    JOIN DoAn da ON hdda.maDoAn = da.maDoAn
    WHERE lssd.maKhachHang = @maKhachHang
    AND lssd.maSuDung = (
        SELECT TOP 1 maSuDung 
        FROM LichSuSuDung 
        WHERE maKhachHang = @maKhachHang 
        ORDER BY thoiGianVao DESC
    );

    -- Lấy chi tiết game của hóa đơn mới nhất
    SELECT 
        hd.maHoaDon,
        g.tenGame,
        hdg.soLuong,
        hdg.donGia,
        hdg.thanhTien
    FROM LichSuSuDung lssd
    JOIN HoaDon hd ON lssd.maSuDung = hd.maSuDung
    JOIN HoaDonGame hdg ON hd.maHoaDon = hdg.maHDGame
    JOIN Game g ON hdg.maGame = g.maGame
    WHERE lssd.maKhachHang = @maKhachHang
    AND lssd.maSuDung = (
        SELECT TOP 1 maSuDung 
        FROM LichSuSuDung 
        WHERE maKhachHang = @maKhachHang 
        ORDER BY thoiGianVao DESC
    );
END
GO

DROP PROCEDURE IF EXISTS sp_BatDauThueMay;
GO
CREATE PROCEDURE sp_BatDauThueMay
    @maMay CHAR(6),
    @maKhachHang CHAR(6),
    @maNhanVien CHAR(6) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
            IF NOT EXISTS (SELECT 1 FROM MayTinh WHERE maMay = @maMay)
            BEGIN
                SELECT N'Error: Máy không tồn tại' AS Result;
                ROLLBACK TRANSACTION;
                RETURN;
            END

            IF EXISTS (SELECT 1 FROM MayTinh WHERE maMay = @maMay AND trangThai IN (N'Đang thuê', N'Đang hư'))
            BEGIN
                SELECT N'Error: Máy không sẵn sàng để thuê' AS Result;
                ROLLBACK TRANSACTION;
                RETURN;
            END

            IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE maKhachHang = @maKhachHang AND trangThaiTaiKhoan = N'Đang hoạt động')
            BEGIN
                SELECT N'Error: Tài khoản khách hàng chưa được xác nhận hoặc đã khóa' AS Result;
                ROLLBACK TRANSACTION;
                RETURN;
            END

            DECLARE @maSuDung CHAR(6), @maHoaDon CHAR(6);
            SELECT @maSuDung = 'LS' + RIGHT('0000' + CAST(ISNULL(MAX(CAST(SUBSTRING(maSuDung, 3, 4) AS INT)), 0) + 1 AS VARCHAR(4)), 4)
            FROM LichSuSuDung;
            SELECT @maHoaDon = 'HD' + RIGHT('0000' + CAST(ISNULL(MAX(CAST(SUBSTRING(maHoaDon, 3, 4) AS INT)), 0) + 1 AS VARCHAR(4)), 4)
            FROM HoaDon;

            INSERT INTO LichSuSuDung (maSuDung, maKhachHang, maMay, thoiGianVao)
            VALUES (@maSuDung, @maKhachHang, @maMay, GETDATE());

            INSERT INTO HoaDon (maHoaDon, maNhanVien, maKhachHang, maSuDung, ngay, ngayDat, soGioChoi, tongTienDoAn, tongTienGame, tienMay, tongTien, trangThai)
            VALUES (@maHoaDon, @maNhanVien, @maKhachHang, @maSuDung, CAST(GETDATE() AS DATE), GETDATE(), 0, 0, 0, 0, 0, N'Đã đặt');

            UPDATE MayTinh
            SET trangThai = N'Đang thuê',
                maKhachHang = @maKhachHang
            WHERE maMay = @maMay;
        COMMIT TRANSACTION;
        SELECT N'Success' AS Result, @maHoaDon AS MaHoaDon, @maSuDung AS MaSuDung;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        SELECT N'Error: ' + ERROR_MESSAGE() AS Result;
    END CATCH
END
GO

DROP PROCEDURE IF EXISTS sp_KetThucThueMay;
GO
CREATE PROCEDURE sp_KetThucThueMay
    @maMay CHAR(6),
    @maNhanVien CHAR(6) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
            DECLARE @maSuDung CHAR(6), @maHoaDon CHAR(6), @giaGio DECIMAL(10,2);
            SELECT TOP 1
                @maSuDung = l.maSuDung,
                @maHoaDon = h.maHoaDon,
                @giaGio = m.giaGio
            FROM LichSuSuDung l
            JOIN MayTinh m ON l.maMay = m.maMay
            LEFT JOIN HoaDon h ON h.maSuDung = l.maSuDung
            WHERE l.maMay = @maMay AND l.thoiGianRa IS NULL
            ORDER BY l.thoiGianVao DESC;

            IF @maSuDung IS NULL OR @maHoaDon IS NULL
            BEGIN
                SELECT N'Error: Không tìm thấy phiên thuê đang mở của máy' AS Result;
                ROLLBACK TRANSACTION;
                RETURN;
            END

            UPDATE LichSuSuDung
            SET thoiGianRa = GETDATE()
            WHERE maSuDung = @maSuDung;

            DECLARE @soPhut INT, @soGioLamTron INT, @tienMay DECIMAL(10,2), @tongDoAn DECIMAL(10,2);
            SELECT @soPhut = DATEDIFF(MINUTE, thoiGianVao, thoiGianRa)
            FROM LichSuSuDung
            WHERE maSuDung = @maSuDung;

            SET @soPhut = CASE WHEN @soPhut IS NULL OR @soPhut < 1 THEN 1 ELSE @soPhut END;
            SET @soGioLamTron = CEILING(@soPhut / 60.0);
            SET @tienMay = (@soPhut / 60.0) * ISNULL(@giaGio, 0);
            SET @tongDoAn = ISNULL((SELECT SUM(thanhTien) FROM HoaDonDoAn WHERE maHDDoAn = @maHoaDon), 0);

            UPDATE HoaDon
            SET maNhanVien = COALESCE(@maNhanVien, maNhanVien),
                ngay = CAST(GETDATE() AS DATE),
                soGioChoi = @soGioLamTron,
                tongTienDoAn = @tongDoAn,
                tongTienGame = 0,
                tienMay = @tienMay,
                tongTien = @tongDoAn + @tienMay,
                trangThai = N'Đã thanh toán'
            WHERE maHoaDon = @maHoaDon;

            UPDATE MayTinh
            SET trangThai = N'Còn trống',
                maKhachHang = NULL
            WHERE maMay = @maMay;

            UPDATE KhachHang
            SET trangThaiTaiKhoan = N'Đã khóa',
                matKhau = NULL
            WHERE maKhachHang = (SELECT maKhachHang FROM LichSuSuDung WHERE maSuDung = @maSuDung);
        COMMIT TRANSACTION;
        SELECT N'Success' AS Result, @maHoaDon AS MaHoaDon;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        SELECT N'Error: ' + ERROR_MESSAGE() AS Result;
    END CATCH
END
GO


select * from KhachHang
select * from NhanVien