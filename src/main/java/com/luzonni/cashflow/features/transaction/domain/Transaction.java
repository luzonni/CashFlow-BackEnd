package com.luzonni.cashflow.features.transaction.domain;

import com.luzonni.cashflow.features.usercategory.domain.UserCategory;
import com.luzonni.cashflow.features.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
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
    @JoinColumn(name = "user_category_id")
    private UserCategory userCategory;
    @Column
    private BigDecimal amount;
    @Column
    private String description;
    @Column(name = "transaction_date")
    private Date transactionDate;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
