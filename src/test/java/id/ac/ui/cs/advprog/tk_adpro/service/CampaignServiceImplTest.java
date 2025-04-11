import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import id.ac.ui.cs.advprog.tk_adpro.repository.CampaignRepository;
import id.ac.ui.cs.advprog.tk_adpro.state.CampaignStatusState;
import id.ac.ui.cs.advprog.tk_adpro.state.CampaignStatusStateFactory;
import id.ac.ui.cs.advprog.tk_adpro.strategy.WithdrawStrategy;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    private Campaign campaign;


    @BeforeEach
    void setUp() {
        CampaignStatus campaignStatus;
        LocalDateTime now = LocalDateTime.now();
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                now,
                123123,
                "Ini deskripsi."
        );
    }

    @Test
    void testCheckBalance() {
        when(campaignRepository.findByFundraiserId("eb558e9f-1c39-460e-8860-71af6af63bd6")).thenReturn(List.of(campaign));
        int result = campaignService.checkBalance("eb558e9f-1c39-460e-8860-71af6af63bd6");
        assertEquals(200, result);
    }

    @Test
    void testCreateCampaign() {
        when(campaignRepository.save(any())).thenReturn(campaign);
        Campaign created = campaignService.createCampaign(campaign);
        assertEquals(CampaignStatus.INACTIVE.getValue(), created.getStatus());
    }

    @Test
    void testActivateCampaign() {
        campaign.setStatus(CampaignStatus.INACTIVE.getValue());
        when(campaignRepository.findByCampaignId("13652556-012a-4c07-b546-54eb1396d79b")).thenReturn(campaign);
        campaignService.activateCampaign("13652556-012a-4c07-b546-54eb1396d79b");
        verify(campaignRepository).save(argThat(c -> c.getStatus().equals(CampaignStatus.ACTIVE.getValue())));
    }

    @Test
    void testInactivateCampaign() {
        campaign.setStatus(CampaignStatus.ACTIVE.getValue());
        when(campaignRepository.findByCampaignId("13652556-012a-4c07-b546-54eb1396d79b")).thenReturn(campaign);
        campaignService.inactivateCampaign("13652556-012a-4c07-b546-54eb1396d79b");
        verify(campaignRepository).save(argThat(c -> c.getStatus().equals(CampaignStatus.INACTIVE.getValue())));
    }

    @Test
    void testGetCampaignById() {
        when(campaignRepository.findByCampaignId("13652556-012a-4c07-b546-54eb1396d79b")).thenReturn(campaign);
        Campaign result = campaignService.getCampaignByCampaignId("13652556-012a-4c07-b546-54eb1396d79b");
        assertEquals("13652556-012a-4c07-b546-54eb1396d79b", result.getCampaignId());
    }

    @Test
    void testGetCampaignByFundraiserId() {
        when(campaignRepository.findByFundraiserId("eb558e9f-1c39-460e-8860-71af6af63bd6")).thenReturn(List.of(campaign));
        List<Campaign> results = campaignService.getCampaignByFundraiserId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        assertEquals(1, results.size());
    }

    @Test
    void testUpdateJudul() {
        when(campaignRepository.findByCampaignId("13652556-012a-4c07-b546-54eb1396d79b")).thenReturn(campaign);
        when(campaignRepository.save(any())).thenReturn(campaign);
        Campaign updated = campaignService.updateCampaignJudul("13652556-012a-4c07-b546-54eb1396d79b", "New Judul");
        assertEquals("New Judul", updated.getJudul());
    }

    @Test
    void testUpdateTarget() {
        when(campaignRepository.findByCampaignId("13652556-012a-4c07-b546-54eb1396d79b").thenReturn(campaign);
        when(campaignRepository.save(any())).thenReturn(campaign);
        Campaign updated = campaignService.updateCampaignTarget("13652556-012a-4c07-b546-54eb1396d79b", 2000);
        assertEquals(2000, updated.getTarget());
    }

    @Test
    void testUpdateDeskripsi() {
        when(campaignRepository.findByCampaignId("13652556-012a-4c07-b546-54eb1396d79b")).thenReturn(campaign);
        when(campaignRepository.save(any())).thenReturn(campaign);
        Campaign updated = campaignService.updateCampaignDeskripsi("13652556-012a-4c07-b546-54eb1396d79b", "Updated");
        assertEquals("Updated", updated.getDeskripsi());
    }

    @Test
    void testDeleteCampaign() {
        campaignService.deleteCampaign("13652556-012a-4c07-b546-54eb1396d79b");
        verify(campaignRepository).deleteByCampaignId("13652556-012a-4c07-b546-54eb1396d79b");
    }
}
