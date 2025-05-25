package id.ac.ui.cs.advprog.tk_adpro.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DonationDTOTest {
    @Test
    void testNoArgsConstructorAndSetters() {
        DonationDTO dto = new DonationDTO();

        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        int amount = 50000;
        String status = "SUCCESS";
        String message = "Keep up the good work!";
        LocalDateTime datetime = LocalDateTime.now();

        dto.setDonationId(donationId);
        dto.setCampaignId(campaignId);
        dto.setDonaturId(donaturId);
        dto.setAmount(amount);
        dto.setStatus(status);
        dto.setMessage(message);
        dto.setDatetime(datetime);

        assertEquals(donationId, dto.getDonationId());
        assertEquals(campaignId, dto.getCampaignId());
        assertEquals(donaturId, dto.getDonaturId());
        assertEquals(amount, dto.getAmount());
        assertEquals(status, dto.getStatus());
        assertEquals(message, dto.getMessage());
        assertEquals(datetime, dto.getDatetime());
    }

    @Test
    void testAllArgsConstructor() {
        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        int amount = 100000;
        String status = "PENDING";
        LocalDateTime datetime = LocalDateTime.now();
        String message = "Hope this helps";

        DonationDTO dto = new DonationDTO(donationId, campaignId, donaturId, amount, status, datetime, message);
        assertEquals(donationId, dto.getDonationId());
        assertEquals(campaignId, dto.getCampaignId());
        assertEquals(donaturId, dto.getDonaturId());
        assertEquals(amount, dto.getAmount());
        assertEquals(status, dto.getStatus());
        assertEquals(message, dto.getMessage());
        assertEquals(datetime, dto.getDatetime());
    }
}