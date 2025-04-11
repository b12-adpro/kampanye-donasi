package id.ac.ui.cs.advprog.tk_adpro.model;

import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class Campaign {
    private String campaignId;
    private String fundraiserId;
    private String judul;
    private String status;
    private LocalDateTime datetime;
    private int target;
    private String deskripsi;

    public Campaign(String campaignId, String fundraiserId, String judul, String status, LocalDateTime datetime, int target, String deskripsi) {
        validateCommonFields(campaignId, fundraiserId, judul, status, target);
        this.campaignId = campaignId;
        this.fundraiserId = fundraiserId;
        this.judul = judul;
        this.status = status;
        this.datetime = datetime;
        this.target = target;
        this.deskripsi = deskripsi;
    }

    public Campaign(String campaignId, String fundraiserId, String judul, String status, LocalDateTime datetime, int target) {
        this(campaignId, fundraiserId, judul, status, datetime, target,null);
    }

    private void validateCommonFields(String campaignId, String fundraiserId, String judul, String status, int target) {
        if (campaignId == null || campaignId.isEmpty()) {
            throw new IllegalArgumentException("Campaign Id must not be null or empty!");
        }

        if (fundraiserId == null || fundraiserId.isEmpty()) {
            throw new IllegalArgumentException("Fundraiser Id must not be null or empty!");
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
