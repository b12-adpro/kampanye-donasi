package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.exception.WithdrawServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import id.ac.ui.cs.advprog.tk_adpro.repository.CampaignRepository;
import id.ac.ui.cs.advprog.tk_adpro.state.CampaignStatusState;
import id.ac.ui.cs.advprog.tk_adpro.state.CampaignStatusStateFactory;
import org.springframework.transaction.annotation.Transactional;
import id.ac.ui.cs.advprog.tk_adpro.strategy.WithdrawStrategy;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class CampaignServiceImpl implements CampaignService {
    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private WithdrawStrategy withdrawStrategy;

    @Override
    @Transactional
    public Campaign checkBalance(Campaign campaign) {
        if (withdrawStrategy.checkBalance(campaign.getCampaignId()) < campaign.getTarget()) {
            throw new WithdrawServiceException("Not enough balance");
        }
        return campaign;
    }

    @Override
    public Campaign withdrawMoney(Campaign campaign) {
        if (!withdrawStrategy.withdrawMoney(campaign.getFundraiserId(), campaign.getTarget())) {
            throw new WithdrawServiceException("Cannot withdraw money");
        }
        return campaign;
    }

    @Override
    public Campaign createCampaign(Campaign campaign) {
        campaign.setStatus(CampaignStatus.ACTIVE.getValue());
        return campaignRepository.save(campaign);
    }

    @Override
    public void activateCampaign(UUID campaignId) {
        Campaign campaign = getCampaignByIdOrThrow(campaignId);
        CampaignStatusState currentState = CampaignStatusStateFactory.getState(campaign);
        currentState.activate(campaign);
        campaignRepository.save(campaign);
    }

    @Override
    public void inactivateCampaign(UUID campaignId) {
        Campaign campaign = getCampaignByIdOrThrow(campaignId);
        CampaignStatusState currentState = CampaignStatusStateFactory.getState(campaign);
        currentState.inactivate(campaign);
        campaignRepository.save(campaign);
    }

    @Override
    public Campaign getCampaignByCampaignId(UUID campaignId) {
        Optional<Campaign> campaign = campaignRepository.findById(campaignId);
        return campaign.orElse(null);
    }

    private Campaign getCampaignByIdOrThrow(UUID campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + campaignId));
    }

    @Override
    public List<Campaign> getCampaignByFundraiserId(UUID fundraiserId) {
        return campaignRepository.findByFundraiserId(fundraiserId);
    }

    @Override
    public Campaign updateCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public void deleteCampaign(UUID campaignId) {campaignRepository.deleteById(campaignId);}

    @Override
    public String getBuktiPenggalanganDana(UUID campaignId) {
        Campaign campaign = getCampaignByIdOrThrow(campaignId);
        return campaign.getBuktiPenggalanganDana();
    }
}