package com.luzonni.cashflow.features.payment_method.repository;

import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentMethodRepository implements PanacheRepository<PaymentMethod> {

}
