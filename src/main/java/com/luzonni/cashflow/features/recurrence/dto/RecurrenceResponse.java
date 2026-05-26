package com.luzonni.cashflow.features.recurrence.dto;

import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodResponse;
import com.luzonni.cashflow.features.recurrence.domain.Recurrence;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecurrenceResponse {

    private String name;
    private String description;
    private String status;

    private CategoryResponse category;
    private PaymentMethodResponse paymentMethod;

    private String type;
    private BigDecimal amount;
    private String currency;

    private String frequency;
    private Integer maxOccurrences;
    private LocalDateTime nextExecutionAt;

    public static RecurrenceResponse map(Recurrence recurrence) {
        RecurrenceResponse recurrenceResponse = new RecurrenceResponse();

        recurrenceResponse.setName(recurrence.getName());
        recurrenceResponse.setDescription(recurrence.getDescription());
        recurrenceResponse.setStatus(recurrence.getStatus().name());
        recurrenceResponse.setCategory(new CategoryResponse(recurrence.getCategory()));
        recurrenceResponse.setPaymentMethod(new PaymentMethodResponse(recurrence.getPaymentMethod()));
        recurrenceResponse.setType(recurrence.getType().toString());
        recurrenceResponse.setAmount(recurrence.getAmount());
        recurrenceResponse.setCurrency(recurrence.getCurrency());
        recurrenceResponse.setFrequency(recurrence.getFrequency().toString());
        recurrenceResponse.setMaxOccurrences(recurrence.getMaxOccurrences());
        recurrenceResponse.setNextExecutionAt(recurrence.getNextExecutionAt());

        return recurrenceResponse;
    }

}
