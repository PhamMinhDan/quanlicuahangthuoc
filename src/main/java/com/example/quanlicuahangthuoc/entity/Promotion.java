package com.example.quanlicuahangthuoc.entity;

import jakarta.persistence.*;
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
    
    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotBlank(message = "Loại khuyến mãi không được để trống")
    @Column(name = "type", nullable = false, length = 100)
    private String type;
    
    @DecimalMin(value = "0.0", message = "Phần trăm giảm giá phải >= 0")
    @DecimalMax(value = "100.0", message = "Phần trăm giảm giá phải <= 100")
    @Column(name = "discount_percent", nullable = false, columnDefinition = "DECIMAL(5,2)")
    private Double discountPercent;
    
    @NotBlank(message = "Thời gian hiệu lực không được để trống")
    @Column(name = "validity_period", nullable = false, length = 50)
    private String validityPeriod;
    
    // Constructors
    public Promotion() {}
    
    public Promotion(String name, String type, Double discountPercent, String validityPeriod) {
        this.name = name;
        this.type = type;
        this.discountPercent = discountPercent;
        this.validityPeriod = validityPeriod;
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
    
    public String getValidityPeriod() {
        return validityPeriod;
    }
    
    public void setValidityPeriod(String validityPeriod) {
        this.validityPeriod = validityPeriod;
    }
    
  
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", discountPercent=" + discountPercent +
                ", validityPeriod='" + validityPeriod + '\'' +
                '}';
    }
}
