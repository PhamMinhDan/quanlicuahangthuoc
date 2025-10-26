package com.example.quanlicuahangthuoc.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "order_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonBackReference(value = "customer-order")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // THÊM MỚI: Trường phone để tìm customer
    @Transient // Không lưu vào database, chỉ dùng để tìm customer
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{9,11}$", message = "Số điện thoại phải từ 9 đến 11 chữ số")
    private String customerPhone;

    @JsonBackReference(value = "staff-order")
    @NotNull(message = "Staff ID cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @JsonBackReference(value = "promotion-order")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_id")
    private Promotion promotion;

    // THÊM MỚI: Trường promotionName để chọn từ dropdown
    @Transient // Không lưu vào database
    private String promotionName;

    @NotNull(message = "Order date cannot be null")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total amount must be greater than or equal to 0")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE) // Định nghĩa hành vi cascade trên CSDL
    private List<Payment> payments;

    public enum OrderStatus {
        da_thanh_toan("Đã thanh toán"),
        chua_thanh_toan("Chưa thanh toán"),
        da_huy("Đã hủy");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Order(Customer customer, Staff staff, Promotion promotion, LocalDate orderDate, OrderStatus status, BigDecimal totalAmount) {
        this.customer = customer;
        this.staff = staff;
        this.promotion = promotion;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters đã có từ Lombok
    // Thêm getter/setter cho customerPhone và promotionName nếu cần custom logic

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + (customer != null ? customer.getId() : null) +
                ", staff=" + (staff != null ? staff.getId() : null) +
                ", promotion=" + (promotion != null ? promotion.getId() : null) +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                '}';
    }
}