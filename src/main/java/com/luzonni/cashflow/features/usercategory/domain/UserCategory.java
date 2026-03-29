package com.luzonni.cashflow.features.usercategory.domain;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.shared.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity()
@Table(name = "user_categories")
public class UserCategory {

    @Id
    @UuidGenerator()
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "base_category_id")
    private Category baseCategory;
    @Column
    private String name;
    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
