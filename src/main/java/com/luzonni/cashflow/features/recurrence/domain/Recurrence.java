package com.luzonni.cashflow.features.recurrence.domain;

import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.recurrence.enums.Scheduling;
import com.luzonni.cashflow.features.recurrence.enums.RecurrenceStatus;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.shared.type.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "recurrence")
public class Recurrence {

    @Id
    @UuidGenerator()
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;
    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column
    private BigDecimal amount;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    @Enumerated(EnumType.STRING)
    private Scheduling frequency;
    @Column(name = "interval_value")
    private Integer intervalValue;
    @Column(name = "max_occurrences")
    private Integer maxOccurrences;
    @Column
    private String currency;
    @Column
    private String timezone;
    @Column
    @Enumerated(EnumType.STRING)
    private RecurrenceStatus status = RecurrenceStatus.ACTIVE;

    @OneToMany(mappedBy = "recurrence")
    @OrderBy("scheduledTo ASC")
    private List<RecurrenceRecord> records;

    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = RecurrenceStatus.ACTIVE;
    }

}
