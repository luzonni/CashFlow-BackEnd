package com.luzonni.cashflow.features.recurrence.domain;

import com.luzonni.cashflow.features.recurrence.enums.RecurrenceRecordStatus;
import com.luzonni.cashflow.features.recurrence.enums.RecurrenceStatus;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "recurrence_execution_records")
public class RecurrenceRecord {

    @Id
    @UuidGenerator
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "recurrence_id")
    private Recurrence recurrence;
    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    @Column
    private BigDecimal amount;
    @Column(name = "executed_at")
    private LocalDateTime executedAt;
    @Column(name = "scheduled_to")
    private LocalDate scheduledTo;
    @Column(name = "occurrence_number")
    private Integer occurrenceNumber;
    @Column
    @Enumerated(EnumType.STRING)
    private RecurrenceRecordStatus status; // PENDING, EXECUTED, SKIPPED, FAILED
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = RecurrenceRecordStatus.PENDING;
    }

}
