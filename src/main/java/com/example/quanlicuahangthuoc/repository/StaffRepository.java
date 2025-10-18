package com.example.quanlicuahangthuoc.repository; // Hoặc package phù hợp

import com.example.quanlicuahangthuoc.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    // Thêm các phương thức tìm kiếm custom nếu cần, ví dụ:
    // Optional<Staff> findByEmail(String email);
    // Optional<Staff> findByPhone(String phone);
}