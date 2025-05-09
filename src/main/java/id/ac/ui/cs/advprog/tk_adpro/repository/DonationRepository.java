package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class DonationRepository {
    private final List<Donation> donationData = new ArrayList<>();

    public Donation save(Donation donation) {
        if (donation.getDonationId() != null) {
            int i = 0;
            for (Donation d: donationData) {
                if (d.getDonationId().equals(donation.getDonationId())) {
                    donationData.set(i, donation);
                    return donation;
                }
                i++;
            }
        }
        donation.setDonationId(UUID.randomUUID());
        donationData.add(donation);
        return donation;
    }

    public Donation findByDonationId(UUID donationId) {
        for (Donation d: donationData) if (d.getDonationId().equals(donationId)) return d;
        return null;
    }

    public List<Donation> findByCampaignId(UUID campaignId) {
        List<Donation> donations = new ArrayList<>();
        for (Donation d: donationData) if (d.getCampaignId().equals(campaignId)) donations.add(d);
        return donations;
    }

    public List<Donation> findByDonaturId(UUID donaturId) {
        List<Donation> donations = new ArrayList<>();
        for (Donation d: donationData) if (d.getDonaturId().equals(donaturId)) donations.add(d);
        return donations;
    }
}