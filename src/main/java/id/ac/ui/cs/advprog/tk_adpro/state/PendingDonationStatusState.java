package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

public class PendingDonationStatusState implements DonationStatusState {
    @Override
    public void cancel(Donation donation) {
        donation.setStatus(DonationStatus.CANCELED.getValue());
    }

    @Override
    public void complete(Donation donation) {
        donation.setStatus(DonationStatus.COMPLETED.getValue());
    }
}