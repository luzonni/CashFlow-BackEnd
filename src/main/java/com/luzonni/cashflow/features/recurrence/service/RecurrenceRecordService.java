package com.luzonni.cashflow.features.recurrence.service;

import com.luzonni.cashflow.features.recurrence.domain.Recurrence;
import com.luzonni.cashflow.features.recurrence.domain.RecurrenceRecord;
import com.luzonni.cashflow.features.recurrence.enums.RecurrenceRecordStatus;
import com.luzonni.cashflow.features.recurrence.enums.RecurrenceStatus;
import com.luzonni.cashflow.features.recurrence.repository.RecurrenceRecordRepository;
import com.luzonni.cashflow.features.recurrence.repository.RecurrenceRepository;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import com.luzonni.cashflow.features.transaction.repository.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RecurrenceRecordService {

    private final TransactionRepository transactionRepository;
    private final RecurrenceRepository  recurrenceRepository;
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
    public void execRecurrence(Recurrence recurrence) {
        List<RecurrenceRecord> recurrenceRecords = repository.find(
                "id = ?1 and status = ?2",
                recurrence.getId(), RecurrenceRecordStatus.PENDING
        ).list();
        if (recurrenceRecords.isEmpty()) {
            recurrence.setStatus(RecurrenceStatus.ENDED);
            recurrenceRepository.persist(recurrence);
            return;
        }
        for(RecurrenceRecord recurrenceRecord : recurrenceRecords) {
            LocalDate now = LocalDate.now();
            if(recurrenceRecord.getScheduledTo().isBefore(now)) {
                Transaction transaction = getTransaction(recurrence, now);
                transactionRepository.persist(transaction);
                recurrenceRecord.setStatus(RecurrenceRecordStatus.EXECUTED);
                recurrenceRecord.setRecurrence(recurrence);
                recurrenceRecord.setExecutedAt(LocalDateTime.now());
            }
        }
    }

    private static Transaction getTransaction(Recurrence recurrence, LocalDate now) {
        Transaction transaction = new Transaction();
        transaction.setUser(recurrence.getUser());
        transaction.setDate(now);
        transaction.setType(recurrence.getType());
        transaction.setCategory(recurrence.getCategory());
        transaction.setAmount(recurrence.getAmount());
        transaction.setCurrency(recurrence.getCurrency());
        transaction.setDescription(
                "Created by recurrence: \"" + recurrence.getName() + "\""
        );
        return transaction;
    }

}
