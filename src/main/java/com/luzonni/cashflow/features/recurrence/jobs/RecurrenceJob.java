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

    private final RecurrenceRepository recurrenceRepository;
    private final RecurrenceRecordService recurrenceRecordService;

    public RecurrenceJob(
            RecurrenceRepository recurrenceRepository,
            RecurrenceRecordService recurrenceRecordService
    ) {
        this.recurrenceRepository = recurrenceRepository;
        this.recurrenceRecordService = recurrenceRecordService;
    }


    @Scheduled(every = "1s")
    void execute() {
        System.out.println("Executando recorrências...");
        //TODO FIX THAT
//        List<Recurrence> recurrences = recurrenceRepository.find(
//                "status = ?1",
//                RecurrenceStatus.ACTIVE
//        ).list();
//        for (Recurrence recurrence : recurrences) {
//            recurrenceRecordService.execRecurrence(recurrence);
//        }
    }

}
