package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

public interface DonationStatusState {
    /**
     * Cancels the donation if the state transition is allowed.
     * @param donation The donation to cancel.
     */
    void cancel(Donation donation);

    /**
     * Completes the donation if the state transition is allowed.
     * @param donation The donation to complete.
     */
    void complete(Donation donation);
}