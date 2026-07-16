package com.luzonni.cashflow.features.transaction.service;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.features.exchange.service.ExchangeService;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.payment_method.repository.PaymentMethodRepository;
import com.luzonni.cashflow.features.settings.domain.Settings;
import com.luzonni.cashflow.features.settings.service.SettingsService;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import com.luzonni.cashflow.features.transaction.dto.TransactionRequest;
import com.luzonni.cashflow.features.transaction.dto.TransactionResponse;
import com.luzonni.cashflow.features.transaction.repository.TransactionRepository;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TransactionService {

    private final TransactionRepository repository;
    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final ExchangeService exchangeService;
    private final SettingsService settingsService;

    public TransactionService(
            TransactionRepository repository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            PaymentMethodRepository paymentMethodRepository,
            ExchangeService exchangeService,
            SettingsService settingsService
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.exchangeService = exchangeService;
        this.settingsService = settingsService;
    }

    public List<TransactionResponse> listAll(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        List<Transaction> list = transform(
                user,
                repository
                        .find("user.id = ?1", userId)
                        .list()
        );
        return list.stream().map(TransactionResponse::new).toList();
    }

    public List<TransactionResponse> listWithDate(UUID userId, LocalDate start, LocalDate end) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        List<Transaction> list = transform(
                user,
                repository
                        .find("user.id = ?1 and date between ?2 and ?3 order by date asc",
                                userId, start, end)
                        .list()
        );
        return list.stream().map(TransactionResponse::new).toList();
    }

    private List<Transaction> transform(User user, List<Transaction> trs) {
        Settings settings = settingsService.get(user.getId());
        String currency = settings.getCurrency();
        BigDecimal rate = exchangeService.getRate(currency); //Cotação atual
        return trs.stream()
                .peek((tr) -> {
                    BigDecimal trAmount = tr.getAmount();
                    trAmount = trAmount.multiply(rate); // Sempre cotação atual!
                    tr.setAmount(trAmount);
                }).toList();
    }

    public TransactionResponse get(UUID userId, UUID id) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Transaction transaction = transform(
                user,
                repository
                        .find("user.id = ?1 and id = ?2",
                                userId, id)
                        .list()
        ).getFirst();
        if(transaction == null) {
            throw new NotFoundException();
        }
        return new TransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse update(UUID userId, UUID id, TransactionRequest request) {
        //TODO verificar sé será necessario mudar a taxa de cambio!
        Transaction transaction = repository.find(
                "user.id = ?1 and id = ?2",
                userId, id
        ).firstResultOptional().orElseThrow(NotFoundException::new);
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId());
            transaction.setCategory(category);
        }
        if (request.getPaymentMethodId() != null) {
            PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId());
            transaction.setPaymentMethod(paymentMethod);
        }
        if (request.getDate() != null) {
            transaction.setDate(request.getDate());
        }
        if (request.getAmount() != null) {
            //TODO isso precisa de atenção!
            transaction.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            transaction.setType(request.getType());
        }
        if (request.getCurrency() != null) {
            transaction.setCurrency(request.getCurrency());
        }
        if (request.getState() != null) {
            transaction.setState(request.getState());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        repository.persist(transaction);
        return new TransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse create(UUID userId, TransactionRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        Optional<Category> optCat = categoryRepository.findByIdOptional(request.getCategoryId());
        Optional<PaymentMethod> optPayMet = paymentMethodRepository.findByIdOptional(request.getPaymentMethodId());
        if (optCat.isEmpty() || optPayMet.isEmpty()) {
            throw new NotFoundException("Element for creation not found");
        }
        Transaction tr = new Transaction();
        tr.setUser(user);
        tr.setType(request.getType());
        tr.setState(request.getState());
        tr.setDescription(request.getDescription());
        tr.setDate(request.getDate());
        tr.setCategory(optCat.get());
        tr.setPaymentMethod(optPayMet.get());
        //Amount sempre precisa ser salvo em BASE[USD]!
        String currency = request.getCurrency();
        tr.setCurrency(currency);
        tr.setDefaultAmount(request.getAmount());
        BigDecimal rate = exchangeService.getRate(currency);
        BigDecimal amountUSD = request.getAmount().divide(rate, 12, RoundingMode.HALF_UP);
        tr.setAmount(amountUSD);
        repository.persist(tr);
        TransactionResponse response = new TransactionResponse(tr);
        Settings settings = settingsService.get(user.getId());
        response.setAmount(request.getAmount().multiply(exchangeService.getRate(settings.getCurrency())));
        return response;
    }

}
