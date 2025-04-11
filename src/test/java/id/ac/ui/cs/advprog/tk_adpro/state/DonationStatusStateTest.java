package id.ac.ui.cs.advprog.tk_adpro.state;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class DonationStatusStateTest {
    private Donation baseDonation;

    @BeforeEach
    void setUp() {
        baseDonation = new Donation(
            "don1", "camp1", 123L, 1000,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Test donation"
        );
    }

    // ---------------------------
    // ✅ PENDING state tests
    // ---------------------------

    @Test
    void pendingDonation_canBeCancelled() {
        DonationStatusState state = new PendingDonationStatusState();

        state.cancel(baseDonation);

        assertThat(baseDonation.getStatus()).isEqualTo(DonationStatus.CANCELLED.getValue());
    }

    @Test
    void pendingDonation_canBeCompleted() {
        DonationStatusState state = new PendingDonationStatusState();

        state.complete(baseDonation);

        assertThat(baseDonation.getStatus()).isEqualTo(DonationStatus.COMPLETED.getValue());
    }

    // ---------------------------
    // ❌ CANCELLED state tests
    // ---------------------------

    @Test
    void cancelledDonation_cannotBeCancelledAgain() {
        baseDonation.setStatus(DonationStatus.CANCELLED.getValue());
        DonationStatusState state = new CancelledDonationStatusState();

        assertThatThrownBy(() -> state.cancel(baseDonation))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already cancelled");
    }

    @Test
    void cancelledDonation_cannotBeCompleted() {
        baseDonation.setStatus(DonationStatus.CANCELLED.getValue());
        DonationStatusState state = new CancelledDonationStatusState();

        assertThatThrownBy(() -> state.complete(baseDonation))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("cannot be completed");
    }

    // ---------------------------
    // ❌ COMPLETED state tests
    // ---------------------------

    @Test
    void completedDonation_cannotBeCancelled() {
        baseDonation.setStatus(DonationStatus.COMPLETED.getValue());
        DonationStatusState state = new CompletedDonationStatusState();

        assertThatThrownBy(() -> state.cancel(baseDonation))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("cannot be cancelled");
    }

    @Test
    void completedDonation_cannotBeCompletedAgain() {
        baseDonation.setStatus(DonationStatus.COMPLETED.getValue());
        DonationStatusState state = new CompletedDonationStatusState();

        assertThatThrownBy(() -> state.complete(baseDonation))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already completed");
    }

    // ---------------------------
    // ✅ Factory Test
    // ---------------------------

    @Test
    void factory_shouldReturnCorrectStateForPending() {
        DonationStatusState state = DonationStatusStateFactory.getState(baseDonation);
        assertThat(state).isInstanceOf(PendingDonationStatusState.class);
    }

    @Test
    void factory_shouldReturnCorrectStateForCancelled() {
        baseDonation.setStatus(DonationStatus.CANCELLED.getValue());
        DonationStatusState state = DonationStatusStateFactory.getState(baseDonation);
        assertThat(state).isInstanceOf(CancelledDonationStatusState.class);
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