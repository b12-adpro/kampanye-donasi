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

    String sampleBukti = "http://example.com/bukti_repo.jpg";

    Campaign campaign = new Campaign(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Donation Campaign",
            CampaignStatus.ACTIVE.getValue(),
            LocalDateTime.now(),
            123123,
            "Ini deskripsi.",
            sampleBukti
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
        assertEquals(campaign.getBuktiPenggalanganDana(), found.getBuktiPenggalanganDana());
    }

    @Test
    void testSaveUpdate() {
        campaignRepository.save(campaign);

        String updatedBukti = "http://updated.com/bukti.png";
        Campaign newCampaign = new Campaign(
                campaign.getCampaignId(),
                campaign.getFundraiserId(),
                "Donation Campaign 2",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                1000,
                "Ini deskripsi 2.",
                updatedBukti
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
        assertEquals(updatedBukti, findResult.getBuktiPenggalanganDana());
    }

    @Test
    void testFindByCampaignIdIfCampaignIdFound() {
        campaignRepository.save(campaign);
        Optional<Campaign> result = campaignRepository.findById(campaign.getCampaignId());
        assertTrue(result.isPresent());
        assertEquals(campaign.getCampaignId(), result.get().getCampaignId());
        assertEquals(sampleBukti, result.get().getBuktiPenggalanganDana());
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
        assertEquals(sampleBukti, campaignList.get(0).getBuktiPenggalanganDana());
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
        Campaign campaignToDelete = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi.",
                null
        );

        campaignRepository.save(campaignToDelete);

        Optional<Campaign> foundBefore = campaignRepository.findById(uuidCampaign);
        assertTrue(foundBefore.isPresent());

        campaignRepository.deleteById(uuidCampaign);

        Optional<Campaign> foundAfter = campaignRepository.findById(uuidCampaign);
        assertFalse(foundAfter.isPresent());
    }

    @Test
    void testDeleteByCampaignId_nonExistingId_shouldDoNothing() {
        long initialCount = campaignRepository.count();
        campaignRepository.deleteById(UUID.randomUUID());
        long finalCount = campaignRepository.count();
        assertEquals(initialCount, finalCount);
    }
}