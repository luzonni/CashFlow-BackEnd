package com.luzonni.cashflow.features.payment_method.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PaymentMethodRequest {

    @NotEmpty
    private String color;
    @NotEmpty
    private String name;

}
