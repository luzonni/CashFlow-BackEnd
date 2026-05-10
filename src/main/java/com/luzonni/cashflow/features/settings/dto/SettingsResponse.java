package com.luzonni.cashflow.features.settings.dto;

import com.luzonni.cashflow.features.settings.domain.Settings;
import lombok.Getter;

@Getter
public class SettingsResponse {

    private final String locale;
    private final String currency;
    private final String theme;

    public SettingsResponse(Settings settings) {
        this.locale = settings.getLocale();
        this.currency =  settings.getCurrency();
        this.theme = settings.getTheme();
    }

}
