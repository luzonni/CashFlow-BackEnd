package com.luzonni.cashflow.infrastructure.scheduler;

import com.luzonni.cashflow.features.auth.service.AuthService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CleanupExpiredTokensJob {

    private final AuthService authService;

    @Inject
    public CleanupExpiredTokensJob(AuthService authService) {
        this.authService = authService;
    }

    @Scheduled(every = "24h")
    void cleanOldTokens() {
        authService.deleteOldTokens();
    }

}
