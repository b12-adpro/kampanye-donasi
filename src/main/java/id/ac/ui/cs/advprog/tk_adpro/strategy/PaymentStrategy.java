package id.ac.ui.cs.advprog.tk_adpro.strategy;

import java.util.concurrent.CompletableFuture;
import java.util.UUID;

public interface PaymentStrategy {
    double checkBalance(UUID donaturId);
    CompletableFuture<Void> processPayment(UUID donationId, UUID campaignId, UUID donaturId, int amount);
}