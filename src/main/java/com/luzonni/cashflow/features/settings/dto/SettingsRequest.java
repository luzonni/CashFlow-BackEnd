package com.luzonni.cashflow.features.settings.dto;

import lombok.Data;

@Data
public class SettingsRequest {

    private String theme;
    private String locale;
    private String currency;

}
