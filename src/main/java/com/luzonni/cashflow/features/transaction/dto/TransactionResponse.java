package com.luzonni.cashflow.features.transaction.dto;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import com.luzonni.cashflow.shared.type.TransactionState;
import com.luzonni.cashflow.shared.type.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {

    private UUID id;
    private String description;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private TransactionType type;
    private TransactionState state;
    private Category category;
    private LocalDateTime date;
    private LocalDateTime createdAt;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
        this.paymentMethod = transaction.getPaymentMethod();
        this.type = transaction.getType();
        this.state = transaction.getState();
        this.category = transaction.getCategory();
        this.date =  transaction.getDate();
        this.createdAt = transaction.getCreatedAt();
    }

}
