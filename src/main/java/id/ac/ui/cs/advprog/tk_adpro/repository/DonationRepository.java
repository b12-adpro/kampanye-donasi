package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DonationRepository {
    private final List<Donation> donationData = new ArrayList<>();

    public Donation save(Donation donation) {return null;}

    public Donation findByDonationId(String id) {return null;}

    public List<Donation> findByCampaignId(String campaignId) {return null;}

    public List<Donation> findByDonaturId(long donaturId) {return null;}
}