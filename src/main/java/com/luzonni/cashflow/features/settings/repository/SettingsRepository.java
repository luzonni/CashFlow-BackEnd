package com.luzonni.cashflow.features.settings.repository;

import com.luzonni.cashflow.features.settings.domain.Settings;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class SettingsRepository implements PanacheRepository<Settings> {

    public Settings getById(UUID userId) {
        return find("id = ?1", userId).firstResult();
    }

}
