package id.ac.ui.cs.advprog.tk_adpro.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import id.ac.ui.cs.advprog.tk_adpro.exception.PaymentServiceException;

class PaymentServiceApiStrategyTest {
    private RestTemplateBuilder restTemplateBuilder;
    private RestTemplate restTemplate;
    private PaymentServiceApiStrategy paymentServiceApiStrategy;

    @BeforeEach
    void setUp() {
        restTemplateBuilder = mock(RestTemplateBuilder.class);
        restTemplate = mock(RestTemplate.class);

        // Stub the builder to return our mocked RestTemplate.
        when(restTemplateBuilder.connectTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.readTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        paymentServiceApiStrategy = new PaymentServiceApiStrategy(restTemplateBuilder);
    }

    // ----- checkBalance Tests -----

    @Test
    void testCheckBalanceSuccess() {
        long donaturId = 1L;
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("balance", 100);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://dummy-payment-service.com/api/checkBalance"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        int balance = paymentServiceApiStrategy.checkBalance(donaturId);
        assertEquals(100, balance);
    }

    @Test
    void testCheckBalanceInvalidFormat() {
        long donaturId = 1L;
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("balance", "invalid");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://dummy-payment-service.com/api/checkBalance"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> {
            paymentServiceApiStrategy.checkBalance(donaturId);
        });
        assertTrue(exception.getMessage().contains("Invalid balance format"));
    }

    @Test
    void testCheckBalanceHttpClientError() {
        long donaturId = 1L;

        when(restTemplate.exchange(
                eq("http://dummy-payment-service.com/api/checkBalance"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> {
            paymentServiceApiStrategy.checkBalance(donaturId);
        });
        assertTrue(exception.getMessage().contains("Payment service returned an error"));
    }

    @Test
    void testCheckBalanceResourceAccessException() {
        long donaturId = 1L;

        when(restTemplate.exchange(
                eq("http://dummy-payment-service.com/api/checkBalance"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> {
            paymentServiceApiStrategy.checkBalance(donaturId);
        });
        assertTrue(exception.getMessage().contains("Cannot connect to payment service"));
    }

    // ----- processPayment Tests -----

    @Test
    void testProcessPaymentSuccess() {
        long donaturId = 1L;
        int amount = 50;
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://dummy-payment-service.com/api/processPayment"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        boolean result = paymentServiceApiStrategy.processPayment(donaturId, amount);
        assertTrue(result);
    }

    @Test
    void testProcessPaymentDeclined() {
        long donaturId = 1L;
        int amount = 50;
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", false);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://dummy-payment-service.com/api/processPayment"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        boolean result = paymentServiceApiStrategy.processPayment(donaturId, amount);
        assertFalse(result);
    }

    @Test
    void testProcessPaymentInvalidFormat() {
        long donaturId = 1L;
        int amount = 50;
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", "not a boolean");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://dummy-payment-service.com/api/processPayment"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> {
            paymentServiceApiStrategy.processPayment(donaturId, amount);
        });
        assertTrue(exception.getMessage().contains("Invalid response format"));
    }

    @Test
    void testProcessPaymentHttpClientError() {
        long donaturId = 1L;
        int amount = 50;

        when(restTemplate.exchange(
                eq("http://dummy-payment-service.com/api/processPayment"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> {
            paymentServiceApiStrategy.processPayment(donaturId, amount);
        });
        assertTrue(exception.getMessage().contains("Payment service returned an error"));
    }

    @Test
    void testProcessPaymentResourceAccessException() {
        long donaturId = 1L;
        int amount = 50;

        when(restTemplate.exchange(
                eq("http://dummy-payment-service.com/api/processPayment"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> {
            paymentServiceApiStrategy.processPayment(donaturId, amount);
        });
        assertTrue(exception.getMessage().contains("Cannot connect to payment service"));
    }
}