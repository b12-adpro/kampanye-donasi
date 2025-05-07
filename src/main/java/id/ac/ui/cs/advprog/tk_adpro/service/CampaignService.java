package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

import java.util.List;

public interface CampaignService {
    Campaign createCampaign(Campaign campaign);
    void activateCampaign(String campaignId);
    void inactivateCampaign(String campaignId);
    Campaign getCampaignByCampaignId(String campaignId);
    List<Campaign> getCampaignByFundraiserId(String fundraiserId);
    Campaign updateCampaign(Campaign campaign);
    void deleteCampaign(String campaignId);
}

