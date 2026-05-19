package com.luzonni.cashflow.features.payment_rules.service;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.features.exception.domain.AppException;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodResponse;
import com.luzonni.cashflow.features.payment_method.repository.PaymentMethodRepository;
import com.luzonni.cashflow.features.payment_rules.domain.PaymentRule;
import com.luzonni.cashflow.features.payment_rules.dto.PaymentRuleRequest;
import com.luzonni.cashflow.features.payment_rules.dto.PaymentRuleResponse;
import com.luzonni.cashflow.features.payment_rules.helper.PaymentConfig;
import com.luzonni.cashflow.features.payment_rules.repository.PaymentRuleRepository;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PaymentRulesService {

    private final PaymentRuleRepository repository;
    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    private final PaymentConfig config;

    public PaymentRulesService(
            PaymentRuleRepository repository,
            CategoryRepository categoryRepository,
            PaymentMethodRepository paymentMethodRepository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
        this.config = new PaymentConfig();
    }

    @Transactional
    public PaymentRuleResponse create(UUID userId, PaymentRuleRequest request) {
        if (!config.validate(request.getConfig())) {
            throw new AppException(
                    Response.Status.CONFLICT,
                    ErrorCode.INVALID_OPERATION,
                    "config stream not valid"
            );
        }
        PaymentRule paymentRule = new PaymentRule();
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId());
            paymentRule.setCategory(category);
        } else if (request.getPaymentMethodId() != null) {
            PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId());
            paymentRule.setPaymentMethod(paymentMethod);
        } else {
            throw new AppException(Response.Status.CONFLICT, ErrorCode.INVALID_OPERATION, "Association not found");
        }
        User user = userRepository.getUserById(userId);
        paymentRule.setUser(user);
        paymentRule.setConfig(request.getConfig());
        paymentRule.setType(request.getType());
        repository.persist(paymentRule);
        return new PaymentRuleResponse(paymentRule);
    }

}
