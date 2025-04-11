package id.ac.ui.cs.advprog.tk_adpro.model;

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
    }

    public Donation(String donationId, String campaignId, long donaturId, int amount, String status, LocalDateTime datetime) {
    }
}