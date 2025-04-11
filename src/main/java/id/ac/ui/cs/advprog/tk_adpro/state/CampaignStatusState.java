package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

public interface CampaignStatusState {
    void inactivate(Campaign campaign);

    void activate(Campaign campaign);
}