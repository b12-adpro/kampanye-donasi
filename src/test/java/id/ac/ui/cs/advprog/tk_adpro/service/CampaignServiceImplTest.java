package id.ac.ui.cs.advprog.tk_adpro.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.service.CampaignServiceImpl;
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
        LocalDateTime now = LocalDateTime.now();
        campaign = new Campaign( // <-- ini assign ke field yang udah dideklarasikan di atas
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
    void testGetCampaignByCampaignId() {
        Campaign dummyCampaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        dummyCampaign.setCampaignId("123");

        when(campaignRepository.findByCampaignId("123")).thenReturn(dummyCampaign);

        Campaign result = campaignService.getCampaignByCampaignId("123");

        assertEquals("123", result.getCampaignId());
    }


    @Test
    void testGetCampaignByFundraiserId() {
        Campaign dummyCampaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        dummyCampaign.setFundraiserId("abc");
        List<Campaign> dummyList = List.of(dummyCampaign);

        when(campaignRepository.findByFundraiserId("abc")).thenReturn(dummyList);

        List<Campaign> result = campaignService.getCampaignByFundraiserId("abc");

        assertEquals(1, result.size());
        assertEquals("abc", result.get(0).getFundraiserId());
    }


    @Test
    void testUpdateCampaignJudul() {
        Campaign dummyCampaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        dummyCampaign.setCampaignId("1");
        dummyCampaign.setJudul("Old Title");

        when(campaignRepository.findByCampaignId("1")).thenReturn(dummyCampaign);

        campaignService.updateCampaignJudul("1", "New Title");

        assertEquals("New Title", dummyCampaign.getJudul());
    }

    @Test
    void testUpdateCampaignTarget() {
        Campaign dummyCampaign = new Campaign(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            "Donation Campaign",
            CampaignStatus.ACTIVE.getValue(),
            LocalDateTime.now(),
            123123,
            "Ini deskripsi."
        );
        dummyCampaign.setCampaignId("1");
        dummyCampaign.setTarget(100);

        when(campaignRepository.findByCampaignId("1")).thenReturn(dummyCampaign);

        campaignService.updateCampaignTarget("1", 500);

        assertEquals(500, dummyCampaign.getTarget());
    }

    @Test
    void testUpdateCampaignDeskripsi() {
        Campaign dummyCampaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        dummyCampaign.setCampaignId("1");
        dummyCampaign.setDeskripsi("Old");

        when(campaignRepository.findByCampaignId("1")).thenReturn(dummyCampaign);

        campaignService.updateCampaignDeskripsi("1", "New");

        assertEquals("New", dummyCampaign.getDeskripsi());
    }


    @Test
    void testDeleteCampaign() {
        campaignService.deleteCampaign("13652556-012a-4c07-b546-54eb1396d79b");
        verify(campaignRepository).deleteByCampaignId("13652556-012a-4c07-b546-54eb1396d79b");
    }
}
