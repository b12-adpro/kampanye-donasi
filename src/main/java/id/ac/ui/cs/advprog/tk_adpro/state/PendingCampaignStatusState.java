package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

public class PendingCampaignStatusState implements CampaignStatusState {
    @Override
    public void activate(Campaign campaign) {
        campaign.setStatus(CampaignStatus.ACTIVE.getValue());
    }

    @Override
    public void inactivate(Campaign campaign) {
        campaign.setStatus(CampaignStatus.INACTIVE.getValue());
    }
}