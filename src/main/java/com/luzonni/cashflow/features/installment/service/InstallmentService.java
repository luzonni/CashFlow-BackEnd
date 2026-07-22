package com.luzonni.cashflow.features.installment.service;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.features.installment.domain.Installment;
import com.luzonni.cashflow.features.installment.dto.InstallmentRequest;
import com.luzonni.cashflow.features.installment.dto.InstallmentResponse;
import com.luzonni.cashflow.features.installment.repository.InstallmentRepository;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.payment_method.repository.PaymentMethodRepository;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import com.luzonni.cashflow.features.transaction.dto.TransactionRequest;
import com.luzonni.cashflow.features.transaction.dto.TransactionResponse;
import com.luzonni.cashflow.features.transaction.service.TransactionService;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import com.luzonni.cashflow.shared.type.TransactionState;
import com.luzonni.cashflow.shared.type.TransactionType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class InstallmentService {

    private final InstallmentRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final TransactionService transactionService;

    public InstallmentService(
            InstallmentRepository repository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            PaymentMethodRepository paymentMethodRepository,
            TransactionService transactionService
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public InstallmentResponse create(UUID userId, InstallmentRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        Installment inst = new Installment();
        inst.setUser(user);
        Category cat = categoryRepository.findById(request.getCategoryId());
        inst.setCategory(cat);
        PaymentMethod pm = paymentMethodRepository.findById(request.getPaymentMethodId());
        inst.setPaymentMethod(pm);
        inst.setDescription(request.getDescription());
        inst.setAmount(request.getAmount());
        inst.setInstallments(request.getInstallments());
        inst.setCurrency(request.getCurrency());
        inst.setDate(request.getDate());
        List<Transaction> transactions = buildTransactions(userId, inst);
        inst.setTransactions(transactions);
        repository.persist(inst);
        return new InstallmentResponse(inst);
    }


    private List<Transaction> buildTransactions(UUID userId, Installment inst) {
        List<Transaction> listTransactions = new ArrayList<>();
        BigDecimal installmentAmount = inst.getAmount().divide(BigDecimal.valueOf(inst.getInstallments()), 2, RoundingMode.HALF_EVEN);
        for(int i = 0; i < inst.getInstallments(); i++) {
            TransactionRequest request = new TransactionRequest();
            request.setType(TransactionType.EXPENSE);
            request.setState(TransactionState.PENDING);
            request.setAmount(installmentAmount);
            request.setPaymentMethodId(inst.getPaymentMethod().getId());
            request.setCategoryId(inst.getCategory().getId());
            request.setCurrency(inst.getCurrency());
            request.setDescription(inst.getDescription());
            request.setDate(inst.getDate().plusMonths(i));
            Transaction tr = transactionService.create(userId, request);
            listTransactions.add(tr);
        }
        listTransactions.getFirst().setState(TransactionState.CONFIRM);
        return listTransactions;
    }

}
