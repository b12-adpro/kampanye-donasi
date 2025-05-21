package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@DataJpaTest
class CampaignRepositoryTest {
    @Autowired
    CampaignRepository campaignRepository;

    Campaign campaign = new Campaign(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Donation Campaign",
            CampaignStatus.ACTIVE.getValue(),
            LocalDateTime.now(),
            123123,
            "Ini deskripsi."
    );

    @Test
    void testSaveCreate() {
        Campaign saved = campaignRepository.save(campaign);
        assertNotNull(saved.getCampaignId());

        Optional<Campaign> foundOpt = campaignRepository.findById(saved.getCampaignId());
        assertTrue(foundOpt.isPresent());

        Campaign found = foundOpt.get();
        assertEquals(campaign.getCampaignId(), found.getCampaignId());
        assertEquals(campaign.getFundraiserId(), found.getFundraiserId());
        assertEquals(campaign.getJudul(), found.getJudul());
        assertEquals(campaign.getDatetime(), found.getDatetime());
        assertEquals(campaign.getTarget(), found.getTarget());
        assertEquals(campaign.getDeskripsi(), found.getDeskripsi());
    }

    @Test
    void testSaveUpdate() {
        campaignRepository.save(campaign);

        Campaign newCampaign = new Campaign(
                campaign.getCampaignId(),
                campaign.getFundraiserId(),
                "Donation Campaign 2",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                1000,
                "Ini deskripsi 2."
        );
        Campaign result = campaignRepository.save(newCampaign);
        Optional<Campaign> findOpt = campaignRepository.findById(campaign.getCampaignId());
        assertTrue(findOpt.isPresent());

        Campaign findResult = findOpt.get();

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
        Optional<Campaign> result = campaignRepository.findById(campaign.getCampaignId());
        assertTrue(result.isPresent());
        assertEquals(campaign.getCampaignId(), result.get().getCampaignId());
    }

    @Test
    void testFindByIdCampaignIdIfCampaignIdNotFound() {
        Optional<Campaign> result = campaignRepository.findById(UUID.randomUUID());
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByFundraiserIdIfIdFundraiserFound() {
        campaignRepository.save(campaign);
        List<Campaign> campaignList = campaignRepository.findByFundraiserId(campaign.getFundraiserId());
        assertEquals(1, campaignList.size());
    }

    @Test
    void testFindByFundraiserIdIfIdFundraiserNotFound() {
        List<Campaign> campaignList = campaignRepository.findByFundraiserId(UUID.randomUUID());
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