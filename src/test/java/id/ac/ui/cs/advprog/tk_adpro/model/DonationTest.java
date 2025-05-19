package id.ac.ui.cs.advprog.tk_adpro.model;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DonationTest {
    private static final UUID donationId = UUID.randomUUID();
    private static final UUID campaignId = UUID.randomUUID();
    private static final UUID donaturId = UUID.randomUUID();

    @Test
    void testCreateDonationWithMessage() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        Donation donation = new Donation(
            donationId,
            campaignId,
            donaturId,
            169500,
            status,
            now,
            "Get well soon!"
        );

        assertNotNull(donation);
        assertEquals(donationId, donation.getDonationId());
        assertEquals(campaignId, donation.getCampaignId());
        assertEquals(donaturId, donation.getDonaturId());
        assertEquals(169500, donation.getAmount());
        assertEquals(status, donation.getStatus());
        assertEquals(now, donation.getDatetime());
        assertEquals("Get well soon!", donation.getMessage());
    }

    @Test
    void testCreateDonationWithoutMessage() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        Donation donation = new Donation(
            donationId,
            campaignId,
            donaturId,
            169500,
            status,
            now
        );

        assertNotNull(donation);
        assertEquals(donationId, donation.getDonationId());
        assertEquals(campaignId, donation.getCampaignId());
        assertEquals(donaturId, donation.getDonaturId());
        assertEquals(169500, donation.getAmount());
        assertEquals(status, donation.getStatus());
        assertEquals(now, donation.getDatetime());
        assertNull(donation.getMessage());
    }

    @Test
    void testCreateDonationNullMessage() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        Donation donation = new Donation(
            donationId,
            campaignId,
            donaturId,
            169500,
            status,
            now,
            null
        );

        assertNotNull(donation);
        assertEquals(donationId, donation.getDonationId());
        assertEquals(campaignId, donation.getCampaignId());
        assertEquals(donaturId, donation.getDonaturId());
        assertEquals(169500, donation.getAmount());
        assertEquals(status, donation.getStatus());
        assertEquals(now, donation.getDatetime());
        assertNull(donation.getMessage());
    }

    @Test
    void testCreateDonationNullDonationId() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        Donation donation = new Donation(
            null,
            campaignId,
            donaturId,
            169500,
            status,
            now
        );

        assertNotNull(donation);
        assertNull(donation.getDonationId());
        assertEquals(campaignId, donation.getCampaignId());
        assertEquals(donaturId, donation.getDonaturId());
        assertEquals(169500, donation.getAmount());
        assertEquals(status, donation.getStatus());
        assertEquals(now, donation.getDatetime());
        assertNull(donation.getMessage());
    }

    @Test
    void testCreateDonationZeroAmount() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        Donation donation = new Donation(
            donationId,
            campaignId,
            donaturId,
            0,
            status,
            now
        );

        assertNotNull(donation);
        assertEquals(donationId, donation.getDonationId());
        assertEquals(campaignId, donation.getCampaignId());
        assertEquals(donaturId, donation.getDonaturId());
        assertEquals(0, donation.getAmount());
        assertEquals(status, donation.getStatus());
        assertEquals(now, donation.getDatetime());
        assertNull(donation.getMessage());
    }

    @Test
    void testCreateDonationNegativeAmount() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Donation(
            donationId,
            campaignId,
            donaturId,
            -1,
            status,
            now
        ));
    }

    @Test
    void testCreateDonationNullCampaignId() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Donation(
            donationId,
            null,
            donaturId,
            1,
            status,
            now
        ));
    }

    @Test
    void testCreateDonationNullDonaturId() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Donation(
            donationId,
            campaignId,
            null,
            1,
            status,
            now
        ));
    }

    @Test
    void testCreateDonationWithValidStatuses() {
        LocalDateTime now = LocalDateTime.now();
        for (DonationStatus ds : DonationStatus.values()) {
            Donation donation = new Donation(
                donationId,
                campaignId,
                donaturId,
                100,
                ds.getValue(),
                now
            );
            assertEquals(ds.getValue(), donation.getStatus());
        }
    }

    @Test
    void testCreateDonationInvalidStatus() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Donation(
            donationId,
            campaignId,
            donaturId,
            1,
            "INVALID_STATUS",
            now
        ));
    }

    @Test
    void testDefaultConstructor() {
        Donation donation = new Donation();
        assertNotNull(donation);
        assertNull(donation.getDonationId());
        assertNull(donation.getCampaignId());
        assertNull(donation.getDonaturId());
        assertEquals(0, donation.getAmount());
        assertNull(donation.getStatus());
        assertNull(donation.getDatetime());
        assertNull(donation.getMessage());
    }
}