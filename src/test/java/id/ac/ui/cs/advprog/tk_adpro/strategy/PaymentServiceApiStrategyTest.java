package id.ac.ui.cs.advprog.tk_adpro.strategy;

import id.ac.ui.cs.advprog.tk_adpro.exception.PaymentServiceException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@TestPropertySource(properties = {
    "payment.service.check-balance-url=http://dummy-payment-service.com/api/wallet",
    "payment.service.process-payment-url=http://dummy-payment-service.com/api/wallet/donate"
})
class PaymentServiceApiStrategyTest {
    private static RestTemplate restTemplate;
    private static PaymentStrategy paymentStrategy;

    @Value("${payment.service.check-balance-url}")
    private String CHECK_BALANCE_URL;

    @Value("${payment.service.process-payment-url}")
    private String PROCESS_PAYMENT_URL;

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

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("balance", 100.5);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", dataMap);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        String expectedUrl = CHECK_BALANCE_URL + "?userId=" + donaturId;
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        double balance = paymentStrategy.checkBalance(donaturId);

        assertEquals(100.5, balance);
    }

    @Test
    void testCheckBalanceSuccessWithIntegerBalance() {
        UUID donaturId = UUID.randomUUID();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("balance", 75); // Integer value

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", dataMap);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        String expectedUrl = CHECK_BALANCE_URL + "?userId=" + donaturId;
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        double balance = paymentStrategy.checkBalance(donaturId);

        assertEquals(75.0, balance);
    }

    @Test
    void testCheckBalanceInvalidBalanceFormat() {
        UUID donaturId = UUID.randomUUID();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("balance", "invalid_string");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", dataMap);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        String expectedUrl = CHECK_BALANCE_URL + "?userId=" + donaturId;
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentStrategy.checkBalance(donaturId)
        );

        assertTrue(exception.getMessage().contains("Invalid balance format received from payment service"));
    }

    @Test
    void testCheckBalanceMissingDataField() {
        UUID donaturId = UUID.randomUUID();

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("someOtherField", "value");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        String expectedUrl = CHECK_BALANCE_URL + "?userId=" + donaturId;
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentStrategy.checkBalance(donaturId)
        );

        assertTrue(exception.getMessage().contains("Missing or invalid 'data' field in response"));
    }

    @Test
    void testCheckBalanceInvalidDataField() {
        UUID donaturId = UUID.randomUUID();

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", "not_a_map");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        String expectedUrl = CHECK_BALANCE_URL + "?userId=" + donaturId;
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentStrategy.checkBalance(donaturId)
        );

        assertTrue(exception.getMessage().contains("Missing or invalid 'data' field in response"));
    }

    @Test
    void testCheckBalanceHttpClientError() {
        UUID donaturId = UUID.randomUUID();

        String expectedUrl = CHECK_BALANCE_URL + "?userId=" + donaturId;
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentStrategy.checkBalance(donaturId)
        );

        assertTrue(exception.getMessage().contains("Payment service returned an error: 400 BAD_REQUEST"));
    }

    @Test
    void testCheckBalanceResourceAccessException() {
        UUID donaturId = UUID.randomUUID();

        String expectedUrl = CHECK_BALANCE_URL + "?userId=" + donaturId;
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentStrategy.checkBalance(donaturId)
        );

        assertTrue(exception.getMessage().contains("Cannot connect to payment service"));
    }

    @Test
    void testCheckBalanceUnexpectedException() {
        UUID donaturId = UUID.randomUUID();

        String expectedUrl = CHECK_BALANCE_URL + "?userId=" + donaturId;
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(null);
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RuntimeException("Unexpected error"));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentStrategy.checkBalance(donaturId)
        );

        assertTrue(exception.getMessage().contains("Unexpected error during balance check"));
    }

    @Test
    void testCheckBalanceStatusNotOk() {
        UUID donaturId = UUID.randomUUID();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("balance", 50.0);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", dataMap);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);

        String expectedUrl = CHECK_BALANCE_URL + "?userId=" + donaturId;
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
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

        String expectedUrl = CHECK_BALANCE_URL + "?userId=" + donaturId;
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
                paymentStrategy.checkBalance(donaturId)
        );

        assertTrue(exception.getMessage().contains("Failed to check balance from payment service"));
    }

    @Test
    void testProcessPaymentSuccess() throws InterruptedException {
        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        int amount = 50;

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(PROCESS_PAYMENT_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        CompletableFuture<Void> future = paymentStrategy.processPayment(donationId, campaignId, donaturId, amount);
        assertDoesNotThrow(() -> future.get());

        verify(restTemplate, times(1)).exchange(
            eq(PROCESS_PAYMENT_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        );
    }

    @Test
    void testProcessPaymentDeclined() throws InterruptedException {
        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        int amount = 50;

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", false);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(PROCESS_PAYMENT_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        CompletableFuture<Void> future = paymentStrategy.processPayment(donationId, campaignId, donaturId, amount);

        assertDoesNotThrow(() -> future.get());

        verify(restTemplate, times(1)).exchange(
            eq(PROCESS_PAYMENT_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        );
    }

    @Test
    void testProcessPaymentInvalidResponseFormat() {
        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        int amount = 50;

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", "not_a_boolean");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(PROCESS_PAYMENT_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        CompletableFuture<Void> future = paymentStrategy.processPayment(donationId, campaignId, donaturId, amount);

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(PaymentServiceException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Invalid response format from payment service"));
    }

    @Test
    void testProcessPaymentHttpClientError() {
        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        int amount = 50;

        when(restTemplate.exchange(
            eq(PROCESS_PAYMENT_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        CompletableFuture<Void> future = paymentStrategy.processPayment(donationId, campaignId, donaturId, amount);

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(PaymentServiceException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Payment service returned an error"));
    }

    @Test
    void testProcessPaymentResourceAccessException() {
        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        int amount = 50;

        when(restTemplate.exchange(
            eq(PROCESS_PAYMENT_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        CompletableFuture<Void> future = paymentStrategy.processPayment(donationId, campaignId, donaturId, amount);

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(PaymentServiceException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Cannot connect to payment service"));
    }

    @Test
    void testProcessPaymentStatusNotOk() {
        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        int amount = 75;

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.SERVICE_UNAVAILABLE);

        when(restTemplate.exchange(
            eq(PROCESS_PAYMENT_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        CompletableFuture<Void> future = paymentStrategy.processPayment(donationId, campaignId, donaturId, amount);

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(PaymentServiceException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Status: 503 SERVICE_UNAVAILABLE"));
    }

    @Test
    void testProcessPaymentNullBody() {
        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        int amount = 30;

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(PROCESS_PAYMENT_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        CompletableFuture<Void> future = paymentStrategy.processPayment(donationId, campaignId, donaturId, amount);

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(PaymentServiceException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Failed to process payment. Status"));
    }

    @Test
    void testProcessPaymentMissingSuccessField() {
        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        int amount = 40;

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", "completed"); // Missing "success" field

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(PROCESS_PAYMENT_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        CompletableFuture<Void> future = paymentStrategy.processPayment(donationId, campaignId, donaturId, amount);

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(PaymentServiceException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Invalid response format from payment service"));
    }
}