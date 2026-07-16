package com.luzonni.cashflow.features.transaction.domain;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.shared.type.TransactionState;
import com.luzonni.cashflow.shared.type.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @UuidGenerator()
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(precision = 28, scale = 8)
    private BigDecimal amount;
    @Column
    private String description;
    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column
    private String currency;
    @Column(precision = 28, scale = 8)
    private BigDecimal defaultAmount;
    @Column
    @Enumerated(EnumType.STRING)
    private TransactionState state;
    @Column(name = "transaction_date")
    private LocalDate date;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
