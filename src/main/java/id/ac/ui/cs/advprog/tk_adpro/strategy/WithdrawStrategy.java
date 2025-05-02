package id.ac.ui.cs.advprog.tk_adpro.strategy;

public interface WithdrawStrategy {
    int checkBalance(String fundraiserId);
    boolean withdrawMoney(String fundraiserId, int amount);
}