package com.luzonni.cashflow.features.auth.dto;

import lombok.Data;

@Data
public class TokenResponse {

    public String accessToken; // 5 a 15 min
    public String refreshToken; // 7 a 30 dias
    public String type;

    /*
    Access Token = pulseira de evento
        você entra e usa livremente
        mas expira rápido
    Refresh Token = documento oficial
        você usa pra pegar outra pulseira
        é verificado com muito mais cuidado
    */






}
