package id.ac.ui.cs.advprog.tk_adpro.strategy;

import java.util.concurrent.CompletableFuture;
import java.util.UUID;

public interface PaymentStrategy {
    int checkBalance(UUID donaturId);
    CompletableFuture<Void> processPayment(UUID donaturId, int amount);
}