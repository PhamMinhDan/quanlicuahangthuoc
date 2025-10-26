package com.example.quanlicuahangthuoc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Promotion name must not be empty")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Promotion type must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "ENUM('giam_gia_truc_tiep', 'giam_gia_tong_hoa_don')")
    private PromotionType type;

    @NotNull(message = "Discount percent must not be null")
    @DecimalMin(value = "0.0", message = "Discount percent must be >= 0")
    @DecimalMax(value = "100.0", message = "Discount percent must be <= 100")
    @Column(name = "discount_percent", nullable = false, columnDefinition = "DECIMAL(5,2)")
    private Double discountPercent;

    @NotNull(message = "Expired date must not be null")
    @FutureOrPresent(message = "Expired date must be today or in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "validity_period", nullable = false)
    private LocalDate expiredDate;

    // Constructors
    public Promotion() {}

    public Promotion(String name, PromotionType type, Double discountPercent, LocalDate expiredDate) {
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

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
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

    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", discountPercent=" + discountPercent +
                ", expiredDate=" + expiredDate +
                '}';
    }

    // Enum cho loại khuyến mãi với displayName
    public enum PromotionType {
        giam_gia_truc_tiep("Giảm giá trực tiếp"),
        giam_gia_tong_hoa_don("Giảm giá tổng hóa đơn");

        private final String displayName;

        PromotionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}