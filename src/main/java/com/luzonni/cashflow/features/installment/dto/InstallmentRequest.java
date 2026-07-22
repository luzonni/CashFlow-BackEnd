package com.luzonni.cashflow.features.installment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class InstallmentRequest {

    @NotEmpty
    private String currency;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer installments;
    @NotNull
    private Long categoryId;
    @NotNull
    private Long paymentMethodId;
    @NotEmpty
    private String description;
    @NotNull
    private LocalDate date;

}
