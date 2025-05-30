package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

public class CompletedDonationStatusState implements DonationStatusState {
    @Override
    public void cancel(Donation donation) {
        throw new IllegalStateException("Completed donations cannot be canceled.");
    }

    @Override
    public void complete(Donation donation) {
        throw new IllegalStateException("Donation is already completed.");
    }

    @Override
    public void pending(Donation donation) {
        donation.setStatus(DonationStatus.PENDING.getValue());
    }
}