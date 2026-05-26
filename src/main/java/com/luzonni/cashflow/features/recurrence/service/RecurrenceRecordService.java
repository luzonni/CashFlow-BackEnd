package com.luzonni.cashflow.features.recurrence.service;

import com.luzonni.cashflow.features.recurrence.domain.Recurrence;
import com.luzonni.cashflow.features.recurrence.domain.RecurrenceRecord;
import com.luzonni.cashflow.features.recurrence.enums.Status;
import com.luzonni.cashflow.features.recurrence.repository.RecurrenceRecordRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RecurrenceRecordService {

    private final RecurrenceRecordRepository repository;

    public RecurrenceRecordService(RecurrenceRecordRepository repository) {
        this.repository = repository;
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
            recurrenceRecord.setStatus(Status.PENDING);
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

}
