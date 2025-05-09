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
import java.util.UUID;

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
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        campaign = new Campaign(
                uuidCampaign,
                uuidFundraiser,
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
        when(campaignRepository.findByCampaignId(campaign.getCampaignId())).thenReturn(campaign);
        campaignService.activateCampaign(campaign.getCampaignId());
        verify(campaignRepository).save(argThat(c -> c.getStatus().equals(CampaignStatus.ACTIVE.getValue())));
    }

    @Test
    void testInactivateCampaign() {
        campaign.setStatus(CampaignStatus.ACTIVE.getValue());
        when(campaignRepository.findByCampaignId(campaign.getCampaignId())).thenReturn(campaign);
        campaignService.inactivateCampaign(campaign.getCampaignId());
        verify(campaignRepository).save(argThat(c -> c.getStatus().equals(CampaignStatus.INACTIVE.getValue())));
    }

    @Test
    void testGetCampaignByCampaignId() {
        UUID uuidDummyCampaign = UUID.randomUUID();
        UUID uuidDummyFundraiser = UUID.randomUUID();
        Campaign dummyCampaign = new Campaign(
                uuidDummyCampaign,
                uuidDummyFundraiser,
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        UUID uuidNewDummyCampaign = UUID.randomUUID();
        dummyCampaign.setCampaignId(uuidNewDummyCampaign);

        when(campaignRepository.findByCampaignId(uuidNewDummyCampaign)).thenReturn(dummyCampaign);

        Campaign result = campaignService.getCampaignByCampaignId(uuidNewDummyCampaign);

        assertEquals(uuidNewDummyCampaign, result.getCampaignId());
    }


    @Test
    void testGetCampaignByFundraiserId() {
        UUID uuidDummyCampaign = UUID.randomUUID();
        UUID uuidDummyFundraiser = UUID.randomUUID();
        Campaign dummyCampaign = new Campaign(
                uuidDummyCampaign,
                uuidDummyFundraiser,
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        UUID uuidNewDummyFundraiser = UUID.randomUUID();
        dummyCampaign.setFundraiserId(uuidNewDummyFundraiser);
        List<Campaign> dummyList = List.of(dummyCampaign);

        when(campaignRepository.findByFundraiserId(uuidNewDummyFundraiser)).thenReturn(dummyList);

        List<Campaign> result = campaignService.getCampaignByFundraiserId(uuidNewDummyFundraiser);

        assertEquals(1, result.size());
        assertEquals(uuidNewDummyFundraiser, result.get(0).getFundraiserId());
    }

    @Test
    void testUpdateCampaign() {
        UUID uuidDummyCampaign = UUID.randomUUID();
        UUID uuidDummyFundraiser = UUID.randomUUID();
        Campaign dummyCampaign = new Campaign(
                uuidDummyCampaign,
                uuidDummyFundraiser,
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        UUID uuidNewDummyCampaign = UUID.randomUUID();
        dummyCampaign.setCampaignId(uuidNewDummyCampaign);
        dummyCampaign.setJudul("New Title");
        dummyCampaign.setTarget(100);
        dummyCampaign.setDeskripsi("New Deskripsi");

        when(campaignRepository.save(dummyCampaign)).thenReturn(dummyCampaign);

        Campaign updatedCampaign = campaignService.updateCampaign(dummyCampaign);
        assertEquals("New Title", updatedCampaign.getJudul());
        assertEquals(100, updatedCampaign.getTarget());
        assertEquals("New Deskripsi", updatedCampaign.getDeskripsi());
    }

    @Test
    void testDeleteCampaign() {
        campaignService.deleteCampaign(campaign.getCampaignId());
        verify(campaignRepository).deleteByCampaignId(campaign.getCampaignId());
    }
}
