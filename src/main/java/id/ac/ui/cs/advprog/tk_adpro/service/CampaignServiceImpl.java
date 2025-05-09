package id.ac.ui.cs.advprog.tk_adpro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import id.ac.ui.cs.advprog.tk_adpro.repository.CampaignRepository;
import id.ac.ui.cs.advprog.tk_adpro.state.CampaignStatusState;
import id.ac.ui.cs.advprog.tk_adpro.state.CampaignStatusStateFactory;
import id.ac.ui.cs.advprog.tk_adpro.strategy.WithdrawStrategy;
import java.util.List;

@Service
public class CampaignServiceImpl implements CampaignService {
    @Autowired
    private CampaignRepository campaignRepository;

    @Override
    public Campaign createCampaign(Campaign campaign) {
        campaign.setStatus(CampaignStatus.INACTIVE.getValue());
        return campaignRepository.save(campaign);
    }

    @Override
    public void activateCampaign(String campaignId) {
        Campaign campaign = getCampaignByCampaignId(campaignId);
        campaign.setStatus(CampaignStatus.ACTIVE.getValue());
        campaignRepository.save(campaign);
    }

    @Override
    public void inactivateCampaign(String campaignId) {
        Campaign campaign = getCampaignByCampaignId(campaignId);
        campaign.setStatus(CampaignStatus.INACTIVE.getValue());
        campaignRepository.save(campaign);
    }

    @Override
    public Campaign getCampaignByCampaignId(String campaignId) {
        Campaign campaign = campaignRepository.findByCampaignId(campaignId);
        if (campaign == null) {
            throw new RuntimeException("Campaign not found");
        }
        return campaign;
    }

    @Override
    public List<Campaign> getCampaignByFundraiserId(String fundraiserId) {
        return campaignRepository.findByFundraiserId(fundraiserId);
    }

    @Override
    public Campaign updateCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public void deleteCampaign(String campaignId) {
        campaignRepository.deleteByCampaignId(campaignId);
    }
}

