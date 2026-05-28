package com.luzonni.cashflow.features.recurrence.jobs;

import com.luzonni.cashflow.features.recurrence.domain.Recurrence;
import com.luzonni.cashflow.features.recurrence.domain.RecurrenceRecord;
import com.luzonni.cashflow.features.recurrence.enums.RecurrenceStatus;
import com.luzonni.cashflow.features.recurrence.repository.RecurrenceRecordRepository;
import com.luzonni.cashflow.features.recurrence.repository.RecurrenceRepository;
import com.luzonni.cashflow.features.recurrence.service.RecurrenceRecordService;
import com.luzonni.cashflow.features.recurrence.service.RecurrenceService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RecurrenceJob {

    private final RecurrenceService service;

    public RecurrenceJob(
            RecurrenceService service
    ) {
        this.service = service;
    }


    @Scheduled(every = "12h")
    void execute() {
        System.out.println("Executando recorrências...");
        service.transactionLauncher();
    }

}
