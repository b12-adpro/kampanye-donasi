package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CampaignRepository {
    private final List<Campaign> campaignData = new ArrayList<>();

    public Campaign save(Campaign campaign) {
        int i = 0;
        for (Campaign c: campaignData) {
            if (c.getCampaignId().equals(campaign.getCampaignId())) {
                campaignData.set(i, campaign);
                return campaign;
            }
            i++;
        }
        campaignData.add(campaign);
        return campaign;
    }

    public Campaign findByCampaignId(String id) {
        for (Campaign c: campaignData) if (c.getCampaignId().equals(id)) return c;
        return null;
    }

    public List<Campaign> findByFundraiserId(String fundraiserId) {
        List<Campaign> campaigns = new ArrayList<>();
        for (Campaign c: campaignData) if (c.getFundraiserId().equals(fundraiserId)) campaigns.add(c);
        return campaigns;
    }
}