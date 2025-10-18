package com.example.quanlicuahangthuoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quanlicuahangthuoc.entity.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
}