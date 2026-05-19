package com.luzonni.cashflow.features.transaction.dto;

import com.luzonni.cashflow.shared.type.TransactionState;
import com.luzonni.cashflow.shared.type.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @Min(value = 1, message = "Category is required")
    @NotNull(message = "Category is required")
    private Long categoryId;
    @Min(value = 1, message = "Payment method is required")
    @NotNull(message = "Payment method is required")
    private Long paymentMethodId;
    @NotNull
    private BigDecimal amount;
    @NotNull(message = "Type is required")
    private TransactionType type;
    @NotNull(message = "State is required")
    private TransactionState state;
    @NotEmpty(message = "Description is required")
    private String description;
    @NotNull
    private LocalDate date;
    @NotEmpty(message = "The currency is necessary")
    private String currency;


}
