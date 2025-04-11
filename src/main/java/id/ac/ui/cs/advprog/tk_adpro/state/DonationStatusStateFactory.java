package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

public class DonationStatusStateFactory {
    private DonationStatusStateFactory() {}

    public static DonationStatusState getState(Donation donation) {
        String status = donation.getStatus();
        if (DonationStatus.PENDING.getValue().equals(status)) {
            return new PendingDonationStatusState();
        } else if (DonationStatus.CANCELLED.getValue().equals(status)) {
            return new CancelledDonationStatusState();
        } else if (DonationStatus.COMPLETED.getValue().equals(status)) {
            return new CompletedDonationStatusState();
        }
        throw new IllegalStateException("Unknown donation status: " + status);
    }
}