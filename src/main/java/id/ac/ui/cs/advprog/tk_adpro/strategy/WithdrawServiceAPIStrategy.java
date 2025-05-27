package id.ac.ui.cs.advprog.tk_adpro.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import id.ac.ui.cs.advprog.tk_adpro.exception.WithdrawServiceException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;

@Service
public class WithdrawServiceAPIStrategy implements WithdrawStrategy {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawServiceAPIStrategy.class);

    @Value("${payment.service.check-balance-url}")
    private String CHECK_BALANCE_URL;

    @Value("${payment.service.process-withdraw-url}")
    private String WITHDRAW_MONEY_URL;

    private final RestTemplate restTemplate;

    @Autowired
    public WithdrawServiceAPIStrategy(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(java.time.Duration.ofSeconds(5))
                .readTimeout(java.time.Duration.ofSeconds(10))
                .build();
    }

    @Override
    public int checkBalance(UUID fundraiserId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fundraiserId", fundraiserId);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            logger.debug("Sending balance check request to: {}", CHECK_BALANCE_URL);

            ResponseEntity<Map> response = restTemplate.exchange(
                    CHECK_BALANCE_URL,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            Map body = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && body != null) {
                Object balanceObj = body.get("balance");
                if (balanceObj instanceof Number number) {
                    return number.intValue();
                } else {
                    logger.error("Invalid balance format received: {}", balanceObj);
                    throw new WithdrawServiceException("Invalid balance format received from withdraw service");
                }
            }
            logger.error("Failed to check balance. Status code: {}", response.getStatusCode());
            throw new WithdrawServiceException("Failed to check balance from withdraw service. Status: " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            logger.error("Withdraw service client error: {}", e.getMessage());
            throw new WithdrawServiceException("Withdraw service returned an error: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            logger.error("Cannot connect to withdraw service: {}", e.getMessage());
            throw new WithdrawServiceException("Cannot connect to withdraw service: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during balance check: {}", e.getMessage());
            throw new WithdrawServiceException("Unexpected error during balance check: " + e.getMessage());
        }
    }

    @Override
    public boolean withdrawMoney(UUID fundraiserId, int amount) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fundraiserId", fundraiserId);
            requestBody.put("amount", amount);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            logger.debug("Processing withdraw request to: {} for fundraiser: {} amount: {}", WITHDRAW_MONEY_URL, fundraiserId, amount);

            ResponseEntity<Map> response = restTemplate.exchange(
                    WITHDRAW_MONEY_URL,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            Map body = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && body != null) {
                Object successObj = body.get("success");
                if (successObj instanceof Boolean success) {
                    if (!success.booleanValue()) {
                        logger.warn("Withdraw declined for fundraiserId: {}, amount: {}", fundraiserId, amount);
                    }
                    return success.booleanValue();
                } else {
                    logger.error("Invalid success response format received: {}", successObj);
                    throw new WithdrawServiceException("Invalid response format from withdraw service");
                }
            }
            logger.error("Failed to process withdraw. Status code: {}", response.getStatusCode());
            throw new WithdrawServiceException("Failed to process withdraw. Status: " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            logger.error("Withdraw service client error: {}", e.getMessage());
            throw new WithdrawServiceException("Withdraw service returned an error: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            logger.error("Cannot connect to withdraw service: {}", e.getMessage());
            throw new WithdrawServiceException("Cannot connect to withdraw service: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during withdraw processing: {}", e.getMessage());
            throw new WithdrawServiceException("Unexpected error during withdraw processing: " + e.getMessage());
        }
    }
}