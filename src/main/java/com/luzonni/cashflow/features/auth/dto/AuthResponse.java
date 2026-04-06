package com.luzonni.cashflow.features.auth.dto;

import com.luzonni.cashflow.features.user.domain.User;
import lombok.Data;

@Data
public class AuthResponse {

    public boolean success;
    public User user;

    /*
    Access Token = pulseira de evento
        você entra e usa livremente
        mas expira rápido
    Refresh Token = documento oficial
        você usa pra pegar outra pulseira
        é verificado com muito mais cuidado
    */

}
