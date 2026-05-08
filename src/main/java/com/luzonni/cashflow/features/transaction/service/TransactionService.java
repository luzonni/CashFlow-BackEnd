package com.luzonni.cashflow.features.transaction.service;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.payment_method.repository.PaymentMethodRepository;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import com.luzonni.cashflow.features.transaction.dto.TransactionRequest;
import com.luzonni.cashflow.features.transaction.dto.TransactionResponse;
import com.luzonni.cashflow.features.transaction.repository.TransactionRepository;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class TransactionService {

    private final TransactionRepository repository;
    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository repository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            PaymentMethodRepository paymentMethodRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Transactional
    public TransactionResponse create(UUID userId, TransactionRequest request) {
        User user =  userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(request.getCategoryId());
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId());
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setCategory(category);
        transaction.setPaymentMethod(paymentMethod);
        repository.persist(transaction);
        return new TransactionResponse(transaction);
    }

}
