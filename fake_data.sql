-- Tạo dữ liệu fake cho hệ thống quản lý cửa hàng thuốc

-- 1. Tạo dữ liệu cho bảng Staff (Nhân viên)
INSERT INTO staff (name, email, phone, role, salary, work_shift) VALUES
('Nguyễn Văn An', 'an.nguyen@pharmacy.com', '0901234567', 'quan_ly', 15000000.00, 'sang'),
('Trần Thị Bình', 'binh.tran@pharmacy.com', '0901234568', 'nhan_vien', 8000000.00, 'sang'),
('Lê Hoàng Cường', 'cuong.le@pharmacy.com', '0901234569', 'nhan_vien', 8500000.00, 'chieu'),
('Phạm Thị Dung', 'dung.pham@pharmacy.com', '0901234570', 'nhan_vien', 9000000.00, 'sang'),
('Võ Minh Em', 'em.vo@pharmacy.com', '0901234571', 'nhan_vien', 8200000.00, 'chieu');

-- 2. Tạo dữ liệu cho bảng Customer (Khách hàng)
INSERT INTO customer (name, email, phone, customer_type, reward_points) VALUES
('Nguyễn Thị Hoa', 'hoa.nguyen@gmail.com', '0987654321', 'than_thiet', 150),
('Trần Văn Minh', 'minh.tran@gmail.com', '0987654322', 'vang_lai', 0),
('Lê Thị Lan', 'lan.le@gmail.com', '0987654323', 'than_thiet', 85),
('Phạm Hoàng Nam', 'nam.pham@gmail.com', '0987654324', 'vang_lai', 0),
('Võ Thị Oanh', 'oanh.vo@gmail.com', '0987654325', 'than_thiet', 220),
('Hoàng Văn Phong', 'phong.hoang@gmail.com', '0987654326', 'vang_lai', 0),
('Đặng Thị Quỳnh', 'quynh.dang@gmail.com', '0987654327', 'than_thiet', 95),
('Bùi Văn Sơn', 'son.bui@gmail.com', '0987654328', 'vang_lai', 0),
('Lý Thị Tâm', 'tam.ly@gmail.com', '0987654329', 'than_thiet', 310),
('Ngô Văn Ước', 'uoc.ngo@gmail.com', '0987654330', 'vang_lai', 0);

-- 3. Tạo dữ liệu cho bảng Medicine (Thuốc)
INSERT INTO medicine (name, type, expiry_date, price, stock_quantity, supplier, image) VALUES
('Paracetamol 500mg', 'giam_dau', '2025-12-31', 5000.00, 500, 'Pfizer', 'paracetamol.jpg'),
('Amoxicillin 250mg', 'khang_sinh', '2025-11-30', 25000.00, 200, 'Novartis', 'amoxicillin.jpg'),
('Ibuprofen 400mg', 'chong_viem', '2025-10-31', 15000.00, 300, 'Johnson', 'ibuprofen.jpg'),
('Amlodipine 5mg', 'thuoc_ha_huyet_ap', '2026-01-15', 35000.00, 150, 'Roche', 'amlodipine.jpg'),
('Aspirin 100mg', 'giam_dau', '2025-09-30', 8000.00, 400, 'Merck', 'aspirin.jpg');

-- 4. Tạo dữ liệu cho bảng Promotion (Khuyến mãi)
INSERT INTO promotion (name, type, discount_percent, expired_date) VALUES
('Giảm giá mùa hè', 'Seasonal', 10.00, '2025-08-31'),
('Khuyến mãi sinh nhật', 'Birthday', 15.00, '2025-12-31'),
('Giảm giá cuối tuần', 'Weekend', 5.00, '2025-11-30'),
('Khuyến mãi khách hàng thân thiết', 'Loyalty', 20.00, '2025-12-31'),
('Không có khuyến mãi', 'None', 0.00, '2030-12-31');

-- 5. Tạo dữ liệu cho bảng Order (Đơn hàng)
INSERT INTO order_table (customer_id, staff_id, promo_id, order_date, status, total_amount) VALUES
(1, 1, 1, '2025-10-20', 'da_thanh_toan', 45000.00),
(2, 2, 5, '2025-10-21', 'da_thanh_toan', 30000.00),
(3, 3, 2, '2025-10-21', 'da_thanh_toan', 72250.00),
(4, 1, 5, '2025-10-22', 'da_thanh_toan', 55000.00),
(5, 4, 4, '2025-10-22', 'da_thanh_toan', 96000.00),
(6, 2, 5, '2025-10-22', 'chua_thanh_toan', 25000.00),
(7, 5, 3, '2025-10-22', 'da_thanh_toan', 23750.00),
(8, 3, 5, '2025-10-22', 'da_thanh_toan', 40000.00),
(9, 1, 4, '2025-10-22', 'da_thanh_toan', 64000.00),
(10, 4, 1, '2025-10-22', 'da_thanh_toan', 67500.00);

-- 6. Tạo dữ liệu cho bảng Payment (Thanh toán)
INSERT INTO payment (order_id, payment_method, amount, `change`, payment_date) VALUES
(1, 'tien_mat', 45000.00, 5000.00, '2025-10-20'),
(2, 'chuyen_khoan', 30000.00, 0.00, '2025-10-21'),
(3, 'tien_mat', 72250.00, 7750.00, '2025-10-21'),
(4, 'tien_mat', 55000.00, 15000.00, '2025-10-22'),
(5, 'chuyen_khoan', 96000.00, 0.00, '2025-10-22'),
(7, 'tien_mat', 23750.00, 1250.00, '2025-10-22'),
(8, 'chuyen_khoan', 40000.00, 0.00, '2025-10-22'),
(9, 'tien_mat', 64000.00, 6000.00, '2025-10-22'),
(10, 'tien_mat', 67500.00, 2500.00, '2025-10-22');

-- 7. Tạo dữ liệu cho bảng OrderDetail (Chi tiết đơn hàng)
INSERT INTO order_detail (order_id, medicine_id, quantity, unit_price) VALUES
-- Order 1: Paracetamol x5 + Ibuprofen x2
(1, 1, 5, 5000.00),
(1, 3, 2, 15000.00),

-- Order 2: Amoxicillin x1 + Aspirin x1  
(2, 2, 1, 25000.00),
(2, 5, 1, 8000.00),

-- Order 3: Amlodipine x2 + Paracetamol x3 (có giảm giá 15%)
(3, 4, 2, 35000.00),
(3, 1, 3, 5000.00),

-- Order 4: Ibuprofen x2 + Amoxicillin x1
(4, 3, 2, 15000.00),
(4, 2, 1, 25000.00),

-- Order 5: Amlodipine x2 + Paracetamol x4 + Aspirin x2 (có giảm giá 20%)
(5, 4, 2, 35000.00),
(5, 1, 4, 5000.00),
(5, 5, 2, 8000.00),

-- Order 6: Amoxicillin x1 (chưa thanh toán)
(6, 2, 1, 25000.00),

-- Order 7: Ibuprofen x1 + Aspirin x1 (có giảm giá 5%)
(7, 3, 1, 15000.00),
(7, 5, 1, 8000.00),

-- Order 8: Paracetamol x8
(8, 1, 8, 5000.00),

-- Order 9: Amlodipine x1 + Ibuprofen x2 (có giảm giá 20%)
(9, 4, 1, 35000.00),
(9, 3, 2, 15000.00),

-- Order 10: Amoxicillin x2 + Aspirin x2 (có giảm giá 10%)
(10, 2, 2, 25000.00),
(10, 5, 2, 8000.00);
