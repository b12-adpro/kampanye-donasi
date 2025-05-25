package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.exception.WithdrawServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import id.ac.ui.cs.advprog.tk_adpro.repository.CampaignRepository;
import id.ac.ui.cs.advprog.tk_adpro.strategy.WithdrawStrategy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private WithdrawStrategy withdrawStrategy;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    private Campaign campaign;
    private Campaign campaign2;
    private final String sampleBukti = "http://example.com/bukti.jpg";

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        UUID uuidCampaign1 = UUID.randomUUID();
        UUID uuidCampaign2 = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        campaign = new Campaign(
                uuidCampaign1,
                uuidFundraiser,
                "Donation Campaign 1",
                CampaignStatus.PENDING.getValue(),
                now,
                123123,
                "Ini deskripsi 1.",
                sampleBukti
        );
        campaign2 = new Campaign(
                uuidCampaign2,
                uuidFundraiser,
                "Donation Campaign 2",
                CampaignStatus.ACTIVE.getValue(),
                now.plusDays(1),
                200000,
                "Ini deskripsi 2.",
                null
        );
    }

    @Test
    void testCheckBalanceSuccess() {
        when(withdrawStrategy.checkBalance(campaign.getCampaignId())).thenReturn(123123);
        Campaign result = campaignService.checkBalance(campaign);
        assertEquals(campaign, result);
    }

    @Test
    void testCheckBalanceFailed() {
        when(withdrawStrategy.checkBalance(campaign.getCampaignId())).thenReturn(123122);
        assertThrows(WithdrawServiceException.class, () -> campaignService.checkBalance(campaign));
    }

    @Test
    void testWithdrawSuccess() {
        when(withdrawStrategy.withdrawMoney(campaign.getFundraiserId(), campaign.getTarget())).thenReturn(true);
        Campaign result = campaignService.withdrawMoney(campaign);
        assertEquals(campaign, result);
    }

    @Test
    void testWithdrawFailed() {
        when(withdrawStrategy.withdrawMoney(campaign.getFundraiserId(), campaign.getTarget())).thenReturn(false);
        assertThrows(WithdrawServiceException.class, () -> campaignService.withdrawMoney(campaign));
    }

    @Test
    void testCreateCampaign() {
        Campaign newCampaign = new Campaign(
                UUID.randomUUID(), UUID.randomUUID(), "New",
                CampaignStatus.PENDING.getValue(), // <-- Berikan status awal yang valid
                LocalDateTime.now(), 100, "Desc", null
        );

        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign saved = invocation.getArgument(0);
            // Service akan mengatur ini, jadi mock bisa mencerminkannya atau tidak,
            // tergantung apa yang ingin Anda uji dari mock save.
            // Untuk konsistensi, kita bisa pastikan mock juga mengembalikan PENDING.
            saved.setStatus(CampaignStatus.PENDING.getValue());
            return saved;
        });

        Campaign created = campaignService.createCampaign(newCampaign);

        assertEquals(CampaignStatus.PENDING.getValue(), created.getStatus());
        verify(campaignRepository).save(argThat(c -> c.getStatus().equals(CampaignStatus.PENDING.getValue())));
    }

    @Test
    void testCreateCampaign_WithNullId_GeneratesUUID() {
        Campaign campaign1 = new Campaign(
                null,
                UUID.randomUUID(),
                "Campaign Donation",
                CampaignStatus.PENDING.getValue(), // <-- Berikan status awal yang valid
                LocalDateTime.now(),
                123123,
                "Get well soon!",
                sampleBukti
        );
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> {
            Campaign saved = invocation.getArgument(0);
            saved.setStatus(CampaignStatus.PENDING.getValue()); // Service akan mengatur ini
            if (saved.getCampaignId() == null) {
                saved.setCampaignId(UUID.randomUUID()); // Service tidak secara eksplisit generate ID, itu tugas @PrePersist
            }
            return saved;
        });

        Campaign result = campaignService.createCampaign(campaign1);
        verify(campaignRepository).save(result); // Verifikasi objek yang dikembalikan oleh service
        assertEquals(CampaignStatus.PENDING.getValue(), result.getStatus());
        // Bandingkan field lain, status dan ID mungkin berbeda dari campaign1 awal
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("campaignId", "status") // Abaikan status karena sudah dicek, ID karena bisa null
                .isEqualTo(campaign1); // Bandingkan field lain
    }

    @Test
    void testActivateCampaign_FromPending() {
        campaign.setStatus(CampaignStatus.PENDING.getValue());
        when(campaignRepository.findById(campaign.getCampaignId())).thenReturn(Optional.of(campaign));
        campaignService.activateCampaign(campaign.getCampaignId());
        verify(campaignRepository).save(argThat(c -> c.getStatus().equals(CampaignStatus.ACTIVE.getValue())));
    }

    @Test
    void testActivateCampaign_CampaignNotFound() {
        UUID campaignId = UUID.randomUUID();
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> campaignService.activateCampaign(campaignId));
    }

    @Test
    void testInactivateCampaign_FromPending() {
        campaign.setStatus(CampaignStatus.PENDING.getValue());
        when(campaignRepository.findById(campaign.getCampaignId())).thenReturn(Optional.of(campaign));
        campaignService.inactivateCampaign(campaign.getCampaignId());
        verify(campaignRepository).save(argThat(c -> c.getStatus().equals(CampaignStatus.INACTIVE.getValue())));
    }

    @Test
    void testInactivateCampaign_CampaignNotFound() {
        UUID campaignId = UUID.randomUUID();
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> campaignService.inactivateCampaign(campaignId));
    }

    @Test
    void testGetCampaignByCampaignId() {
        UUID uuidDummyCampaign = campaign.getCampaignId();
        when(campaignRepository.findById(uuidDummyCampaign)).thenReturn(Optional.of(campaign));

        Campaign result = campaignService.getCampaignByCampaignId(uuidDummyCampaign);

        assertEquals(uuidDummyCampaign, result.getCampaignId());
        assertEquals(sampleBukti, result.getBuktiPenggalanganDana());
    }

    @Test
    void testGetCampaignByCampaignId_NotFound() {
        UUID campaignId = UUID.randomUUID();
        when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());
        Campaign result = campaignService.getCampaignByCampaignId(campaignId);
        assertNull(result);
    }

    @Test
    void testGetCampaignByFundraiserId() {
        UUID uuidDummyFundraiser = campaign.getFundraiserId();
        List<Campaign> dummyList = List.of(campaign);
        when(campaignRepository.findByFundraiserId(uuidDummyFundraiser)).thenReturn(dummyList);

        List<Campaign> result = campaignService.getCampaignByFundraiserId(uuidDummyFundraiser);

        assertEquals(1, result.size());
        assertEquals(uuidDummyFundraiser, result.get(0).getFundraiserId());
        assertEquals(sampleBukti, result.get(0).getBuktiPenggalanganDana());
    }

    @Test
    void testUpdateCampaign() {
        UUID campaignId = UUID.randomUUID();

        Campaign existingCampaign = new Campaign();
        existingCampaign.setCampaignId(campaignId);
        existingCampaign.setJudul("Old Title");
        existingCampaign.setTarget(50);
        existingCampaign.setDeskripsi("Old Description");
        existingCampaign.setBuktiPenggalanganDana("http://old.com/proof.pdf");

        Campaign updatedCampaignData = new Campaign();
        updatedCampaignData.setJudul("New Title");
        updatedCampaignData.setTarget(100);
        updatedCampaignData.setDeskripsi("New Description");
        updatedCampaignData.setBuktiPenggalanganDana("http://new.com/proof.pdf");

        Campaign expectedResult = new Campaign();
        expectedResult.setCampaignId(campaignId);
        expectedResult.setJudul("New Title");
        expectedResult.setTarget(100);
        expectedResult.setDeskripsi("New Description");
        expectedResult.setBuktiPenggalanganDana("http://new.com/proof.pdf");

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(existingCampaign));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(expectedResult);

        Campaign result = campaignService.updateCampaign(campaignId, updatedCampaignData);

        assertNotNull(result);
        assertEquals(campaignId, result.getCampaignId());
        assertEquals("New Title", result.getJudul());
        assertEquals(100, result.getTarget());
        assertEquals("New Description", result.getDeskripsi());
        assertEquals("http://new.com/proof.pdf", result.getBuktiPenggalanganDana());

        verify(campaignRepository).findById(campaignId);
        verify(campaignRepository).save(any(Campaign.class));
    }

    @Test
    void testDeleteCampaign() {
        campaignService.deleteCampaign(campaign.getCampaignId());
        verify(campaignRepository).deleteById(campaign.getCampaignId());
    }

    @Test
    void testGetBuktiPenggalanganDana_Found() {
        String buktiUrl = "http://example.com/bukti.png";
        campaign.setBuktiPenggalanganDana(buktiUrl);
        when(campaignRepository.findById(campaign.getCampaignId())).thenReturn(Optional.of(campaign));

        String result = campaignService.getBuktiPenggalanganDana(campaign.getCampaignId());

        assertEquals(buktiUrl, result);
        verify(campaignRepository).findById(campaign.getCampaignId());
    }

    @Test
    void testGetBuktiPenggalanganDana_NullProof() {
        campaign.setBuktiPenggalanganDana(null);
        when(campaignRepository.findById(campaign.getCampaignId())).thenReturn(Optional.of(campaign));

        String result = campaignService.getBuktiPenggalanganDana(campaign.getCampaignId());

        assertNull(result);
        verify(campaignRepository).findById(campaign.getCampaignId());
    }

    @Test
    void testGetBuktiPenggalanganDana_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(campaignRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            campaignService.getBuktiPenggalanganDana(randomId);
        });
        verify(campaignRepository).findById(randomId);
    }

    @Test
    void testGetAllCampaigns() {
        List<Campaign> expectedCampaigns = Arrays.asList(campaign, campaign2);
        when(campaignRepository.findAll()).thenReturn(expectedCampaigns);

        List<Campaign> actualCampaigns = campaignService.getAllCampaigns();

        assertEquals(expectedCampaigns.size(), actualCampaigns.size());
        assertEquals(expectedCampaigns, actualCampaigns);
        verify(campaignRepository).findAll();
    }

    @Test
    void testGetAllCampaigns_Empty() {
        when(campaignRepository.findAll()).thenReturn(new ArrayList<>());

        List<Campaign> actualCampaigns = campaignService.getAllCampaigns();

        assertTrue(actualCampaigns.isEmpty());
        verify(campaignRepository).findAll();
    }
}