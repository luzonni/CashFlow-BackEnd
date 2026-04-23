package com.luzonni.cashflow.features.category.domain;

import com.luzonni.cashflow.features.category.type.TransactionType;
import com.luzonni.cashflow.features.group_category.domain.GroupCategory;
import com.luzonni.cashflow.features.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity()
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupCategory group;
    @Column
    private String color;
    @Column
    private String name;
    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column
    private Boolean deleted = false;
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
