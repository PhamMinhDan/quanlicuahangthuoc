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

    // Tìm kiếm theo tên thuốc (không phân biệt hoa thường) với phân trang
    Page<Medicine> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Tìm kiếm theo tên thuốc chính xác để kiểm tra trùng lặp
    Optional<Medicine> findByName(String name);

    // Tìm kiếm theo loại thuốc với phân trang
    Page<Medicine> findByType(Medicine.MedicineType type, Pageable pageable);

    // Tìm kiếm theo nhà cung cấp với phân trang
    Page<Medicine> findBySupplier(Medicine.Supplier supplier, Pageable pageable);

    // Tìm kiếm theo nhiều điều kiện với phân trang và sắp xếp
    @Query("SELECT m FROM Medicine m WHERE " +
            "(:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:type IS NULL OR m.type = :type) AND " +
            "(:supplier IS NULL OR m.supplier = :supplier)")
    Page<Medicine> findByFilters(@Param("name") String name,
                                 @Param("type") Medicine.MedicineType type,
                                 @Param("supplier") Medicine.Supplier supplier,
                                 Pageable pageable);

    @Query("SELECT COUNT(m) FROM Medicine m WHERE " +
            "(:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:type IS NULL OR m.type = :type) AND " +
            "(:supplier IS NULL OR m.supplier = :supplier)")
    long countByFilters(@Param("name") String name,
                        @Param("type") Medicine.MedicineType type,
                        @Param("supplier") Medicine.Supplier supplier);
    // Thêm phương thức đếm tổng số thuốc
    long count();

    // Thêm truy vấn để đếm số nhà cung cấp khác nhau
    @Query("SELECT COUNT(DISTINCT m.supplier) FROM Medicine m")
    long countDistinctSuppliers();

    // Thêm truy vấn để tính tổng stockQuantity
    @Query("SELECT COALESCE(SUM(m.stockQuantity), 0) FROM Medicine m")
    Integer sumStockQuantity();
}