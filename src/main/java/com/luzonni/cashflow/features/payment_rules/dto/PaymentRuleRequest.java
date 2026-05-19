package com.luzonni.cashflow.features.payment_rules.dto;

import com.luzonni.cashflow.features.payment_rules.domain.PaymentRuleType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRuleRequest {

    @Nullable
    private Long paymentMethodId;
    @Nullable
    private Long categoryId;
    @NotNull
    private PaymentRuleType type;
    @NotEmpty
    private String config;

}
