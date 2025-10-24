-- ============================================
-- FIX CONSTRAINTS CHO ORDER_DETAIL
-- ============================================

-- Bước 1: Kiểm tra cấu trúc hiện tại
SELECT 'Cấu trúc hiện tại:' as info;
DESCRIBE order_detail;

-- Bước 2: ALTER TABLE để đảm bảo constraints đúng
-- staff_id: NOT NULL (bắt buộc - người bán hàng)
-- promo_id: NULL (tùy chọn - không phải đơn nào cũng có khuyến mãi)

ALTER TABLE order_detail 
MODIFY COLUMN staff_id INT NOT NULL;

ALTER TABLE order_detail 
MODIFY COLUMN promo_id INT NULL;

-- Bước 3: Kiểm tra lại
SELECT 'Cấu trúc sau khi fix:' as info;
DESCRIBE order_detail;

-- Bước 4: Kiểm tra dữ liệu
SELECT 'Kiểm tra dữ liệu:' as info;
SELECT 
    'Tổng order_detail' as type,
    COUNT(*) as count
FROM order_detail

UNION ALL

SELECT 
    'Có staff_id NULL',
    COUNT(*)
FROM order_detail
WHERE staff_id IS NULL

UNION ALL

SELECT 
    'Có promo_id NULL',
    COUNT(*)
FROM order_detail
WHERE promo_id IS NULL;

