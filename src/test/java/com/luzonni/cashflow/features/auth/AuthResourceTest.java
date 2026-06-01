package com.luzonni.cashflow.features.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class AuthResourceTest {


    @Test
    @DisplayName("Can be register a user with ok!")
    public void testeRegister() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username": "luzonni",
                            "email": "lucas.z.200375@teste.com",
                            "password": "558300Lz!!",
                            "birthday": "2003-05-07"
                        }
                        """)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(200);
    }

}
