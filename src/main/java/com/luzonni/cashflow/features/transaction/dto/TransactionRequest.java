package com.luzonni.cashflow.features.transaction.dto;

import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionRequest {

    @NotNull
    private String userCategoryId;
    @NotNull
    private BigDecimal amount;
    @NotEmpty(message = "Name is required")
    private String description;
    @NotNull
    private Date transactionDate;


}
