package id.ac.ui.cs.advprog.tk_adpro.model;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class Donation {
    private String donationId;
    private String campaignId;
    private long donaturId;
    private int amount;
    private String status;
    private LocalDateTime datetime;
    private String message;

    public Donation(String donationId, String campaignId, long donaturId, int amount, String status, LocalDateTime datetime, String message) {
        validateCommonFields(campaignId, donaturId, amount, status);
        this.donationId = donationId;
        this.campaignId = campaignId;
        this.donaturId = donaturId;
        this.amount = amount;
        this.status = status;
        this.message = message;
        this.datetime = datetime;
    }

    public Donation(String donationId, String campaignId, long donaturId, int amount, String status, LocalDateTime datetime) {
        this(donationId, campaignId, donaturId, amount, status, datetime, null);
    }

    public Donation() {}

    private void validateCommonFields(String campaignId, long donaturId, int amount, String status) {
        if (campaignId == null || campaignId.isEmpty()) {
            throw new IllegalArgumentException("Campaign Id must not be null or empty!");
        }
        if (donaturId < 0) {
            throw new IllegalArgumentException("DonaturId is not valid!");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cant be negative!");
        }
        if (!DonationStatus.contains(status)) {
            throw new IllegalArgumentException("Status is not valid!");
        }
    }
}