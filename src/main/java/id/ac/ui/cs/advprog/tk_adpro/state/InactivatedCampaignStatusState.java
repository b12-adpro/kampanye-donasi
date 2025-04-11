package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

public class InactivatedCampaignStatusState implements CampaignStatusState{
    @Override
    public void inactivate(Campaign campaign) {
        throw new IllegalStateException("Campaign is already inactivated");
    }

    @Override
    public void activate(Campaign campaign) {
        campaign.setStatus(CampaignStatus.ACTIVE.getValue());
    }
}
