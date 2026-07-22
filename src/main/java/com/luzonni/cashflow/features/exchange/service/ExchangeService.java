package com.luzonni.cashflow.features.exchange.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luzonni.cashflow.features.exception.domain.AppException;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    private final AtomicReference<Map<String, BigDecimal>> ratesRef = new AtomicReference<>();

    @PostConstruct
    void init() {
        try {
            refreshRates();
        } catch (Exception e) {
            Log.error("Failed to load exchange rates at startup; retrying later.", e);
        }
    }

    @Scheduled(every = "12h")
    public synchronized void refreshRates() {
        try {
            JsonNode ratesNode = getRates();
            ratesRef.set(toRatesMap(ratesNode));
            Log.info("Refresh rates complete.");
        } catch (Exception e) {
            Log.info("Failed to load exchange rates.", e);
            throw new AppException(
                    Response.Status.BAD_REQUEST,
                    ErrorCode.RATE_ERROR_OPERATION,
                    e.getMessage()
            );
        }
    }

    private Map<String, BigDecimal> toRatesMap(JsonNode ratesNode) throws Exception {
        if (ratesNode == null || ratesNode.isMissingNode()) {
            throw new IllegalStateException("The exchange rate API response does not contain the expected rates node.");
        }
        Map<String, BigDecimal> result = new HashMap<>();
        for (String c : CURRENCY) {
            String key = c.toLowerCase();
            JsonNode value = ratesNode.get(key);
            if (value == null || value.isNull()) {
                throw new Exception("API without declared currency");
            }
            result.put(c, value.decimalValue());
        }
        return result;
    }

    private JsonNode getRates() throws Exception {
        String url = String.format(URL_TEMPLATE, ExchangeService.BASE.toLowerCase());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("API return status " + response.statusCode() + ": " + response.body());
        }
        JsonNode root = mapper.readTree(response.body());
        JsonNode baseNode = root.get(ExchangeService.BASE.toLowerCase());
        if(baseNode == null) {
            throw new IllegalStateException("Resposta não contém o nó base '" + BASE.toLowerCase() + "'");
        }
        return baseNode;
    }

    // Uses

    public List<String> getCurrency() {
        return Arrays.asList(CURRENCY);
    }

    public synchronized BigDecimal getRate(String currency) throws AppException {
        Map<String, BigDecimal> rates = ratesRef.get();
        if (rates == null) {
            refreshRates();
            rates = ratesRef.get();
            if (rates == null) {
                throw new AppException(
                        Response.Status.BAD_REQUEST,
                        ErrorCode.RATE_ERROR_OPERATION,
                        "Rates not present"
                );
            }
        }
        BigDecimal rate = rates.get(currency);
        if (rate == null) {
            throw new AppException(
                    Response.Status.BAD_REQUEST,
                    ErrorCode.RATE_ERROR_OPERATION,
                    "Currency not supported: " + currency
            );
        }
        return rate;
    }

    public BigDecimal getUSD(String currency, BigDecimal amount) {
        BigDecimal rate = getRate(currency);
        return amount.divide(rate, 8, RoundingMode.HALF_EVEN);
    }

}
