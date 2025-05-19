package id.ac.ui.cs.advprog.tk_adpro.model;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "donations")
@Builder
@NoArgsConstructor
@Getter @Setter
public class Donation {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID donationId;

    @Column(updatable = false, nullable = false)
    private UUID campaignId;

    @Column(updatable = false, nullable = false)
    private UUID donaturId;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime datetime;

    @Column
    private String message;

    @PrePersist
    public void ensureId() {
        if (this.donationId == null) this.donationId = UUID.randomUUID();
    }

    public Donation(UUID donationId, UUID campaignId, UUID donaturId, int amount, String status, LocalDateTime datetime, String message) {
        validateCommonFields(campaignId, donaturId, amount, status);
        this.donationId = donationId;
        this.campaignId = campaignId;
        this.donaturId = donaturId;
        this.amount = amount;
        this.status = status;
        this.datetime = datetime;
        this.message = message;
    }

    public Donation(UUID donationId, UUID campaignId, UUID donaturId, int amount, String status, LocalDateTime datetime) {
        this(donationId, campaignId, donaturId, amount, status, datetime, null);
    }

    private void validateCommonFields(UUID campaignId, UUID donaturId, int amount, String status) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign Id must not be null!");
        }
        if (donaturId == null) {
            throw new IllegalArgumentException("DonaturId is must not be null!");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount can't be negative!");
        }
        if (!DonationStatus.contains(status)) {
            throw new IllegalArgumentException("Status is not valid!");
        }
    }
}