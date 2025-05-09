package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class CampaignStatusStateTest{
    private Campaign baseCampaign;

    @BeforeEach
    void setUp() {
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        baseCampaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Test campaign"
        );
    }

    @Test
    void inactiveCampaign_cannotBeInactivatedAgain() {
        baseCampaign.setStatus(CampaignStatus.INACTIVE.getValue());
        CampaignStatusState state = new InactivatedCampaignStatusState();

        assertThatThrownBy(() -> state.inactivate(baseCampaign))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already inactivated");
    }

    @Test
    void activeCampaign_canBeInactivated() {
        baseCampaign.setStatus(CampaignStatus.ACTIVE.getValue());
        CampaignStatusState state = new ActivatedCampaignStatusState();

        state.inactivate(baseCampaign);

        assertThat(baseCampaign.getStatus()).isEqualTo(CampaignStatus.INACTIVE.getValue());
    }

    @Test
    void activatedCampaign_cannotBeActivatedAgain() {
        baseCampaign.setStatus(CampaignStatus.ACTIVE.getValue());
        CampaignStatusState state = new ActivatedCampaignStatusState();

        assertThatThrownBy(() -> state.activate(baseCampaign))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already activated");
    }

    @Test
    void inactiveCampaign_canBeActivatedAgain() {
        baseCampaign.setStatus(CampaignStatus.INACTIVE.getValue());
        CampaignStatusState state = new InactivatedCampaignStatusState();

        state.activate(baseCampaign);

        assertThat(baseCampaign.getStatus()).isEqualTo(CampaignStatus.ACTIVE.getValue());
    }

    @Test
    void factory_shouldReturnCorrectStateForActive() {
        CampaignStatusState state = CampaignStatusStateFactory.getState(baseCampaign);
        assertThat(state).isInstanceOf(ActivatedCampaignStatusState.class);
    }

    @Test
    void factory_shouldReturnCorrectStateForInactive() {
        baseCampaign.setStatus(CampaignStatus.INACTIVE.getValue());
        CampaignStatusState state = CampaignStatusStateFactory.getState(baseCampaign);
        assertThat(state).isInstanceOf(InactivatedCampaignStatusState.class);
    }

    @Test
    void factory_shouldThrowOnUnknownStatus() {
        baseCampaign.setStatus("UNKNOWN_STATUS");

        assertThatThrownBy(() -> CampaignStatusStateFactory.getState(baseCampaign))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unknown campaign status");
    }
}
