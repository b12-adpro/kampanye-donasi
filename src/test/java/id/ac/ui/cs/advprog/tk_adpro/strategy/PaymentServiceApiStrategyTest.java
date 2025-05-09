package id.ac.ui.cs.advprog.tk_adpro.strategy;

import id.ac.ui.cs.advprog.tk_adpro.exception.PaymentServiceException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

class PaymentServiceApiStrategyTest {
    private RestTemplate restTemplate;
    private PaymentStrategy paymentStrategy;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
        restTemplate = mock(RestTemplate.class);

        when(restTemplateBuilder.connectTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.readTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        paymentStrategy = new PaymentServiceApiStrategy(restTemplateBuilder);
    }

    @Test
    void testCheckBalanceSuccess() {
        UUID donaturId = UUID.randomUUID();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("balance", 100);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
            eq("http://dummy-payment-service.com/api/checkBalance"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        int balance = paymentStrategy.checkBalance(donaturId);
        assertEquals(100, balance);
    }

    @Test
    void testCheckBalanceInvalidFormat() {
        UUID donaturId = UUID.randomUUID();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("balance", "invalid");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
            eq("http://dummy-payment-service.com/api/checkBalance"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            paymentStrategy.checkBalance(donaturId)
        );
        assertTrue(exception.getMessage().contains("Invalid balance format"));
    }

    @Test
    void testCheckBalanceHttpClientError() {
        UUID donaturId = UUID.randomUUID();

        when(restTemplate.exchange(
            eq("http://dummy-payment-service.com/api/checkBalance"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            paymentStrategy.checkBalance(donaturId)
        );
        assertTrue(exception.getMessage().contains("Payment service returned an error"));
    }

    @Test
    void testCheckBalanceResourceAccessException() {
        UUID donaturId = UUID.randomUUID();

        when(restTemplate.exchange(
            eq("http://dummy-payment-service.com/api/checkBalance"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            paymentStrategy.checkBalance(donaturId)
        );
        assertTrue(exception.getMessage().contains("Cannot connect to payment service"));
    }

    @Test
    void testCheckBalanceStatusNotOk() {
        UUID donaturId = UUID.randomUUID();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("balance", 50);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(
            eq("http://dummy-payment-service.com/api/checkBalance"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            paymentStrategy.checkBalance(donaturId)
        );

        assertTrue(exception.getMessage().contains("Status: 500 INTERNAL_SERVER_ERROR"));
    }

    @Test
    void testCheckBalanceNullBody() {
        UUID donaturId = UUID.randomUUID();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
            eq("http://dummy-payment-service.com/api/checkBalance"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            paymentStrategy.checkBalance(donaturId)
        );
        assertTrue(exception.getMessage().contains("Failed to check balance from payment service"));
    }

    @Test
    void testProcessPaymentSuccess() {
        UUID donaturId = UUID.randomUUID();
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

        boolean result = paymentStrategy.processPayment(donaturId, amount);
        assertTrue(result);
    }

    @Test
    void testProcessPaymentDeclined() {
        UUID donaturId = UUID.randomUUID();
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

        boolean result = paymentStrategy.processPayment(donaturId, amount);
        assertFalse(result);
    }

    @Test
    void testProcessPaymentInvalidFormat() {
        UUID donaturId = UUID.randomUUID();
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

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            paymentStrategy.processPayment(donaturId, amount)
        );
        assertTrue(exception.getMessage().contains("Invalid response format"));
    }

    @Test
    void testProcessPaymentHttpClientError() {
        UUID donaturId = UUID.randomUUID();
        int amount = 50;

        when(restTemplate.exchange(
            eq("http://dummy-payment-service.com/api/processPayment"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            paymentStrategy.processPayment(donaturId, amount)
        );
        assertTrue(exception.getMessage().contains("Payment service returned an error"));
    }

    @Test
    void testProcessPaymentResourceAccessException() {
        UUID donaturId = UUID.randomUUID();
        int amount = 50;

        when(restTemplate.exchange(
            eq("http://dummy-payment-service.com/api/processPayment"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            paymentStrategy.processPayment(donaturId, amount)
        );
        assertTrue(exception.getMessage().contains("Cannot connect to payment service"));
    }

    @Test
    void testProcessPaymentStatusNotOk() {
        UUID donaturId = UUID.randomUUID();
        int amount = 75;
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.SERVICE_UNAVAILABLE);

        when(restTemplate.exchange(
            eq("http://dummy-payment-service.com/api/processPayment"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            paymentStrategy.processPayment(donaturId, amount)
        );
        assertTrue(exception.getMessage().contains("Status: 503 SERVICE_UNAVAILABLE"));
    }

    @Test
    void testProcessPaymentNullBody() {
        UUID donaturId = UUID.randomUUID();
        int amount = 30;

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
            eq("http://dummy-payment-service.com/api/processPayment"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            paymentStrategy.processPayment(donaturId, amount)
        );
        assertTrue(exception.getMessage().contains("Failed to process payment. Status"));
    }
}