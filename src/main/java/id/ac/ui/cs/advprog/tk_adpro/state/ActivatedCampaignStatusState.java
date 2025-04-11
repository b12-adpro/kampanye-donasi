package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

public class ActivatedCampaignStatusState implements CampaignStatusState{
    @Override
    public void inactivate(Campaign campaign) {
        campaign.setStatus(CampaignStatus.INACTIVE.getValue());
    }

    @Override
    public void activate(Campaign campaign) {
        throw new IllegalStateException("Campaign is already activated");
    }
}
