package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

public class CanceledDonationStatusState implements DonationStatusState {
    @Override
    public void cancel(Donation donation) {
        throw new IllegalStateException("Donation is already cancelled and cannot be cancelled again.");
    }

    @Override
    public void complete(Donation donation) {
        throw new IllegalStateException("Canceled donations cannot be completed.");
    }
}