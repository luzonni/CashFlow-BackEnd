package com.luzonni.cashflow.features.exchange.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luzonni.cashflow.features.exception.domain.AppException;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@ApplicationScoped
public class ExchangeService {

    private static final String URL_TEMPLATE = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/%s.min.json";

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    private final ObjectMapper mapper = new ObjectMapper();

    private final static String BASE = "USD";
    private final static String[] CURRENCY = {
            "USD",
            "BRL",
            "EUR",
            "GBP",
            "JPY",
            "CLP",
            "CNY",
            "CHF",
            "CAD",
            "AUD",
            "ARS",
            "MXN"
    };
    private static Map<String, BigDecimal> rates = null;

    @Scheduled(every = "12h")
    public void getter() {
        System.out.println("Getting rates");
        try {
            rates = toRatesMap(getRates());
            System.out.println("Rates got");
        } catch (Exception e) {
            System.err.println("Error getting rates: " + e.getMessage());
            throw new AppException( // TODO melhorar essa esception
                    Response.Status.BAD_REQUEST,
                    ErrorCode.RATE_ERROR_OPERATION,
                    e.getMessage()
            );
        }
    }

    private Map<String, BigDecimal> toRatesMap(JsonNode ratesNode) {
        Map<String, BigDecimal> result = new HashMap<>();
        for(String c : CURRENCY) {
            String key = c.toLowerCase();
            result.put(c, ratesNode.get(key).decimalValue());
        }
        return result;
    }

    private JsonNode getRates() throws Exception {
        String url = String.format(URL_TEMPLATE, ExchangeService.BASE.toLowerCase());
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(response.body());
        return root.get(ExchangeService.BASE.toLowerCase());
    }

    //Uses

    public List<String> getCurrency() {
        return Arrays.asList(CURRENCY);
    }

    public BigDecimal getRate(String currency) throws AppException {
        if (rates == null) {
            throw new AppException(
                    Response.Status.BAD_REQUEST,
                    ErrorCode.RATE_ERROR_OPERATION,
                    "Rates not present"
            );
        }
        return rates.get(currency);
    }

}
