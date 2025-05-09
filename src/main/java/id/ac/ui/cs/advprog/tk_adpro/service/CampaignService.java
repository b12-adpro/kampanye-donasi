package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

import java.util.List;
import java.util.UUID;

public interface CampaignService {
    Campaign createCampaign(Campaign campaign);
    void activateCampaign(UUID campaignId);
    void inactivateCampaign(UUID campaignId);
    Campaign getCampaignByCampaignId(UUID campaignId);
    List<Campaign> getCampaignByFundraiserId(UUID fundraiserId);
    Campaign updateCampaign(Campaign campaign);
    void deleteCampaign(UUID campaignId);
}

