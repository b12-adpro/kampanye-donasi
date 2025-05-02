package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

public class CampaignStatusStateFactory {
    private CampaignStatusStateFactory() {}

    public static CampaignStatusState getState(Campaign campaign) {
        String status = campaign.getStatus();
        if (CampaignStatus.ACTIVE.getValue().equals(status)) {
            return new ActivatedCampaignStatusState();
        }
        else if (CampaignStatus.INACTIVE.getValue().equals(status)) {
            return new InactivatedCampaignStatusState();
        }
        throw new IllegalStateException("Unknown campaign status: " + status);
    }
}