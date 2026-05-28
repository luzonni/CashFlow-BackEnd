package com.luzonni.cashflow.features.recurrence.service;

import com.luzonni.cashflow.features.recurrence.domain.Recurrence;
import com.luzonni.cashflow.features.recurrence.domain.RecurrenceRecord;
import com.luzonni.cashflow.features.recurrence.enums.RecurrenceRecordStatus;
import com.luzonni.cashflow.features.recurrence.enums.RecurrenceStatus;
import com.luzonni.cashflow.features.recurrence.repository.RecurrenceRecordRepository;
import com.luzonni.cashflow.features.recurrence.repository.RecurrenceRepository;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import com.luzonni.cashflow.features.transaction.repository.TransactionRepository;
import com.luzonni.cashflow.shared.type.TransactionState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RecurrenceRecordService {

    private final TransactionRepository transactionRepository;
    private final RecurrenceRepository recurrenceRepository;
    private final RecurrenceRecordRepository repository;

    public RecurrenceRecordService(
            RecurrenceRecordRepository repository,
            RecurrenceRepository recurrenceRepository,
            TransactionRepository transactionRepository
    ) {
        this.repository = repository;
        this.recurrenceRepository = recurrenceRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public List<RecurrenceRecord> create(Recurrence recurrence, LocalDate firstRecurrence) {
        List<RecurrenceRecord> records = new ArrayList<>();
        long interval = recurrence.getIntervalValue();
        long occurrences = recurrence.getMaxOccurrences();
        LocalDate date = firstRecurrence;
        for (int i = 0; i < occurrences; i++) {
            RecurrenceRecord recurrenceRecord = new RecurrenceRecord();
            recurrenceRecord.setRecurrence(recurrence);
            recurrenceRecord.setScheduledTo(date);
            recurrenceRecord.setAmount(recurrence.getAmount());
            recurrenceRecord.setOccurrenceNumber(i + 1);
            recurrenceRecord.setStatus(RecurrenceRecordStatus.PENDING);
            repository.persist(recurrenceRecord);
            records.add(recurrenceRecord);
            date = switch (recurrence.getFrequency()) {
                case DAILY -> date.plusDays(interval);
                case WEEKLY -> date.plusWeeks(interval);
                case MONTHLY -> date.plusMonths(interval);
                case YEARLY -> date.plusYears(interval);
            };
        }
        return records;
    }

    public List<RecurrenceRecord> list(Recurrence recurrence) {
        UUID id = recurrence.getId();
        return repository.find(
                "id = ?1",
                id
        ).list();
    }

    @Transactional
    public void updateRecords(Recurrence recurrence, BigDecimal amount) {
        List<RecurrenceRecord> records = recurrence.getRecords();
        for (RecurrenceRecord record : records) {
            if (record.getStatus().equals(RecurrenceRecordStatus.PENDING)) {
                record.setAmount(amount);
                repository.persist(record);
            }
        }
    }

    @Transactional
    public boolean execRecords(Recurrence recurrence) {
        ZoneId zoneId = ZoneId.of(recurrence.getTimezone());
        List<RecurrenceRecord> recurrenceRecords = repository.find(
                "recurrence.id = ?1 and status != EXECUTED",
                recurrence.getId()
        ).list();
        if (recurrenceRecords.isEmpty()) {
            return true;
        }
        boolean complete = false;
        for (RecurrenceRecord recurrenceRecord : recurrenceRecords) {
            LocalDate now = LocalDate.now(zoneId);
            if (recurrenceRecord.getScheduledTo().isBefore(now) || recurrenceRecord.getScheduledTo().isEqual(now)) {
                if (recurrence.getStatus().equals(RecurrenceStatus.ACTIVE)) {
                    try {
                        Transaction transaction = getTransaction(recurrence, now);
                        transactionRepository.persist(transaction);
                        recurrenceRecord.setTransaction(transaction);
                        recurrenceRecord.setExecutedAt(LocalDateTime.now(zoneId));
                        recurrenceRecord.setStatus(RecurrenceRecordStatus.EXECUTED);
                        repository.persist(recurrenceRecord);
                        if(recurrenceRecords.size() == 1) {
                            complete = true;
                        }
                    }catch (Exception e){
                        recurrenceRecord.setStatus(RecurrenceRecordStatus.FAILED);
                    }
                } else if (recurrence.getStatus().equals(RecurrenceStatus.PAUSED)) {
                    recurrenceRecord.setStatus(RecurrenceRecordStatus.SKIPPED);
                    repository.persist(recurrenceRecord);
                }
            }
        }
        return complete;
    }

    private Transaction getTransaction(Recurrence recurrence, LocalDate now) {
        Transaction transaction = new Transaction();
        transaction.setUser(recurrence.getUser());
        transaction.setDate(now);
        transaction.setState(TransactionState.CONFIRM);
        transaction.setType(recurrence.getType());
        transaction.setCategory(recurrence.getCategory());
        transaction.setPaymentMethod(recurrence.getPaymentMethod());
        transaction.setAmount(recurrence.getAmount());
        transaction.setCurrency(recurrence.getCurrency());
        transaction.setDescription(
                "Created by recurrence: \"" + recurrence.getName() + "\""
        );
        return transaction;
    }

}
