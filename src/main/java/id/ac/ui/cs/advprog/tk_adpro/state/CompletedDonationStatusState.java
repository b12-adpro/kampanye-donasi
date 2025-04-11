package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

public class CompletedDonationStatusState implements DonationStatusState {
    @Override
    public void cancel(Donation donation) {
        throw new IllegalStateException("Completed donations cannot be cancelled.");
    }

    @Override
    public void complete(Donation donation) {
        throw new IllegalStateException("Donation is already completed.");
    }
}