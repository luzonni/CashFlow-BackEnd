package com.luzonni.cashflow.features.transaction.dto;

import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodResponse;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {

    private final UUID id;
    private final String description;
    private BigDecimal amount;
    private final BigDecimal defaultAmount;
    private final PaymentMethodResponse paymentMethod;
    private final String type;
    private final String state;
    private final String currency;
    private final CategoryResponse category;
    private final LocalDate date;
    private final LocalDateTime createdAt;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
        this.paymentMethod = new PaymentMethodResponse(transaction.getPaymentMethod());
        this.type = transaction.getType().toString();
        this.state = transaction.getState().toString();
        this.category = new CategoryResponse(transaction.getCategory());
        this.date =  transaction.getDate();
        this.currency = transaction.getCurrency();
        this.defaultAmount = transaction.getDefaultAmount();
        this.createdAt = transaction.getCreatedAt();
    }

}
