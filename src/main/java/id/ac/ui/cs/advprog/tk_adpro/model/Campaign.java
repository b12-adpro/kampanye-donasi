package id.ac.ui.cs.advprog.tk_adpro.model;

import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import lombok.*;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Campaign {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID campaignId;

    @Column(updatable = false, nullable = false)
    private UUID fundraiserId;

    @Column(nullable = false)
    private String judul;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime datetime;

    @Column(nullable = false)
    private int target;

    @Column
    private String deskripsi;

    @PrePersist
    public void ensureId() {
        if (this.campaignId == null) this.campaignId = UUID.randomUUID();
    }

    public Campaign(UUID campaignId, UUID fundraiserId, String judul, String status, LocalDateTime datetime, int target, String deskripsi) {
        validateCommonFields(campaignId, fundraiserId, judul, status, target);
        this.campaignId = campaignId;
        this.fundraiserId = fundraiserId;
        this.judul = judul;
        this.status = status;
        this.datetime = datetime;
        this.target = target;
        this.deskripsi = deskripsi;
    }

    public Campaign(UUID campaignId, UUID fundraiserId, String judul, String status, LocalDateTime datetime, int target) {
        this(campaignId, fundraiserId, judul, status, datetime, target, null);
    }

    private void validateCommonFields(UUID campaignId, UUID fundraiserId, String judul, String status, int target) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign Id must not be null!");
        }

        if (fundraiserId == null) {
            throw new IllegalArgumentException("Fundraiser Id must not be null!");
        }

        if (judul == null || judul.isEmpty()) {
            throw new IllegalArgumentException("Judul must not be null or empty!");
        }

        if (!CampaignStatus.contains(status)) {
            throw new IllegalArgumentException("Status is not valid!");
        }

        if (target <= 0) {
            throw new IllegalArgumentException("Target must be greater than zero!");
        }
    }
}
