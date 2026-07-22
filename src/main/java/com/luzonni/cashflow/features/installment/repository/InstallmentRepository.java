package com.luzonni.cashflow.features.installment.repository;

import com.luzonni.cashflow.features.installment.domain.Installment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InstallmentRepository implements PanacheRepository<Installment> {
}
