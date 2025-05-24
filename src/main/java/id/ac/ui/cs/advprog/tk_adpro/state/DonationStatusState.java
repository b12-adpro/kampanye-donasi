package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

public interface DonationStatusState {
    void cancel(Donation donation);
    void complete(Donation donation);
    void pending(Donation donation);
}