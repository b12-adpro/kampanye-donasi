package id.ac.ui.cs.advprog.tk_adpro.strategy;

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

import id.ac.ui.cs.advprog.tk_adpro.exception.PaymentServiceException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;

@Service
public class PaymentServiceApiStrategy implements PaymentStrategy {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceApiStrategy.class);
    private static final String CHECK_BALANCE_URL = "http://dummy-payment-service.com/api/checkBalance";
    private static final String PROCESS_PAYMENT_URL = "http://dummy-payment-service.com/api/processPayment";
    private final RestTemplate restTemplate;

    @Autowired
    public PaymentServiceApiStrategy(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
            .connectTimeout(java.time.Duration.ofSeconds(5))
            .readTimeout(java.time.Duration.ofSeconds(10))
            .build();
    }

    @Override
    public int checkBalance(long donaturId) {
        try {
            // Prepare HTTP headers and body for JSON communication
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Create request body as a map
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("donaturId", donaturId);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Send POST request to the checkBalance API
            logger.debug("Sending balance check request to: {}", CHECK_BALANCE_URL);

            ResponseEntity<Map> response = restTemplate.exchange(
                CHECK_BALANCE_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );

            // Process response
            Map body = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && body != null) {
                Object balanceObj = body.get("balance");
                if (balanceObj instanceof Number number) {
                    return number.intValue();
                } else {
                    logger.error("Invalid balance format received: {}", balanceObj);
                    throw new PaymentServiceException("Invalid balance format received from payment service");
                }
            }
            logger.error("Failed to check balance. Status code: {}", response.getStatusCode());
            throw new PaymentServiceException("Failed to check balance from payment service. Status: " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            logger.error("Payment service client error: {}", e.getMessage());
            throw new PaymentServiceException("Payment service returned an error: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            logger.error("Cannot connect to payment service: {}", e.getMessage());
            throw new PaymentServiceException("Cannot connect to payment service: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during balance check: {}", e.getMessage());
            throw new PaymentServiceException("Unexpected error during balance check: " + e.getMessage());
        }
    }

    @Override
    public boolean processPayment(long donaturId, int amount) {
        try {
            // Prepare HTTP headers and body for JSON communication
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Create request body as a map
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("donaturId", donaturId);
            requestBody.put("amount", amount);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Send POST request to the processPayment API
            logger.debug("Processing payment request to: {} for donatur: {} amount: {}", PROCESS_PAYMENT_URL, donaturId, amount);

            ResponseEntity<Map> response = restTemplate.exchange(
                PROCESS_PAYMENT_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );

            // Process response
            Map body = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && body != null) {
                Object successObj = body.get("success");
                if (successObj instanceof Boolean success) {
                    if (!success.booleanValue()) {
                        logger.warn("Payment declined for donaturId: {}, amount: {}", donaturId, amount);
                    }
                    return success.booleanValue();
                } else {
                    logger.error("Invalid success response format received: {}", successObj);
                    throw new PaymentServiceException("Invalid response format from payment service");
                }
            }
            logger.error("Failed to process payment. Status code: {}", response.getStatusCode());
            throw new PaymentServiceException("Failed to process payment. Status: " + response.getStatusCode());

        } catch (HttpClientErrorException e) {
            logger.error("Payment service client error: {}", e.getMessage());
            throw new PaymentServiceException("Payment service returned an error: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            logger.error("Cannot connect to payment service: {}", e.getMessage());
            throw new PaymentServiceException("Cannot connect to payment service: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during payment processing: {}", e.getMessage());
            throw new PaymentServiceException("Unexpected error during payment processing: " + e.getMessage());
        }
    }
}