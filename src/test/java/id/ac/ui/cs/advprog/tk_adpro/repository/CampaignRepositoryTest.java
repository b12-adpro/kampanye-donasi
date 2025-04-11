package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

class CampaignRepositoryTest {
    CampaignRepository campaignRepository;
    Campaign campaign = new Campaign(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
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

        Campaign newCampaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign 2",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                1000,
                "Ini deskripsi 2."
        );
        Campaign result = campaignRepository.save(newCampaign);
        Campaign findResult = campaignRepository.findByCampaignId(campaign.getCampaignId());

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
        Campaign result = campaignRepository.findByCampaignId("YAH GAADA");
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
        List<Campaign> campaignList = campaignRepository.findByFundraiserId("GAADA YAH");
        assertTrue(campaignList.isEmpty());
    }

    @Test
    void testDeleteByCampaignId_shouldRemoveCampaign() {
        assertNotNull(campaignRepository.findByCampaignId("13652556-012a-4c07-b546-54eb1396d79b"));
        campaignRepository.deleteByCampaignId("13652556-012a-4c07-b546-54eb1396d79b");
        assertNull(campaignRepository.findByCampaignId("13652556-012a-4c07-b546-54eb1396d79b"));
    }

    @Test
    void testDeleteByCampaignId_nonExistingId_shouldDoNothing() {
        int initialSize = campaignRepository.findByFundraiserId("eb558e9f-1c39-460e-8860-71af6af63bd6").size();
        campaignRepository.deleteByCampaignId("999");
        int finalSize = campaignRepository.findByFundraiserId("eb558e9f-1c39-460e-8860-71af6af63bd6").size();
        assertEquals(initialSize, finalSize);
    }
}