package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {

    // Phân trang với sắp xếp
    Page<Medicine> findAll(Pageable pageable);

    // Tìm kiếm theo tên thuốc (không phân biệt hoa thường)
    List<Medicine> findByNameContainingIgnoreCase(String name);

    // Tìm kiếm theo tên thuốc chính xác để kiểm tra trùng lặp
    Optional<Medicine> findByName(String name);

    // Tìm kiếm theo loại thuốc
    List<Medicine> findByType(Medicine.MedicineType type);

    // Tìm kiếm theo nhà cung cấp
    List<Medicine> findBySupplier(Medicine.Supplier supplier);

    // Tìm kiếm theo nhiều điều kiện
    @Query("SELECT m FROM Medicine m WHERE " +
           "(:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:type IS NULL OR m.type = :type) AND " +
           "(:supplier IS NULL OR m.supplier = :supplier)")
    List<Medicine> findByFilters(@Param("name") String name,
                                 @Param("type") Medicine.MedicineType type,
                                 @Param("supplier") Medicine.Supplier supplier);
}