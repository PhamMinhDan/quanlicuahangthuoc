package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByPhone(String phone);
}