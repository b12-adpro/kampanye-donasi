package id.ac.ui.cs.advprog.tk_adpro.model;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CampaignTest {

    private final String sampleBukti = "http://example.com/bukti.jpg";

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
        assertNull(campaign.getBuktiPenggalanganDana());
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
        assertNull(campaign.getBuktiPenggalanganDana());
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
        assertNull(campaign.getBuktiPenggalanganDana());
    }

    @Test
    void testCreateCampaignWithBukti() {
        String status = CampaignStatus.ACTIVE.getValue();
        LocalDateTime now = LocalDateTime.now();
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign campaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Campaign With Proof",
                status,
                now,
                200000,
                "With description",
                sampleBukti
        );

        assertNotNull(campaign);
        assertEquals(uuidCampaign, campaign.getCampaignId());
        assertEquals(uuidFundraiser, campaign.getFundraiserId());
        assertEquals("Campaign With Proof", campaign.getJudul());
        assertEquals(status, campaign.getStatus());
        assertEquals(now, campaign.getDatetime());
        assertEquals(200000, campaign.getTarget());
        assertEquals("With description", campaign.getDeskripsi());
        assertEquals(sampleBukti, campaign.getBuktiPenggalanganDana());
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
        assertEquals(status, campaign.getStatus());
        assertNull(campaign.getBuktiPenggalanganDana());
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
        assertEquals(status, campaign.getStatus());
        assertNull(campaign.getBuktiPenggalanganDana());
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

    @Test
    void testSetAndGetBuktiPenggalanganDana() {
        Campaign campaign = new Campaign();
        assertNull(campaign.getBuktiPenggalanganDana());
        campaign.setBuktiPenggalanganDana(sampleBukti);
        assertEquals(sampleBukti, campaign.getBuktiPenggalanganDana());
    }

    @Test
    void testCampaignBuilderWithBukti() {
        LocalDateTime now = LocalDateTime.now();
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign campaign = Campaign.builder()
                .campaignId(uuidCampaign)
                .fundraiserId(uuidFundraiser)
                .judul("Builder Test")
                .status(CampaignStatus.ACTIVE.getValue())
                .datetime(now)
                .target(5000)
                .deskripsi("Built with Lombok")
                .buktiPenggalanganDana(sampleBukti)
                .build();

        assertEquals(uuidCampaign, campaign.getCampaignId());
        assertEquals("Builder Test", campaign.getJudul());
        assertEquals(5000, campaign.getTarget());
        assertEquals(sampleBukti, campaign.getBuktiPenggalanganDana());
    }

    @Test
    void testNoArgsConstructor() {
        Campaign campaign = new Campaign();
        assertNotNull(campaign);
        assertNull(campaign.getCampaignId());
        assertNull(campaign.getJudul());
        assertNull(campaign.getBuktiPenggalanganDana());
    }
}