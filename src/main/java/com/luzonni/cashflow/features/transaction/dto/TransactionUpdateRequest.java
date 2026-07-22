package com.luzonni.cashflow.features.transaction.dto;

import com.luzonni.cashflow.shared.type.TransactionState;
import com.luzonni.cashflow.shared.type.TransactionType;
import lombok.Getter;

@Getter
public class TransactionUpdateRequest {

    private Long categoryId;
    private Long paymentMethodId;
    private TransactionState state;
    private TransactionType type;
    private String description;

}
