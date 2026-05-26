package com.luzonni.cashflow.features.recurrence.jobs;

import com.luzonni.cashflow.features.recurrence.service.RecurrenceRecordService;
import com.luzonni.cashflow.features.recurrence.service.RecurrenceService;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RecurrenceJob {

    private final RecurrenceService recurrenceService;
    private final RecurrenceRecordService recurrenceRecordService;

    public RecurrenceJob(
            RecurrenceService recurrenceService,
            RecurrenceRecordService recurrenceRecordService
    ) {
        this.recurrenceService = recurrenceService;
        this.recurrenceRecordService = recurrenceRecordService;
    }


    @Scheduled(every = "1s")
    void execute() {
        System.out.println("Executando recorrências...");
    }

}
