package com.luzonni.cashflow.features.payment_rules.helper;

import com.luzonni.cashflow.features.payment_rules.domain.PaymentRuleType;
import jakarta.validation.constraints.NotEmpty;

public class PaymentConfig {

    public boolean validate(String config) {
        String[] values = config.split("/");
        if(values.length != 3) {
            return false;
        }
        try {
            Integer.parseInt(values[0]);
            Integer.parseInt(values[2]);
            return values[1].equalsIgnoreCase("true") || values[1].equalsIgnoreCase("false");
        }catch (Exception e){
            return false;
        }
    }

    public int input(PaymentRuleType ruleType, String config, int input) {
        return switch (ruleType) {
            case FIXED_RATE -> fixed(config, input);
            case VARIABLE_RATE -> percent(config, input);
            case null, default -> input;
        };
    }

    private int fixed(String config, int input) {
        String[] values = config.split("/");
        int fixedRate = Integer.parseInt(values[0]);
        boolean actived = Boolean.parseBoolean(values[1]);
        if (actived) {
            return input + fixedRate;
        }
        return input;
    }

    private int percent(String config, int input) {
        String[] values = config.split("/");
        int percentage = Integer.parseInt(values[0]);
        return input + input * percentage;
    }


}
