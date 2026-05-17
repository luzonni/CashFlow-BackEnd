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
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public List<TransactionResponse> listAll(UUID userId) {
        List<Transaction> list = repository.find("user.id = ?1", userId).list();
        return list.stream().map(TransactionResponse::new).toList();
    }

    public List<TransactionResponse> listWithDate(UUID userId, LocalDate start, LocalDate end) {
        List<Transaction> list = repository.find(
                "user.id = ?1 and date between ?2 and ?3 order by date asc",
                userId, start, end
        ).list();
        return list.stream().map(TransactionResponse::new).toList();
    }

    public TransactionResponse get(UUID userId, UUID id) {
        Transaction transaction = repository.find(
                "user.id = ?1 and id = ?2",
                userId, id
        ).firstResultOptional().orElseThrow(NotFoundException::new);
        return new TransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse update(UUID userId, UUID id, TransactionRequest request) {
        Transaction transaction = repository.find(
                "user.id = ?1 and id = ?2",
                userId, id
        ).firstResultOptional().orElseThrow(NotFoundException::new);
        if(request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId());
            transaction.setCategory(category);
        }
        if(request.getPaymentMethodId() != null) {
            PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId());
            transaction.setPaymentMethod(paymentMethod);
        }
        if(request.getDate() != null) {
            transaction.setDate(request.getDate());
        }
        if(request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if(request.getCurrency() != null) {
            transaction.setCurrency(request.getCurrency());
        }
        if(request.getState() != null) {
            transaction.setState(request.getState());
        }
        if(request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        repository.persist(transaction);
        return new TransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse create(UUID userId, TransactionRequest request) {
        User user =  userRepository.findById(userId).orElseThrow();
        Optional<Category> optCat = categoryRepository.findByIdOptional(request.getCategoryId());
        Optional<PaymentMethod> optPayMet = paymentMethodRepository.findByIdOptional(request.getPaymentMethodId());
        if(optCat.isEmpty() || optPayMet.isEmpty()) {
            throw new NotFoundException("Element for creation not found");
        }
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCurrency(request.getCurrency());
        transaction.setState(request.getState());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setCategory(optCat.get());
        transaction.setPaymentMethod(optPayMet.get());
        repository.persist(transaction);
        return new TransactionResponse(transaction);
    }

}
