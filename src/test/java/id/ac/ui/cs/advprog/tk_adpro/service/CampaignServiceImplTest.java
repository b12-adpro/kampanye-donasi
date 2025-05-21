package id.ac.ui.cs.advprog.tk_adpro.service;
import id.ac.ui.cs.advprog.tk_adpro.exception.WithdrawServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.service.CampaignServiceImpl;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import id.ac.ui.cs.advprog.tk_adpro.repository.CampaignRepository;
import id.ac.ui.cs.advprog.tk_adpro.state.CampaignStatusState;
import id.ac.ui.cs.advprog.tk_adpro.state.CampaignStatusStateFactory;
import id.ac.ui.cs.advprog.tk_adpro.strategy.WithdrawStrategy;
import id.ac.ui.cs.advprog.tk_adpro.exception.WithdrawServiceException;
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
        when(campaignRepository.save(any())).thenReturn(campaign);
        Campaign created = campaignService.createCampaign(campaign);
        assertEquals(CampaignStatus.ACTIVE.getValue(), created.getStatus());
    }

    @Test
    void testCreateCampaign_WithNullId_GeneratesUUID() {
        Campaign campaign1 = new Campaign(
                null,
                UUID.randomUUID(),
                "Campaign Donation",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Get well soon!"
        );
        when(campaignRepository.save(campaign1)).thenReturn(campaign1);

        Campaign result = campaignService.createCampaign(campaign1);
        verify(campaignRepository).save(campaign1);
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("campaignId")
                .isEqualTo(campaign1);
    }

    @Test
    void testActivateCampaign() {
        campaign.setStatus(CampaignStatus.INACTIVE.getValue());
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
    void testInactivateCampaign() {
        campaign.setStatus(CampaignStatus.ACTIVE.getValue());
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

        when(campaignRepository.findById(uuidNewDummyCampaign)).thenReturn(Optional.of(dummyCampaign));

        Campaign result = campaignService.getCampaignByCampaignId(uuidNewDummyCampaign);

        assertEquals(uuidNewDummyCampaign, result.getCampaignId());
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
