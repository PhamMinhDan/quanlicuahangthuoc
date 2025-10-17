# 👥 Hướng dẫn Test API Staff

## 📋 **Tổng quan Staff API**

### **Base URL:** `http://localhost:8082/api/staff`

---

## 🎯 **Các API Endpoints chính:**

### **1. Lấy danh sách nhân viên (phân trang + sắp xếp)**
```
GET http://localhost:8082/api/staff?page=0&size=10&sortBy=name&sortDirection=asc
```

### **2. Tạo nhân viên mới**
```
POST http://localhost:8082/api/staff
Content-Type: application/json

{
  "name": "Nguyễn Văn A",
  "role": "quan_ly",
  "workShift": "sang",
  "salary": 15000000.00,
  "email": "nguyenvana@example.com",
  "phone": "0123456789"
}
```

### **3. Cập nhật nhân viên**
```
PUT http://localhost:8082/api/staff/{id}
Content-Type: application/json

{
  "name": "Nguyễn Văn A (Updated)",
  "role": "quan_ly",
  "workShift": "chieu",
  "salary": 18000000.00,
  "email": "nguyenvana@example.com",
  "phone": "0123456789"
}
```

### **4. Xóa nhân viên**
```
DELETE http://localhost:8082/api/staff/{id}
```

### **5. Lấy nhân viên theo ID**
```
GET http://localhost:8082/api/staff/{id}
```

---

## 🔍 **API tìm kiếm và lọc:**

### **6. Tìm nhân viên theo email**
```
GET http://localhost:8082/api/staff/email/nguyenvana@example.com
```

### **7. Tìm nhân viên theo số điện thoại**
```
GET http://localhost:8082/api/staff/phone/0123456789
```

### **8. Lấy nhân viên theo vai trò**
```
GET http://localhost:8082/api/staff/role/quan_ly
GET http://localhost:8082/api/staff/role/nhan_vien
```

### **9. Lấy nhân viên theo ca làm việc**
```
GET http://localhost:8082/api/staff/work-shift/sang
GET http://localhost:8082/api/staff/work-shift/chieu
```

### **10. Tìm kiếm nhân viên theo tên**
```
GET http://localhost:8082/api/staff/search?name=Nguyễn
```

### **11. Lấy nhân viên theo khoảng lương**
```
GET http://localhost:8082/api/staff/salary-range?minSalary=10000000&maxSalary=20000000
```

---

## 📊 **API thống kê:**

### **12. Đếm số nhân viên theo vai trò**
```
GET http://localhost:8082/api/staff/stats/role/quan_ly
GET http://localhost:8082/api/staff/stats/role/nhan_vien
```

### **13. Đếm số nhân viên theo ca làm việc**
```
GET http://localhost:8082/api/staff/stats/work-shift/sang
GET http://localhost:8082/api/staff/stats/work-shift/chieu
```

### **14. Lấy nhân viên có lương cao nhất**
```
GET http://localhost:8082/api/staff/top-salary
```

### **15. Health check**
```
GET http://localhost:8082/api/staff/test
```

---

## 📝 **Cấu trúc dữ liệu Staff:**

```json
{
  "id": 1,
  "name": "Nguyễn Văn A",
  "role": "quan_ly",
  "workShift": "sang",
  "salary": 15000000.00,
  "email": "nguyenvana@example.com",
  "phone": "0123456789"
}
```

---

## 🔄 **Sắp xếp (Sort Parameters):**

| Trường | Giá trị | Mô tả |
|--------|---------|--------|
| `sortBy` | `name`, `salary`, `email`, `role`, `workShift`, `id` | Trường sắp xếp |
| `sortDirection` | `asc`, `desc` | Hướng sắp xếp |

### **Ví dụ sắp xếp:**
```
GET http://localhost:8082/api/staff?sortBy=salary&sortDirection=desc
GET http://localhost:8082/api/staff?sortBy=name&sortDirection=asc
```

---

## 📄 **Phân trang (Pagination Parameters):**

| Parameter | Giá trị mặc định | Mô tả |
|-----------|------------------|--------|
| `page` | `0` | Số trang (bắt đầu từ 0) |
| `size` | `10` | Số bản ghi mỗi trang |

### **Ví dụ phân trang:**
```
GET http://localhost:8082/api/staff?page=0&size=5
GET http://localhost:8082/api/staff?page=1&size=10
```

---

## ✅ **Validation Rules:**

| Trường | Validation | Thông báo lỗi |
|--------|------------|---------------|
| `name` | `@NotBlank`, `@Size(2-255)` | "Tên nhân viên không được để trống" / "Tên nhân viên phải từ 2 đến 255 ký tự" |
| `role` | `@NotNull` | "Vai trò không được để trống" |
| `workShift` | `@NotNull` | "Ca làm việc không được để trống" |
| `salary` | `@NotNull`, `@DecimalMin(0.0)` | "Lương không được để trống" / "Lương phải lớn hơn hoặc bằng 0" |
| `email` | `@NotBlank`, `@Email` | "Email không được để trống" / "Email không hợp lệ" |
| `phone` | `@NotBlank`, `@Pattern(^[0-9]{10,15}$)` | "Số điện thoại không được để trống" / "Số điện thoại phải từ 10 đến 15 chữ số" |

---

## 🧪 **Test Cases:**

### **Test Case 1: Tạo nhân viên hợp lệ**
```json
POST http://localhost:8082/api/staff
{
  "name": "Trần Thị B",
  "role": "nhan_vien",
  "workShift": "chieu",
  "salary": 8000000.00,
  "email": "tranthib@example.com",
  "phone": "0987654321"
}
```
**Kết quả mong đợi:** Status 201 - Nhân viên được tạo thành công

### **Test Case 2: Validation lỗi**
```json
POST http://localhost:8082/api/staff
{
  "name": "",
  "role": null,
  "workShift": null,
  "salary": -1000.00,
  "email": "invalid-email",
  "phone": "123"
}
```
**Kết quả mong đợi:** Status 400 - Nhiều thông báo lỗi validation

### **Test Case 3: Email trùng lặp**
```json
POST http://localhost:8082/api/staff
{
  "name": "Người khác",
  "role": "nhan_vien",
  "workShift": "sang",
  "salary": 7000000.00,
  "email": "nguyenvana@example.com",
  "phone": "0999888777"
}
```
**Kết quả mong đợi:** Status 400 - "Email đã tồn tại"

### **Test Case 4: Phân trang + Sắp xếp**
```
GET http://localhost:8082/api/staff?page=0&size=5&sortBy=salary&sortDirection=desc
```
**Kết quả mong đợi:** Status 200 - Trang đầu tiên, 5 nhân viên, sắp xếp theo lương giảm dần

---

## 🎯 **Enum Values:**

### **Role (Vai trò):**
- `quan_ly` - Quản lý
- `nhan_vien` - Nhân viên

### **WorkShift (Ca làm việc):**
- `sang` - Ca sáng
- `chieu` - Ca chiều

---

## 📊 **Response Structure cho phân trang:**

```json
{
  "content": [...],           // Dữ liệu nhân viên
  "totalElements": 25,        // Tổng số nhân viên
  "totalPages": 3,            // Tổng số trang
  "number": 0,                // Trang hiện tại
  "size": 10,                 // Kích thước trang
  "first": true,              // Trang đầu?
  "last": false,              // Trang cuối?
  "numberOfElements": 10      // Số nhân viên trong trang hiện tại
}
```

---

## 🚀 **Sẵn sàng để test!**

Tất cả API endpoints đã được tạo với đầy đủ tính năng:
- ✅ CRUD operations
- ✅ Validation đầy đủ
- ✅ Phân trang và sắp xếp
- ✅ Tìm kiếm và lọc
- ✅ Thống kê
- ✅ Xử lý lỗi

Bạn có thể bắt đầu test ngay trong Postman! 🎉
