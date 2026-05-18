package com.luzonni.cashflow.features.payment_method.service;

import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodRequest;
import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodResponse;
import com.luzonni.cashflow.features.payment_method.repository.PaymentMethodRepository;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PaymentMethodService {

    private final UserRepository  userRepository;
    private final PaymentMethodRepository repository;

    public PaymentMethodService(
            PaymentMethodRepository paymentMethodRepository,
            UserRepository userRepository
    ) {
        this.repository = paymentMethodRepository;
        this.userRepository = userRepository;
    }

    public List<PaymentMethodResponse> listAll(UUID userId) {
        return repository
                .find("deleted = false and user.id = ?1 order by createdAt asc", userId)
                .stream()
                .map(PaymentMethodResponse::new)
                .toList();
    }

    @Transactional
    public PaymentMethodResponse create(UUID userId, PaymentMethodRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        PaymentMethod pm = repository.find("user.id = ?1 and name = ?2 and deleted = true",
            userId, request.getName()
        ).firstResult();
        if(pm != null) {
            pm.setDeleted(false);
            pm.setColor(request.getColor());
            repository.persist(pm);
            return new PaymentMethodResponse(pm);
        }
        pm = new PaymentMethod();
        pm.setUser(user);
        pm.setColor(request.getColor());
        pm.setName(request.getName());
        repository.persist(pm);
        return new PaymentMethodResponse(pm);
    }

    @Transactional
    public PaymentMethodResponse update(UUID userId, Long id, PaymentMethodRequest request) {
        PaymentMethod paymentMethod = repository.find("id = ?1 and user.id = ?2",
                id, userId
        ).firstResult();
        if(paymentMethod != null) {
            paymentMethod.setName(request.getName());
            paymentMethod.setColor(request.getColor());
            repository.persist(paymentMethod);
            return new PaymentMethodResponse(paymentMethod);
        }
        throw new ForbiddenException("Not allowed to update this payment method");
    }

    @Transactional
    public void delete(UUID userId, Long id) {
        PaymentMethod paymentMethod = repository.find("id = ?1 and user.id = ?2",
                id, userId
        ).firstResult();
        if(paymentMethod != null) {
            paymentMethod.setDeleted(true);
            repository.persist(paymentMethod);
        }
    }

}
