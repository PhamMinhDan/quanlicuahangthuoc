package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
}