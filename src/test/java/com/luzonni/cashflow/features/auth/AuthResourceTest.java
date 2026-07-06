package com.luzonni.cashflow.features.auth;

import com.luzonni.cashflow.features.auth.dto.RegisterRequest;
import com.luzonni.cashflow.features.auth.rest.AuthResource;
import com.luzonni.cashflow.features.exception.dto.ErrorResponse;
import io.agroal.api.AgroalDataSource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.microprofile.config.ConfigProvider;

import java.time.LocalDate;

@QuarkusTest
@TestHTTPEndpoint(AuthResource.class)
public class AuthResourceTest {

    @Test
    @DisplayName("should return ok")
    public void register_ok() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("Paulo Oliveira");
        request.setPassword("97493Padu1!!");
        request.setEmail("paulo_oliva37@teste.com");
        request.setBirthday(LocalDate.of(1980, 1, 1));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("register")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("should return 400")
    public void register_bad() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("paduu");
        request.setPassword("97493Padu1!!");
        request.setEmail("paulo_oliva37@teste.com");

         ErrorResponse error = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("register")
                .then()
                .statusCode(400)
                .extract().response().as(ErrorResponse.class);

        assertEquals("VALIDATION_ERROR", error.code());
        assertEquals("Validation failed", error.message());
        assertEquals("Date of birth is required", error.errors().getFirst().message());
    }

}
