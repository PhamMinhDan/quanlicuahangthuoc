package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    

    List<Promotion> findAll(Sort sort);
    

    Page<Promotion> findAll(Pageable pageable);
    

    List<Promotion> findAllByOrderByNameAsc();

    List<Promotion> findAllByOrderByNameDesc();
    

    List<Promotion> findAllByOrderByValidityPeriodAsc();
    

    List<Promotion> findAllByOrderByValidityPeriodDesc();
    

    Page<Promotion> findAllByOrderByNameAsc(Pageable pageable);
    

    Page<Promotion> findAllByOrderByNameDesc(Pageable pageable);
    

    Page<Promotion> findAllByOrderByValidityPeriodAsc(Pageable pageable);
    

    Page<Promotion> findAllByOrderByValidityPeriodDesc(Pageable pageable);
}
