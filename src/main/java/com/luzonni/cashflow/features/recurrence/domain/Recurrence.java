package com.luzonni.cashflow.features.recurrence.domain;

import com.luzonni.cashflow.features.recurrence.enums.Scheduling;
import com.luzonni.cashflow.features.recurrence.enums.Status;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.category.domain.Category;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "recurrence")
public class Recurrence {

    @Id
    @UuidGenerator()
    private UUID id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "user_category_id")
    private Category userCategory;
    @Column
    @Enumerated(EnumType.STRING)
    private Scheduling scheduling;
    @Column(name = "max_occurrences")
    private Integer maxOccurrences;
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
