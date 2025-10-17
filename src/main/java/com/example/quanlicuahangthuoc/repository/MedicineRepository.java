package com.example.quanlicuahangthuoc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quanlicuahangthuoc.entity.Medicine;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
    

    List<Medicine> findAll(Sort sort);
    
    // Phân trang với sắp xếp
    Page<Medicine> findAll(Pageable pageable);

    List<Medicine> findAllByOrderByNameAsc();
    
   
    List<Medicine> findAllByOrderByNameDesc();
    
    
    List<Medicine> findAllByOrderByPriceAsc();
    
 
    List<Medicine> findAllByOrderByPriceDesc();
    
}