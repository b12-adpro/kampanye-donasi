package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

public class CanceledDonationStatusState implements DonationStatusState {
    @Override
    public void cancel(Donation donation) {
        throw new IllegalStateException("Donation is already cancelled.");
    }

    @Override
    public void complete(Donation donation) {
        throw new IllegalStateException("Canceled donations cannot be completed.");
    }

    @Override
    public void pending(Donation donation) {
        throw new IllegalStateException("Canceled donations cannot be pending.");
    }
}