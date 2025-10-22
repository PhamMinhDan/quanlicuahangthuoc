package com.example.quanlicuahangthuoc.repository;

import com.example.quanlicuahangthuoc.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByEmail(String email);

    Optional<Customer> findByPhone(String phone);

    @Query("SELECT c FROM Customer c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:phone IS NULL OR :phone = '' OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
           "(:customerType IS NULL OR :customerType = '' OR CAST(c.customerType AS string) = :customerType)")
    Page<Customer> searchByNamePhoneTypeWithPaging(
            @Param("keyword") String keyword,
            @Param("phone") String phone,
            @Param("customerType") String customerType,
            Pageable pageable);
}