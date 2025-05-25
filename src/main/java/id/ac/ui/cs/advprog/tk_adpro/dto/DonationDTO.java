package id.ac.ui.cs.advprog.tk_adpro.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
public class DonationDTO {
    @Nullable
    private UUID donationId;

    private UUID campaignId;

    @NotNull(message = "Donatur ID can't be empty!")
    private UUID donaturId;

    @Min(value = 1, message = "Amount must be at least 1")
    private int amount;

    @NotNull(message = "Status can't be empty!")
    private String status;

    @Nullable
    @Size(max = 500, message = "Message can't be more than 500 characters")
    private String message;

    private LocalDateTime datetime;

    public DonationDTO() {}

    public DonationDTO(@Nullable UUID donationId, UUID campaignId, UUID donaturId, int amount, String status, LocalDateTime datetime, @Nullable String message) {
        this.donationId = donationId;
        this.campaignId = campaignId;
        this.donaturId = donaturId;
        this.amount = amount;
        this.status = status;
        this.datetime = datetime;
        this.message = message;
    }
}