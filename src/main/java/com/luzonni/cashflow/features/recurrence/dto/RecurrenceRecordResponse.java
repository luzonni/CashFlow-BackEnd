package com.luzonni.cashflow.features.recurrence.dto;

import com.luzonni.cashflow.features.transaction.dto.TransactionResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RecurrenceRecordResponse {

    private UUID id;
    private BigDecimal amount;
    private Integer occurrenceNumber;
    private LocalDateTime scheduledTo;
    private LocalDateTime executeAt;
    private LocalDateTime createAt;
    private RecurrenceResponse recurrence;
    private TransactionResponse transaction;

}
