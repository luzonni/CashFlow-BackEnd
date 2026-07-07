package com.luzonni.cashflow.features.category.domain;

import com.luzonni.cashflow.features.group_category.domain.GroupCategory;
import com.luzonni.cashflow.features.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Boolean deleted = false;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
