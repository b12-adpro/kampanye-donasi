package id.ac.ui.cs.advprog.tk_adpro.model;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DonationTest {
    @Test
    void testCreateDonationWithMessage() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            status,
            now,
            "Get well soon!"
        );

        assertNotNull(donation);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", donation.getDonationId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", donation.getCampaignId());
        assertEquals(123L, donation.getDonaturId());
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
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            status,
            now
        );

        assertNotNull(donation);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", donation.getDonationId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", donation.getCampaignId());
        assertEquals(123L, donation.getDonaturId());
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
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            status,
            now,
            null
        );

        assertNotNull(donation);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", donation.getDonationId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", donation.getCampaignId());
        assertEquals(123L, donation.getDonaturId());
        assertEquals(169500, donation.getAmount());
        assertEquals(status, donation.getStatus());
        assertEquals(now, donation.getDatetime());
        assertNull(donation.getMessage());
    }

    @Test
    void testCreateDonationWithNullDonationId() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        Donation donation = new Donation(
            null,
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            status,
            now
        );

        assertNotNull(donation);
        assertNull(donation.getDonationId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", donation.getCampaignId());
        assertEquals(123L, donation.getDonaturId());
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
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            0,
            status,
            now
        );

        assertNotNull(donation);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", donation.getDonationId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", donation.getCampaignId());
        assertEquals(123L, donation.getDonaturId());
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
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
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
            "13652556-012a-4c07-b546-54eb1396d79b",
            null,
            123L,
            1,
            status,
            now
        ));
    }

    @Test
    void testCreateDonationEmptyStringCampaignId() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "",
            123L,
            1,
            status,
            now
        ));
    }

    @Test
    void testCreateDonationZeroDonaturId() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "",
            0,
            1,
            status,
            now
        ));
    }

    @Test
    void testCreateDonationNegativeDonaturId() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "",
            -1L,
            1,
            status,
            now
        ));
    }

    @Test
    void testCreateDonationCompletedStatus() {
        String status = DonationStatus.COMPLETED.getValue();
        LocalDateTime now = LocalDateTime.now();
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            status,
            now,
            ""
        );

        assertNotNull(donation);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", donation.getDonationId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", donation.getCampaignId());
        assertEquals(123L, donation.getDonaturId());
        assertEquals(169500, donation.getAmount());
        assertEquals(status, donation.getStatus());
        assertEquals(now, donation.getDatetime());
        assertEquals("", donation.getMessage());
    }

    @Test
    void testCreateDonationPendingStatus() {
        String status = DonationStatus.PENDING.getValue();
        LocalDateTime now = LocalDateTime.now();
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            status,
            now,
            null
        );

        assertNotNull(donation);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", donation.getDonationId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", donation.getCampaignId());
        assertEquals(123L, donation.getDonaturId());
        assertEquals(169500, donation.getAmount());
        assertEquals(status, donation.getStatus());
        assertEquals(now, donation.getDatetime());
        assertNull(donation.getMessage());
    }

    @Test
    void testCreateDonationCancelledStatus() {
        String status = DonationStatus.CANCELLED.getValue();
        LocalDateTime now = LocalDateTime.now();
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            status,
            now,
            null
        );

        assertNotNull(donation);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", donation.getDonationId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", donation.getCampaignId());
        assertEquals(123L, donation.getDonaturId());
        assertEquals(169500, donation.getAmount());
        assertEquals(status, donation.getStatus());
        assertEquals(now, donation.getDatetime());
        assertNull(donation.getMessage());
    }

    @Test
    void testCreateDonationInvalidStatus() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            1,
            "Halah mbuh sirahku ngelu",
            now
        ));
    }
}