package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

import java.util.List;

public interface CampaignService {
    Campaign createCampaign(Campaign campaign);
    void activateCampaign(String campaignId);
    void inactivateCampaign(String campaignId);
    Campaign getCampaignByCampaignId(String campaignId);
    List<Campaign> getCampaignByFundraiserId(String fundraiserId);
    Campaign updateCampaignJudul(String campaignId, String newJudul);
    Campaign updateCampaignTarget(String campaignId, int newTarget);
    Campaign updateCampaignDeskripsi(String campaignId, String newDeskripsi);
    void deleteCampaign(String campaignId);
}

