package com.luzonni.cashflow.features.payment_rules.dto;

import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodResponse;
import com.luzonni.cashflow.features.payment_rules.domain.PaymentRule;
import com.luzonni.cashflow.features.payment_rules.domain.PaymentRuleType;
import lombok.Getter;

@Getter
public class PaymentRuleResponse {

    private final PaymentMethodResponse paymentMethodResponse;
    private final CategoryResponse categoryResponse;
    private final PaymentRuleType type;

    public PaymentRuleResponse(
            PaymentRule rule
    ) {
        this.paymentMethodResponse = new PaymentMethodResponse(rule.getPaymentMethod());
        this.categoryResponse = new CategoryResponse(rule.getCategory());
        this.type = rule.getType();
    }

}
