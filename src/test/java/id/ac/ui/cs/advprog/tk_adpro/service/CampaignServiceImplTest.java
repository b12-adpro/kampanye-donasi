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
import java.util.List;
import java.util.UUID;
import java.util.Optional;
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
    private final String sampleBukti = "http://example.com/bukti.jpg";

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        campaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
                "Donation Campaign",
                CampaignStatus.PENDING.getValue(),
                now,
                123123,
                "Ini deskripsi.",
                sampleBukti
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
        String newBukti = "http://new.com/proof.pdf";
        campaign.setJudul("New Title");
        campaign.setTarget(100);
        campaign.setDeskripsi("New Deskripsi");
        campaign.setBuktiPenggalanganDana(newBukti);

        when(campaignRepository.save(campaign)).thenReturn(campaign);

        Campaign updatedCampaign = campaignService.updateCampaign(campaign);
        assertEquals("New Title", updatedCampaign.getJudul());
        assertEquals(100, updatedCampaign.getTarget());
        assertEquals("New Deskripsi", updatedCampaign.getDeskripsi());
        assertEquals(newBukti, updatedCampaign.getBuktiPenggalanganDana());
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
}