package com.luzonni.cashflow.features.payment_rules.rest;

import com.luzonni.cashflow.features.payment_rules.dto.PaymentRuleRequest;
import com.luzonni.cashflow.features.payment_rules.dto.PaymentRuleResponse;
import com.luzonni.cashflow.features.payment_rules.service.PaymentRulesService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@RolesAllowed("USER")
@Path("payment_rules")
public class PaymentRulesResource {

    private final PaymentRulesService service;
    private final JsonWebToken jwt;

    public PaymentRulesResource(
            PaymentRulesService paymentRulesService,
            JsonWebToken jwt
    ) {
        this.service = paymentRulesService;
        this.jwt = jwt;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPaymentRuleToPaymentMethod(PaymentRuleRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        PaymentRuleResponse response = service.create(userId, request);
        return Response.ok(response).build();
    }

}
