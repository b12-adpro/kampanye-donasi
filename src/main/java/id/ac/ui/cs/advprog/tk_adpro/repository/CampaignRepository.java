package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CampaignRepository {
    private final List<Campaign> campaignData = new ArrayList<>();

    public Campaign save(Campaign campaign) {return null;}

    public Campaign findByCampaignId(String id) {return null;}

    public List<Campaign> findByFundraiserId(String fundraiserId) {return null;}
}