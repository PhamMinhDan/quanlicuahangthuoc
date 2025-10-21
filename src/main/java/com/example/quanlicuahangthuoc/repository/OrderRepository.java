package com.example.quanlicuahangthuoc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quanlicuahangthuoc.entity.Order;
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAll();
    Page<Order> findAll(Pageable pageable);
    List<Order> findByOrderId(Integer id);
    Page<Order> findByOrderId(Integer id, Pageable pageable);
}
