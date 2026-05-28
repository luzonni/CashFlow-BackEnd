package com.luzonni.cashflow.features.recurrence.dto;

import com.luzonni.cashflow.features.recurrence.domain.RecurrenceRecord;
import com.luzonni.cashflow.features.transaction.dto.TransactionResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RecurrenceRecordResponse {

    private UUID id;
    private BigDecimal amount;
    private Integer occurrenceNumber;
    private LocalDate scheduledTo;
    private LocalDateTime executeAt;
    private LocalDateTime createAt;
    private String status;

    private TransactionResponse transaction;

    public RecurrenceRecordResponse(RecurrenceRecord recurrenceRecord) {
        this.id = recurrenceRecord.getId();
        this.amount = recurrenceRecord.getAmount();
        this.occurrenceNumber = recurrenceRecord.getOccurrenceNumber();
        this.scheduledTo = recurrenceRecord.getScheduledTo();
        this.executeAt = recurrenceRecord.getExecutedAt();
        this.createAt = recurrenceRecord.getCreatedAt();
        this.status = recurrenceRecord.getStatus().name();
        if(recurrenceRecord.getTransaction() != null) {
            this.transaction = new TransactionResponse(recurrenceRecord.getTransaction());
        }
    }

}
