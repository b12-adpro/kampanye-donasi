package id.ac.ui.cs.advprog.tk_adpro.model;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DonationTest {
    @Test
    void testCreateCampaignWithDeskripsi() {
        String status = CampaignStatus.ACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                status,
                now,
                123123,
                "Ini deskripsi."
        );

        assertNotNull(campaign);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", campaign.getCampaignId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", campaign.getFundraiserId());
        assertEquals("Donation Campaign", campaign.getJudul());
        assertEquals(status, campaign.getStatus());
        assertEquals(now, campaign.getDatetime());
        assertEquals(123123, campaign.getTarget());
        assertEquals("Ini deskripsi.", campaign.getDeskripsi());
    }

    @Test
    void testCreateCampaignWithoutDeskripsi() {
        String status = CampaignStatus.ACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                status,
                now,
                123123
        );

        assertNotNull(campaign);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", campaign.getCampaignId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", campaign.getFundraiserId());
        assertEquals("Donation Campaign", campaign.getJudul());
        assertEquals(status, campaign.getStatus());
        assertEquals(now, campaign.getDatetime());
        assertEquals(123123, campaign.getTarget());
        assertNull(campaign.getDeskripsi());
    }

    @Test
    void testCreateCampaignNullDeskripsi() {
        String status = CampaignStatus.ACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                status,
                now,
                123123,
                null
        );

        assertNotNull(campaign);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", campaign.getCampaignId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", campaign.getFundraiserId());
        assertEquals("Donation Campaign", campaign.getJudul());
        assertEquals(status, campaign.getStatus());
        assertEquals(now, campaign.getDatetime());
        assertEquals(123123, campaign.getTarget());
        assertNull(campaign.getDeskripsi());
    }

    @Test
    void testCreateCampaignZeroTarget() {
        String status = CampaignStatus.ACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                status,
                now,
                0,
                "Ini Deskripsi."
        ));
    }

    @Test
    void testCreateCampaignNegativeTarget() {
        String status = CampaignStatus.ACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                status,
                now,
                -1,
                "Ini Deskripsi."
        ));
    }

    @Test
    void testCreateCampaignNullFundraiserId() {
        String status = CampaignStatus.ACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                null,
                "Donation Campaign",
                status,
                now,
                123123,
                "Ini Deskripsi."
        ));
    }

    @Test
    void testCreateCampaignEmptyStringFundraiserId() {
        String status = CampaignStatus.ACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "",
                "Donation Campaign",
                status,
                now,
                123123,
                "Ini Deskripsi."
        ));
    }

    @Test
    void testCreateCampaignActiveStatus() {
        String status = CampaignStatus.ACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                status,
                now,
                123123,
                "Ini deskripsi."
        );

        assertNotNull(campaign);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", campaign.getCampaignId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", campaign.getFundraiserId());
        assertEquals("Donation Campaign", campaign.getJudul());
        assertEquals(status, campaign.getStatus());
        assertEquals(now, campaign.getDatetime());
        assertEquals(123123, campaign.getTarget());
        assertEquals("Ini deskripsi.", campaign.getDeskripsi());
    }

    @Test
    void testCreateCampaignInactiveStatus() {
        String status = CampaignStatus.INACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                status,
                now,
                123123,
                "Ini deskripsi."
        );

        assertNotNull(campaign);
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", campaign.getCampaignId());
        assertEquals("eb558e9f-1c39-460e-8860-71af6af63bd6", campaign.getFundraiserId());
        assertEquals("Donation Campaign", campaign.getJudul());
        assertEquals(status, campaign.getStatus());
        assertEquals(now, campaign.getDatetime());
        assertEquals(123123, campaign.getTarget());
        assertEquals("Ini deskripsi.", campaign.getDeskripsi());
    }

    @Test
    void testCreateCampaignInvalidStatus() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                "",
                now,
                12,
                "Ga valid bang"
        ));
    }
}