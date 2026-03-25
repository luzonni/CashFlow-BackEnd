package io.luzonni.mindbudget.domain.model.transaction;

import io.luzonni.mindbudget.domain.model.category.UserCategory;
import io.luzonni.mindbudget.domain.model.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
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
    @JoinColumn(name = "user_category_id")
    private UserCategory userCategory;
    @Column
    private BigDecimal amount;
    @Column
    private String description;
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
