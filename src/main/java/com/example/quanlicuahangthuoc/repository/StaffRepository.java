package com.example.quanlicuahangthuoc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quanlicuahangthuoc.entity.Staff;
import java.util.Optional;
@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

  // Tìm kiếm nhân viên theo tên (không phân biệt hoa thường)
    @Query("SELECT s FROM Staff s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Staff> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
  @Query("SELECT COUNT(s) FROM Staff s WHERE " +
          "(:name IS NULL OR :name = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
          "(:role IS NULL OR s.role = :role)")
  long countByFilters(@Param("name") String name, @Param("role") Staff.Role role);
    // Tìm kiếm nhân viên theo chức vụ
    Page<Staff> findByRole(Staff.Role role, Pageable pageable);
    
    // Tìm kiếm nhân viên theo cả tên và chức vụ
    @Query("SELECT s FROM Staff s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) AND s.role = :role")
    Page<Staff> findByNameContainingIgnoreCaseAndRole(@Param("name") String name, @Param("role") Staff.Role role, Pageable pageable);

    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByPhone(String phone);
  long countByWorkShift(Staff.WorkShift workShift);
  long countByRole(Staff.Role role);
}