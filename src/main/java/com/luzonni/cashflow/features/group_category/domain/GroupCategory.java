package com.luzonni.cashflow.features.group_category.domain;

import com.luzonni.cashflow.features.category.type.TransactionType;
import com.luzonni.cashflow.features.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "group_categories")
public class GroupCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private Boolean deleted = false;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean active() {
        return this.deleted == false;
    }

}
