package com.luzonni.cashflow.features.transaction.dto;

import com.luzonni.cashflow.shared.type.TransactionState;
import com.luzonni.cashflow.shared.type.TransactionType;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NegativeOrZero
    private Long categoryId;
    @NotNull
    private Long paymentMethodId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private TransactionType type;
    @NotNull
    private TransactionState state;
    @NotEmpty(message = "Name is required")
    private String description;
    @NotNull
    private LocalDate date;
    @NotEmpty(message = "The currency is necessary")
    private String currency;


}
