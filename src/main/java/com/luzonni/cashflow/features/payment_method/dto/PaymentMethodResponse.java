package com.luzonni.cashflow.features.payment_method.dto;

import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import lombok.Data;

@Data
public class PaymentMethodResponse {

    private Long id;
    private String color;
    private String name;

    public PaymentMethodResponse(PaymentMethod paymentMethod) {
        this.id = paymentMethod.getId();
        this.color = paymentMethod.getColor();
        this.name = paymentMethod.getName();
    }

}
