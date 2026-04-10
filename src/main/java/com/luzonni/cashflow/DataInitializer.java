package com.luzonni.cashflow;

import com.luzonni.cashflow.features.authorization.domain.Role;
import com.luzonni.cashflow.features.authorization.repository.RoleRepository;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DataInitializer {

    @Inject
    RoleRepository roleRepository;
    @Inject
    UserRepository userRepository;

    @Transactional
    void onStart(@Observes StartupEvent ev) {

    }
}
