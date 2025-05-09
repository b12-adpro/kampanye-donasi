package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

class CampaignRepositoryTest {
    CampaignRepository campaignRepository;
    UUID uuidCampaign = UUID.randomUUID();
    UUID uuidFundraiser = UUID.randomUUID();
    Campaign campaign = new Campaign(
            uuidCampaign,
            uuidFundraiser,
            "Donation Campaign",
            CampaignStatus.ACTIVE.getValue(),
            LocalDateTime.now(),
            123123,
            "Ini deskripsi."
    );

    @BeforeEach
    void setUp() {
        campaignRepository = new CampaignRepository();
    }

    @Test
    void testSaveCreate() {
        Campaign result = campaignRepository.save(campaign);
        assertEquals(campaign.getCampaignId(), result.getCampaignId());
        assertEquals(campaign.getFundraiserId(), result.getFundraiserId());
        assertEquals(campaign.getJudul(), result.getJudul());
        assertEquals(campaign.getDatetime(), result.getDatetime());
        assertEquals(campaign.getTarget(), result.getTarget());
        assertEquals(campaign.getDeskripsi(), result.getDeskripsi());
    }

    @Test
    void testSaveUpdate() {
        campaignRepository.save(campaign);
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign newCampaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign 2",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                1000,
                "Ini deskripsi 2."
        );
        Campaign result = campaignRepository.save(newCampaign);
        Campaign findResult = campaignRepository.findByCampaignId(newCampaign.getCampaignId());

        assertEquals(result.getCampaignId(), findResult.getCampaignId());
        assertEquals(result.getFundraiserId(), findResult.getFundraiserId());
        assertEquals(result.getJudul(), findResult.getJudul());
        assertEquals(result.getDatetime(), findResult.getDatetime());
        assertEquals(result.getTarget(), findResult.getTarget());
        assertEquals(result.getDeskripsi(), findResult.getDeskripsi());
    }

    @Test
    void testFindByCampaignIdIfCampaignIdFound() {
        campaignRepository.save(campaign);
        Campaign result = campaignRepository.findByCampaignId(campaign.getCampaignId());
        assertEquals(campaign.getCampaignId(), result.getCampaignId());
        assertEquals(campaign.getFundraiserId(), result.getFundraiserId());
        assertEquals(campaign.getJudul(), result.getJudul());
        assertEquals(campaign.getDatetime(), result.getDatetime());
        assertEquals(campaign.getTarget(), result.getTarget());
        assertEquals(campaign.getDeskripsi(), result.getDeskripsi());
    }

    @Test
    void testFindByIdCampaignIdIfCampaignIdNotFound() {
        Campaign result = campaignRepository.findByCampaignId(null);
        assertNull(result);
    }

    @Test
    void testFindByFundraiserIdIfIdFundraiserFound() {
        campaignRepository.save(campaign);
        List<Campaign> campaignList = campaignRepository.findByFundraiserId(campaign.getFundraiserId());
        assertEquals(1, campaignList.size());
    }

    @Test
    void testFindByFundraiserIdIfIdFundraiserNotFound() {
        List<Campaign> campaignList = campaignRepository.findByFundraiserId(null);
        assertTrue(campaignList.isEmpty());
    }

    @Test
    void testDeleteByCampaignId_shouldRemoveCampaign() {
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign campaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );

        campaignRepository.save(campaign);

        assertNotNull(campaignRepository.findByCampaignId(uuidCampaign));

        campaignRepository.deleteByCampaignId(uuidCampaign);
        assertNull(campaignRepository.findByCampaignId(uuidFundraiser));
    }


    @Test
    void testDeleteByCampaignId_nonExistingId_shouldDoNothing() {
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        int initialSize = campaignRepository.findByFundraiserId(uuidFundraiser).size();
        campaignRepository.deleteByCampaignId(null);
        int finalSize = campaignRepository.findByFundraiserId(uuidFundraiser).size();
        assertEquals(initialSize, finalSize);
    }
}