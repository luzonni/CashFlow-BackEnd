package com.luzonni.cashflow.features.user.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AmountResponse {


    private final String currency;
    private final BigDecimal amount;

    public AmountResponse(
            BigDecimal amount,
            String currency
    ) {
        this.amount = amount;
        this.currency = currency;
    }

}
