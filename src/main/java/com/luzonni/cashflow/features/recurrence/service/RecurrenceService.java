package com.luzonni.cashflow.features.recurrence.service;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.features.exception.domain.AppException;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import com.luzonni.cashflow.features.payment_method.domain.PaymentMethod;
import com.luzonni.cashflow.features.payment_method.repository.PaymentMethodRepository;
import com.luzonni.cashflow.features.recurrence.domain.Recurrence;
import com.luzonni.cashflow.features.recurrence.domain.RecurrenceRecord;
import com.luzonni.cashflow.features.recurrence.dto.RecurrenceRequest;
import com.luzonni.cashflow.features.recurrence.dto.RecurrenceResponse;
import com.luzonni.cashflow.features.recurrence.repository.RecurrenceRepository;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RecurrenceService {

    private final RecurrenceRepository repository;
    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    private final RecurrenceRecordService recurrenceRecordService;

    public RecurrenceService(
            RecurrenceRepository repository,
            CategoryRepository categoryRepository,
            PaymentMethodRepository paymentMethodRepository,
            UserRepository userRepository,
            RecurrenceRecordService recurrenceRecordService
    ) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
        this.recurrenceRecordService = recurrenceRecordService;
    }

    public List<RecurrenceResponse> listAll(UUID userId) {
        List<Recurrence> list = repository.find(
                "user.id = ?1",
                userId
        ).list();
        /*
            TODO:
            É preciso saber a quantidade de lançamentos de cada uma, e para isso, é
            necessario saber quantos recoreds foram lançados e finalizados (status = "EXECUTED")
         */
        return list.stream().map(RecurrenceResponse::map).toList();
    }

    @Transactional
    public RecurrenceResponse create(UUID userId, RecurrenceRequest request) {
        Recurrence recurrence = new Recurrence();
        recurrence.setName(request.getName());
        recurrence.setDescription(request.getDescription());
        recurrence.setAmount(request.getAmount());
        recurrence.setFrequency(request.getFrequency());
        recurrence.setType(request.getType());
        recurrence.setIntervalValue(request.getInterval());
        recurrence.setMaxOccurrences(request.getMaxOccurrences());
        recurrence.setTimezone(request.getTimeZone());
        recurrence.setCurrency(request.getCurrency());


        Optional<User> optUser = userRepository.findById(userId);
        Optional<Category> optCategory = categoryRepository.find(
                "id =? 1 and deleted = false",
                request.getCategoryId()
        ).firstResultOptional();
        Optional<PaymentMethod> optPaymentMethod = paymentMethodRepository.find(
                "id = ?1 and deleted = false",
                request.getPaymentMethodId()
        ).firstResultOptional();
        if (optCategory.isEmpty() || optPaymentMethod.isEmpty() || optUser.isEmpty()) {
            throw new AppException(
                    Response.Status.NOT_FOUND,
                    ErrorCode.ENTITY_NOT_FOUND,
                    "Entity not found or excluded"
            );
        }
        recurrence.setCategory(optCategory.get());
        recurrence.setPaymentMethod(optPaymentMethod.get());
        recurrence.setUser(optUser.get());
        LocalDate firstRecurrence = request.getFirstRecord();
        if(firstRecurrence.isBefore(LocalDate.now())) {
            throw new AppException(
                    Response.Status.CONFLICT,
                    ErrorCode.INVALID_OPERATION,
                    "Date is before now"
            );
        }
        recurrence.setRecords(recurrenceRecordService.create(
                recurrence,
                firstRecurrence
        ));

        repository.persist(recurrence);
        return RecurrenceResponse.map(recurrence);
    }

}
