package id.ac.ui.cs.advprog.tk_adpro.strategy;

public interface PaymentStrategy {
    /**
     * Calls the external API to check the balance for the given donatur ID.
     *
     * @param donaturId the donor's id
     * @return the available balance
     */
    int checkBalance(long donaturId);

    /**
     * Calls the external API to process the payment for a donation.
     *
     * @param donaturId the donor's id
     * @param amount the donation amount
     * @return true if payment is successful, false otherwise
     */
    boolean processPayment(long donaturId, int amount);
}