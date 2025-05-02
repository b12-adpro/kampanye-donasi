package id.ac.ui.cs.advprog.tk_adpro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.service.CampaignService;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import id.ac.ui.cs.advprog.tk_adpro.service.DonationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CampaignController.class)
public class CampaignControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampaignService campaignService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCampaignPage() throws Exception {
        String fundraiserId = "fundraiser123";
        Campaign campaign = new Campaign();
        campaign.setFundraiserId(fundraiserId);
        campaign.setCampaignId(UUID.randomUUID().toString());

        mockMvc.perform(get("/campaign/{fundraiserId}/create-campaign-page", fundraiserId))
                .andExpect(status().isOk())
                .andExpect(view().name("CampaignForm"))
                .andExpect(model().attributeExists("campaign"));
    }

    @Test
    void testVerifyCampaign() throws Exception {
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );

        mockMvc.perform(post("/campaign/{fundraiserId}/campaign", campaign.getFundraiserId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("campaign", campaign))
                .andExpect(status().isOk())
                .andExpect(view().name("VerifyCampaign"));

        verify(campaignService).createCampaign(any(Campaign.class));
    }

    @Test
    void testCreateCampaign() throws Exception {
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );

        mockMvc.perform(post("/campaign/{campaignId}/create-campaign", campaign.getCampaignId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("campaign", campaign))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(campaignService).createCampaign(any(Campaign.class));
    }

    @Test
    void testActivateCampaign() throws Exception {
        String campaignId = "campaign123";

        mockMvc.perform(put("/campaign/{campaignId}/activate", campaignId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(campaignService).activateCampaign(campaignId);
    }

    @Test
    void testInactivateCampaign() throws Exception {
        String campaignId = "campaign123";
        mockMvc.perform(put("/campaign/{campaignId}/inactivate", campaignId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(campaignService).inactivateCampaign(campaignId);
    }

    @Test
    void testGetCampaignByFundraiserId() throws Exception {
        String fundraiserId = "fundraiser123";
        Campaign campaign1 = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                fundraiserId,
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        Campaign campaign2 = new Campaign(
                "23652556-012a-4c07-b546-54eb1396d79b",
                fundraiserId,
                "Donation Campaign 2",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123123,
                "Ini deskripsi 2."
        );
        List<Campaign> campaigns = Arrays.asList(campaign1, campaign2);

        when(campaignService.getCampaignByFundraiserId(fundraiserId)).thenReturn(campaigns);

        mockMvc.perform(get("/campaign/get-by-fundraiser/{fundraiserId}", fundraiserId))
                .andExpect(status().isOk())
                .andExpect(view().name("CampaignList"))
                .andExpect(model().attributeExists("campaigns"));
    }

    @Test
    void testGetCampaignByCampaignId() throws Exception {
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );

        when(campaignService.getCampaignByCampaignId(campaign.getCampaignId())).thenReturn(campaign);

        mockMvc.perform(get("/campaign/get-by-id/{campaignId}", campaign.getCampaignId()))
                .andExpect(status().isOk())
                .andExpect(view().name("CampaignDetail"))
                .andExpect(model().attributeExists("campaign"));
    }

    @Test
    public void testUpdateCampaignJudul() throws Exception {
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        campaign.setCampaignId("123");
        campaign.setJudul("Updated Judul");

        mockMvc.perform(put("/campaign/update-judul")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/campaign/get-by-id/123"));

        Mockito.verify(campaignService).updateCampaignJudul("123", "Updated Judul");
    }

    @Test
    public void testUpdateCampaignTarget() throws Exception {
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        campaign.setCampaignId("123");
        campaign.setTarget(50000);

        mockMvc.perform(put("/campaign/update-target")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/campaign/get-by-id/123"));

        Mockito.verify(campaignService).updateCampaignTarget("123", 50000);
    }

    @Test
    public void testUpdateCampaignDeskripsi() throws Exception {
        Campaign campaign = new Campaign(
                "13652556-012a-4c07-b546-54eb1396d79b",
                "eb558e9f-1c39-460e-8860-71af6af63bd6",
                "Donation Campaign",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(),
                123123,
                "Ini deskripsi."
        );
        campaign.setCampaignId("123");
        campaign.setDeskripsi("Updated Deskripsi");

        mockMvc.perform(put("/campaign/update-deskripsi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/campaign/get-by-id/123"));

        Mockito.verify(campaignService).updateCampaignDeskripsi("123", "Updated Deskripsi");
    }

    @Test
    public void testDeleteCampaign() throws Exception {
        mockMvc.perform(post("/campaign/123/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Mockito.verify(campaignService).deleteCampaign("123");
    }
}
