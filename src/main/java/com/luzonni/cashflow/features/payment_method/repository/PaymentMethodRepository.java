package com.luzonni.cashflow.features.payment_method.repository;

import com.luzonni.cashflow.features.exception.domain.AppException;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@ApplicationScoped
public class PaymentMethodRepository implements PanacheRepository<PaymentMethod> {

    @Override
    public PaymentMethod findById(Long id) {
        Optional<PaymentMethod> optional = find(
                "id = ?1 and deleted = false",
                id
        ).firstResultOptional();
        if(optional.isEmpty()) {
            throw new AppException(
                    Response.Status.NOT_FOUND,
                    ErrorCode.ENTITY_NOT_FOUND,
                    "Payment method not found or is deprecated"
            );
        }
        return optional.get();
    }

}
