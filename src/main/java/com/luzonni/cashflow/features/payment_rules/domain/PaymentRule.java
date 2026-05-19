package com.luzonni.cashflow.features.payment_rules.domain;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import com.luzonni.cashflow.features.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "payment_rules")
public class PaymentRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "rule_type")
    @Enumerated(EnumType.STRING)
    private PaymentRuleType type;

    @Column(name = "config")
    private String config;

    @Column
    private Boolean deleted = false;

    @OneToMany(mappedBy = "paymentRule")
    private Set<TransactionPaymentRule> transactions = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
