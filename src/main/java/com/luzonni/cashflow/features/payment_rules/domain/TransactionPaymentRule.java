package com.luzonni.cashflow.features.payment_rules.domain;

import com.luzonni.cashflow.features.transaction.domain.Transaction;
import jakarta.persistence.*;

@Entity
@Table(name = "payment_rules_provider")
public class TransactionPaymentRule {

    @EmbeddedId
    private TransactionPaymentRuleId id;

    @ManyToOne
    @MapsId("transactionId")
    @JoinColumn(name = "transaction")
    private Transaction transaction;

    @ManyToOne
    @MapsId("paymentRuleId")
    @JoinColumn(name = "payment_rule")
    private PaymentRule paymentRule;

}