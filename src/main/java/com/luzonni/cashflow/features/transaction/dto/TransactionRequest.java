package com.luzonni.cashflow.features.transaction.dto;

import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.shared.type.TransactionState;
import com.luzonni.cashflow.shared.type.TransactionType;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class TransactionRequest {

    @NotNull
    private Long categoryId;
    @NotNull
    private Long paymentMethodId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private TransactionType type;
    @NotNull
    private TransactionState state;
    @NotEmpty(message = "Name is required")
    private String description;
    @NotNull
    private LocalDateTime date;


}
