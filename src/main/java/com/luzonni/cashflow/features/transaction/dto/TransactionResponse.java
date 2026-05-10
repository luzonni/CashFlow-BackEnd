package com.luzonni.cashflow.features.transaction.dto;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodResponse;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import com.luzonni.cashflow.shared.type.TransactionState;
import com.luzonni.cashflow.shared.type.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {

    private UUID id;
    private String description;
    private BigDecimal amount;
    private PaymentMethodResponse paymentMethod;
    private String type;
    private String state;
    private String currency;
    private CategoryResponse category;
    private LocalDate date;
    private LocalDateTime createdAt;

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
        this.createdAt = transaction.getCreatedAt();
    }

}
