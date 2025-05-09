package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DonationStatusStateTest {
    private Donation baseDonation;

    @BeforeEach
    void setUp() {
        baseDonation = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            1000,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Test donation"
        );
    }

    @Test
    void pendingDonation_canBeCanceled() {
        DonationStatusState state = new PendingDonationStatusState();
        state.cancel(baseDonation);
        assertThat(baseDonation.getStatus()).isEqualTo(DonationStatus.CANCELED.getValue());
    }

    @Test
    void pendingDonation_canBeCompleted() {
        DonationStatusState state = new PendingDonationStatusState();
        state.complete(baseDonation);
        assertThat(baseDonation.getStatus()).isEqualTo(DonationStatus.COMPLETED.getValue());
    }

    @Test
    void canceledDonation_cannotBeCanceledAgain() {
        baseDonation.setStatus(DonationStatus.CANCELED.getValue());
        DonationStatusState state = new CanceledDonationStatusState();

        assertThatThrownBy(() -> state.cancel(baseDonation))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already cancelled");
    }

    @Test
    void canceledDonation_cannotBeCompleted() {
        baseDonation.setStatus(DonationStatus.CANCELED.getValue());
        DonationStatusState state = new CanceledDonationStatusState();

        assertThatThrownBy(() -> state.complete(baseDonation))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("cannot be completed");
    }

    @Test
    void completedDonation_cannotBeCanceled() {
        baseDonation.setStatus(DonationStatus.COMPLETED.getValue());
        DonationStatusState state = new CompletedDonationStatusState();

        assertThatThrownBy(() -> state.cancel(baseDonation))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("cannot be canceled");
    }

    @Test
    void completedDonation_cannotBeCompletedAgain() {
        baseDonation.setStatus(DonationStatus.COMPLETED.getValue());
        DonationStatusState state = new CompletedDonationStatusState();

        assertThatThrownBy(() -> state.complete(baseDonation))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already completed");
    }

    @Test
    void factory_shouldReturnCorrectStateForPending() {
        DonationStatusState state = DonationStatusStateFactory.getState(baseDonation);
        assertThat(state).isInstanceOf(PendingDonationStatusState.class);
    }

    @Test
    void factory_shouldReturnCorrectStateForCanceled() {
        baseDonation.setStatus(DonationStatus.CANCELED.getValue());
        DonationStatusState state = DonationStatusStateFactory.getState(baseDonation);
        assertThat(state).isInstanceOf(CanceledDonationStatusState.class);
    }

    @Test
    void factory_shouldReturnCorrectStateForCompleted() {
        baseDonation.setStatus(DonationStatus.COMPLETED.getValue());
        DonationStatusState state = DonationStatusStateFactory.getState(baseDonation);
        assertThat(state).isInstanceOf(CompletedDonationStatusState.class);
    }

    @Test
    void factory_shouldThrowOnUnknownStatus() {
        baseDonation.setStatus("UNKNOWN_STATUS");
        assertThatThrownBy(() -> DonationStatusStateFactory.getState(baseDonation))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Unknown donation status");
    }
}