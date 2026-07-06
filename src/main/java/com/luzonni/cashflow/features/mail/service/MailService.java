package com.luzonni.cashflow.features.mail.service;

import com.luzonni.cashflow.features.exception.domain.AppException;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MailService {

    private final Mailer mailer;
    private final UserRepository userRepository;

    @Inject
    public MailService(
            Mailer mailer,
            UserRepository userRepository
    ) {
        this.mailer = mailer;
        this.userRepository = userRepository;
    }

    public void sendEmail(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new AppException(
                    Response.Status.BAD_REQUEST,
                    ErrorCode.ENTITY_NOT_FOUND,
                    "User not found"
            );
        }
        User user = optionalUser.get();
        String link = "http://localhost:8080/auth/verify?token=" + user.getVerificationToken();
        mailer.send(
                Mail.withHtml(
                        user.getEmail(),
                        "Confirm your email",
                        "<p>Clique on link to confirm email: <a href='" + link + "'>Confirm email</a></p>"
                )
        );
    }

    @Transactional
    public void verify(String token) {
        User user = userRepository.find("verificationToken", token).firstResult();
        if(user == null) {
            throw new AppException(Response.Status.BAD_REQUEST, ErrorCode.INVALID_VERIFICATION_TOKEN, "Invalid token");
        }
        if(user.getVerificationTokenExpiresAt() == null || user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            user.createVerificationToken();
            userRepository.persist(user);
            throw new AppException(Response.Status.BAD_REQUEST, ErrorCode.EXPIRED_VERIFICATION_TOKEN, "Expired token");
        }
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        userRepository.persist(user);
    }

}
