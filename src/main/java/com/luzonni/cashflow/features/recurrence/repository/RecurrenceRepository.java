package com.luzonni.cashflow.features.recurrence.repository;

import com.luzonni.cashflow.features.recurrence.domain.Recurrence;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RecurrenceRepository implements PanacheRepository<Recurrence> {



}
