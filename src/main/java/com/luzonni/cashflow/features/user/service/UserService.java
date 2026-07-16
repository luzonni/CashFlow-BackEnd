package com.luzonni.cashflow.features.user.service;

import com.luzonni.cashflow.features.exception.domain.AppException;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import com.luzonni.cashflow.features.exchange.service.ExchangeService;
import com.luzonni.cashflow.features.mail.service.MailService;
import com.luzonni.cashflow.features.settings.domain.Settings;
import com.luzonni.cashflow.features.settings.dto.SettingsRequest;
import com.luzonni.cashflow.features.settings.service.SettingsService;
import com.luzonni.cashflow.features.transaction.dto.TransactionResponse;
import com.luzonni.cashflow.features.transaction.service.TransactionService;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.dto.AmountResponse;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import com.luzonni.cashflow.shared.type.TransactionState;
import com.luzonni.cashflow.shared.type.TransactionType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    private final UserRepository repository;
    private final SettingsService settingsService;
    private final TransactionService transactionService;
    private final MailService mailService; //Not in Porduction
    private final ExchangeService exchangeService;

    public UserService(
            UserRepository repository,
            SettingsService settingsService,
            MailService mailService,
            TransactionService transactionService,
            ExchangeService exchangeService
    ) {
        this.repository = repository;
        this.settingsService = settingsService;
        this.mailService = mailService;
        this.transactionService = transactionService;
        this.exchangeService = exchangeService;
    }

    @Transactional
    public void changeSettings(UUID userId, SettingsRequest request) {
        User user = repository.findById(userId).orElseThrow();
        settingsService.change(user, request);
    }

    @Transactional
    public User create(String username, String email, LocalDate birthday, String password) {
        User user = new User(
                username,
                email,
                birthday,
                password
        );
        try {
            repository.persist(user);
        } catch (AppException appException) {
            throw new AppException(
                    Response.Status.BAD_REQUEST,
                    ErrorCode.USER_ALREADY_EXISTS,
                    "User already exists."
            );
        }
        //mailService.sendEmail(user.getId());
        return user;
    }

    public AmountResponse getAmount(UUID userId) {
        User user = repository.findById(userId).orElseThrow();
        Settings settings = settingsService.get(user.getId());
        String userCurrency = settings.getCurrency();

        List<TransactionResponse> list = transactionService.listAll(userId);
        BigDecimal amount = BigDecimal.valueOf(0);
        for (TransactionResponse tr : list) {
            BigDecimal trAmount = tr.getAmount();
            TransactionType trType = TransactionType.valueOf(tr.getType());
            if (TransactionState.valueOf(tr.getState()).equals(TransactionState.CONFIRM)) {
                if (trType.equals(TransactionType.EXPENSE)) {
                    amount = amount.subtract(trAmount);
                } else {
                    amount = amount.add(trAmount);
                }
            }
        }
        return new AmountResponse(
                amount.setScale(2, RoundingMode.HALF_UP),
                userCurrency
        );
    }

}
