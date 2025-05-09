package id.ac.ui.cs.advprog.tk_adpro.model;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CampaignTest {
    @Test
    void testCreateCampaignWithDeskripsi() {
        String status = CampaignStatus.ACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign campaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign",
                status,
                now,
                123123,
                "Ini deskripsi."
        );

        assertNotNull(campaign);
        assertEquals(uuidCampaign, campaign.getCampaignId());
        assertEquals(uuidFundraiser, campaign.getFundraiserId());
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
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign campaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign",
                status,
                now,
                123123
        );

        assertNotNull(campaign);
        assertEquals(uuidCampaign, campaign.getCampaignId());
        assertEquals(uuidFundraiser, campaign.getFundraiserId());
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
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign campaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign",
                status,
                now,
                123123,
                null
        );

        assertNotNull(campaign);
        assertEquals(uuidCampaign, campaign.getCampaignId());
        assertEquals(uuidFundraiser, campaign.getFundraiserId());
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
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> new Campaign(
                uuidCampaign,
                uuidFundraiser,
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
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> new Campaign(
                uuidCampaign,
                uuidFundraiser,
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
        UUID uuidCampaign = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> new Campaign(
                uuidCampaign,
                null,
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
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign campaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign",
                status,
                now,
                123123,
                "Ini deskripsi."
        );

        assertNotNull(campaign);
        assertEquals(uuidCampaign, campaign.getCampaignId());
        assertEquals(uuidFundraiser, campaign.getFundraiserId());
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
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign campaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign",
                status,
                now,
                123123,
                "Ini deskripsi."
        );

        assertNotNull(campaign);
        assertEquals(uuidCampaign, campaign.getCampaignId());
        assertEquals(uuidFundraiser, campaign.getFundraiserId());
        assertEquals("Donation Campaign", campaign.getJudul());
        assertEquals(status, campaign.getStatus());
        assertEquals(now, campaign.getDatetime());
        assertEquals(123123, campaign.getTarget());
        assertEquals("Ini deskripsi.", campaign.getDeskripsi());
    }

    @Test
    void testCreateCampaignInvalidStatus() {
        LocalDateTime now = LocalDateTime.now();
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign",
                "",
                now,
                12,
                "Ga valid bang"
        ));
    }
}