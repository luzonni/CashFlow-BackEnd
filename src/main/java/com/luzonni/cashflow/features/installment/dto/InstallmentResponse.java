package com.luzonni.cashflow.features.installment.dto;

import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.installment.domain.Installment;
import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InstallmentResponse {

    private final Long id;
    private final BigDecimal amount;
    private final String currency;
    private final Integer installments;
    private Integer conclusions;
    private final CategoryResponse category;
    private final PaymentMethodResponse paymentMethod;
    private final String description;
    private final LocalDate date;
    private final LocalDateTime createdAt;

    public InstallmentResponse(Installment installment) {
        this.id = installment.getId();
        this.amount = installment.getAmount();
        this.currency = installment.getCurrency();
        this.installments = installment.getInstallments();
        this.category = new CategoryResponse(installment.getCategory());
        this.paymentMethod = new PaymentMethodResponse(installment.getPaymentMethod());
        this.description = installment.getDescription();
        this.date = installment.getDate();
        this.createdAt = installment.getCreatedAt();
        this.conclusions = 0;
    }

}
