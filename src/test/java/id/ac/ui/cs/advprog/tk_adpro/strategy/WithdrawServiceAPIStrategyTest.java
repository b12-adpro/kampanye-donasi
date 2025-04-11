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

import id.ac.ui.cs.advprog.tk_adpro.exception.WithdrawServiceException;

class WithdrawServiceApiStrategyTest {
    private RestTemplateBuilder restTemplateBuilder;
    private RestTemplate restTemplate;
    private WithdrawServiceAPIStrategy withdrawServiceApiStrategy;

    @BeforeEach
    void setUp() {
        restTemplateBuilder = mock(RestTemplateBuilder.class);
        restTemplate = mock(RestTemplate.class);

        when(restTemplateBuilder.connectTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.readTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        withdrawServiceApiStrategy = new WithdrawServiceAPIStrategy(restTemplateBuilder);
    }

    @Test
    void testCheckBalanceSuccess() {
        String fundraiserId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("balance", 100);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://dummy-withdraw-service.com/api/checkBalance"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        int balance = withdrawServiceApiStrategy.checkBalance(fundraiserId);
        assertEquals(100, balance);
    }

    @Test
    void testCheckBalanceInvalidFormat() {
        String fundraiserId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("balance", "invalid");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://dummy-withdraw-service.com/api/checkBalance"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        WithdrawServiceException exception = assertThrows(WithdrawServiceException.class, () -> {
            withdrawServiceApiStrategy.checkBalance(fundraiserId);
        });
        assertTrue(exception.getMessage().contains("Invalid balance format"));
    }

    @Test
    void testCheckBalanceHttpClientError() {
        String fundraiserId = "eb558e9f-1c39-460e-8860-71af6af63bd6";

        when(restTemplate.exchange(
                eq("http://dummy-withdraw-service.com/api/checkBalance"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        WithdrawServiceException exception = assertThrows(WithdrawServiceException.class, () -> {
            withdrawServiceApiStrategy.checkBalance(fundraiserId);
        });
        assertTrue(exception.getMessage().contains("Withdraw service returned an error"));
    }

    @Test
    void testCheckBalanceResourceAccessException() {
        String fundraiserId = "eb558e9f-1c39-460e-8860-71af6af63bd6";

        when(restTemplate.exchange(
                eq("http://dummy-withdraw-service.com/api/checkBalance"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        WithdrawServiceException exception = assertThrows(WithdrawServiceException.class, () -> {
            withdrawServiceApiStrategy.checkBalance(fundraiserId);
        });
        assertTrue(exception.getMessage().contains("Cannot connect to withdraw service"));
    }

    @Test
    void testWithdrawMoneySuccess() {
        String fundraiserId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        int amount = 50;
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", true);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://dummy-withdraw-service.com/api/withdrawMoney"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        boolean result = withdrawServiceApiStrategy.withdrawMoney(fundraiserId, amount);
        assertTrue(result);
    }

    @Test
    void testWithdrawMoneyDeclined() {
        String fundraiserId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        int amount = 50;
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", false);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://dummy-withdraw-service.com/api/withdrawMoney"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        boolean result = withdrawServiceApiStrategy.withdrawMoney(fundraiserId, amount);
        assertFalse(result);
    }

    @Test
    void testWithdrawMoneyInvalidFormat() {
        String fundraiserId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        int amount = 50;
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("success", "not a boolean");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://dummy-withdraw-service.com/api/withdrawMoney"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        WithdrawServiceException exception = assertThrows(WithdrawServiceException.class, () -> {
            withdrawServiceApiStrategy.withdrawMoney(fundraiserId, amount);
        });
        assertTrue(exception.getMessage().contains("Invalid response format"));
    }

    @Test
    void testProcessPaymentHttpClientError() {
        String fundraiserId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        int amount = 50;

        when(restTemplate.exchange(
                eq("http://dummy-withdraw-service.com/api/withdrawMoney"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        WithdrawServiceException exception = assertThrows(WithdrawServiceException.class, () -> {
            withdrawServiceApiStrategy.withdrawMoney(fundraiserId, amount);
        });
        assertTrue(exception.getMessage().contains("Withdraw service returned an error"));
    }

    @Test
    void testProcessPaymentResourceAccessException() {
        String fundraiserId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        int amount = 50;

        when(restTemplate.exchange(
                eq("http://dummy-withdraw-service.com/api/withdrawMoney"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        WithdrawServiceException exception = assertThrows(WithdrawServiceException.class, () -> {
            withdrawServiceApiStrategy.withdrawMoney(fundraiserId, amount);
        });
        assertTrue(exception.getMessage().contains("Cannot connect to withdraw service"));
    }
}