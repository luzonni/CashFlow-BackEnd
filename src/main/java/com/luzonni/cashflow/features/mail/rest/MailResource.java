package com.luzonni.cashflow.features.mail.rest;

import com.luzonni.cashflow.features.mail.service.MailService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@Path("mail")
@RolesAllowed("USER")
public class MailResource {

    private final MailService mailService;
    private final JsonWebToken jwt;

    public MailResource(
            MailService mailService,
            JsonWebToken jwt
    ) {
        this.mailService = mailService;
        this.jwt = jwt;
    }

    @GET
    public Response sendVerificationEmail() {
        UUID userId = UUID.fromString(jwt.getSubject());
        mailService.sendEmail(userId);
        return Response.ok().build();
    }

}
