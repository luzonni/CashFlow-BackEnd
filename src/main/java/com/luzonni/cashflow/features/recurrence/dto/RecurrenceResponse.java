package com.luzonni.cashflow.features.recurrence.dto;

import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodResponse;
import com.luzonni.cashflow.features.recurrence.domain.Recurrence;
import com.luzonni.cashflow.features.recurrence.enums.RecurrenceRecordStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class RecurrenceResponse {

    private UUID id;
    private String name;
    private String description;
    private String status;

    private CategoryResponse category;
    private PaymentMethodResponse paymentMethod;

    private String type;
    private BigDecimal amount;
    private String currency;

    private String frequency;
    private Integer interval;
    private Integer occurrencesProduced;
    private Integer maxOccurrences;

    private List<RecurrenceRecordResponse> records;

    public RecurrenceResponse(Recurrence recurrence) {
        this.id = recurrence.getId();
        this.name = recurrence.getName();
        this.description = recurrence.getDescription();
        this.status = recurrence.getStatus().name();
        this.category = new CategoryResponse(recurrence.getCategory());
        this.paymentMethod = new PaymentMethodResponse(recurrence.getPaymentMethod());
        this.type = recurrence.getType().toString();
        this.amount = recurrence.getAmount();
        this.currency = recurrence.getCurrency();
        this.frequency = recurrence.getFrequency().toString();
        this.interval = recurrence.getIntervalValue();
        this.maxOccurrences = recurrence.getMaxOccurrences();
        this.records = recurrence.getRecords()
                .stream()
                .map(RecurrenceRecordResponse::new)
                .toList();
        this.occurrencesProduced = this.records
                .stream()
                .filter((r) ->
                        r.getStatus().equalsIgnoreCase(RecurrenceRecordStatus.EXECUTED.name())
                )
                .toList()
                .size();
    }

}
