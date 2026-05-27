package com.luzonni.cashflow.features.recurrence.dto;

import com.luzonni.cashflow.features.recurrence.enums.Scheduling;
import com.luzonni.cashflow.shared.type.TransactionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecurrenceRequest {

    @NotEmpty
    private String name;
    @NotEmpty
    private String description;

    @NotNull
    private BigDecimal amount;
    @NotEmpty
    private String currency;
    @NotNull
    private TransactionType type;
    @NotNull
    private Long categoryId;
    @NotNull
    private Long paymentMethodId;

    @NotNull
    private LocalDate firstRecord;
    @NotNull
    private Scheduling frequency;
    @NotNull
    private Integer interval;
    @NotNull
    private Integer maxOccurrences;

    @NotEmpty
    private String timeZone;

}
