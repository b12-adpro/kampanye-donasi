package id.ac.ui.cs.advprog.tk_adpro.strategy;

import java.util.UUID;

public interface WithdrawStrategy {
    int checkBalance(UUID fundraiserId);
    boolean withdrawMoney(UUID fundraiserId, int amount);
}