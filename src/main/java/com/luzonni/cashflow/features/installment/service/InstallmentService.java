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
import com.luzonni.cashflow.features.transaction.repository.TransactionRepository;
import com.luzonni.cashflow.features.transaction.service.TransactionService;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import com.luzonni.cashflow.shared.type.TransactionState;
import com.luzonni.cashflow.shared.type.TransactionType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@ApplicationScoped
public class InstallmentService {

    private final InstallmentRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    public InstallmentService(
            InstallmentRepository repository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            PaymentMethodRepository paymentMethodRepository,
            TransactionService transactionService,
            TransactionRepository transactionRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
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
        return this.map(inst);
    }

    public Map<UUID, Boolean> getPercent(UUID userId, Long id) {
        Installment inst = repository.find(
                "id = ?1 and user.id = ?2",
                id, userId
        ).firstResultOptional().orElseThrow();
        List<Transaction> transactions = inst.getTransactions();
        Map<UUID, Boolean> items = new HashMap<>();
        for (Transaction transaction : transactions) {
            items.put(transaction.getId(), transaction.confirmed());
        }
        return items;
    }

    private Integer getConclusions(Installment inst) {
        return (int) inst.getTransactions().stream().filter(Transaction::confirmed).count();
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

    public List<InstallmentResponse> list(UUID userId) {
        return repository.find(
                "user.id = ?1",
                userId
        ).list().stream().map(this::map).toList();
    }

    private InstallmentResponse map(Installment inst) {
        InstallmentResponse response = new InstallmentResponse(inst);
        response.setConclusions(getConclusions(inst));
        return response;
    }

}
