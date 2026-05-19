package com.luzonni.cashflow.features.payment_rules.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class TransactionPaymentRuleId implements Serializable {

    @Column(name = "transaction")
    private UUID transactionId;

    @Column(name = "payment_rule")
    private Long paymentRuleId;

}