package com.example.quanlicuahangthuoc.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Promotion name must not be empty")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Promotion type must not be empty")
    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @NotNull(message = "Discount percent must not be null")
    @DecimalMin(value = "0.0", message = "Discount percent must be >= 0")
    @DecimalMax(value = "100.0", message = "Discount percent must be <= 100")
    @Column(name = "discount_percent", nullable = false, columnDefinition = "DECIMAL(5,2)")
    private Double discountPercent;

    @NotNull(message = "Expired date must not be null")
    @Column(name = "expired_date", nullable = false)
    private LocalDate expiredDate;
    @JsonManagedReference
    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Order> orders = new HashSet<>();

    // Constructors
    public Promotion() {}

    public Promotion(String name, String type, Double discountPercent, LocalDate expiredDate) {
        this.name = name;
        this.type = type;
        this.discountPercent = discountPercent;
        this.expiredDate = expiredDate;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setPromotion(this);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setPromotion(null);
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", discountPercent=" + discountPercent +
                ", expiredDate=" + expiredDate +
                '}';
    }
}
