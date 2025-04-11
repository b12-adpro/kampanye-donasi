package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import java.util.List;

public interface DonationService {
    Donation checkBalance(Donation donation);
    Donation createDonation(Donation donation);
    Donation completeDonation(String donationId);
    Donation cancelDonation(String donationId);
    Donation getDonationByDonationId(String donationId);
    List<Donation> getDonationsByDonaturId(long donaturId);
    List<Donation> getDonationsByCampaignId(String campaignId);
    Donation updateDonationMessage(String donationId, String newMessage);
    Donation deleteDonationMessage(String donationId);
}