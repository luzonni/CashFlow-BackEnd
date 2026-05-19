package com.luzonni.cashflow.features.payment_rules.repository;

import com.luzonni.cashflow.features.payment_rules.domain.PaymentRule;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentRuleRepository implements PanacheRepository<PaymentRule> {


}
