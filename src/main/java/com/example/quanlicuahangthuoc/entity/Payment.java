package com.example.quanlicuahangthuoc.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Order ID cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Order order;

    @NotNull(message = "Payment method cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be greater than or equal to 0")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Change must be greater than or equal to 0")
    @Column(name = "`change`", precision = 10, scale = 2)
    private BigDecimal change;

    @NotNull(message = "Payment date cannot be null")
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    public enum PaymentMethod {
        tien_mat("Tiền mặt"),
        chuyen_khoan("Chuyển khoản");
        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructor without id
    public Payment(Order order, PaymentMethod paymentMethod, BigDecimal amount, BigDecimal change, LocalDate paymentDate) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.change = change;
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", order=" + (order != null ? order.getId() : null) +
                ", paymentMethod=" + paymentMethod +
                ", amount=" + amount +
                ", change=" + change +
                ", paymentDate=" + paymentDate +
                '}';
    }
}