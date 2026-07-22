package com.luzonni.cashflow.features.installment.domain;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import com.luzonni.cashflow.features.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "installments")
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column()
    private Integer installments;
    @Column()
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column
    private String currency;
    @Column
    private String description;
    @Column
    private LocalDate date;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column
    private Boolean deleted = false;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "installment_transaction",
            joinColumns = @JoinColumn(name = "installment_id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id")
    )
    private List<Transaction> transactions;

    @PrePersist
    public void onCreate() {
        this.deleted = false;
        this.createdAt = LocalDateTime.now();
    }

}
