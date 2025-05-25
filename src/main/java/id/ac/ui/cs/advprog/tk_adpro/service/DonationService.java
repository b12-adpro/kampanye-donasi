package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

import java.util.List;
import java.util.UUID;

public interface DonationService {
    Donation checkBalance(Donation donation);
    Donation createDonation(Donation donation);
    Donation cancelDonation(UUID donationId);
    Donation getDonationByDonationId(UUID donationId);
    List<Donation> getDonationsByDonaturId(UUID donaturId);
    List<Donation> getDonationsByCampaignId(UUID campaignId);
    Donation updateDonationMessage(UUID donationId, String newMessage);
    Donation deleteDonationMessage(UUID donationId);
}