package id.ac.ui.cs.advprog.tk_adpro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import id.ac.ui.cs.advprog.tk_adpro.service.CampaignService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CampaignController.class)
public class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampaignService campaignService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testVerifyCampaign() throws Exception {
        Campaign campaign = new Campaign(
                "campaign123", "fundraiser123", "Judul",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(), 100000, "Deskripsi");

        when(campaignService.createCampaign(any(Campaign.class))).thenReturn(campaign);

        mockMvc.perform(post("/api/campaign/{fundraiserId}/campaign", campaign.getFundraiserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campaignId").value("campaign123"))
                .andExpect(jsonPath("$.judul").value("Judul"));

        verify(campaignService).createCampaign(any(Campaign.class));
    }

    @Test
    void testCreateCampaign() throws Exception {
        Campaign campaign = new Campaign(
                "campaign456", "fundraiser456", "Judul Baru",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(), 50000, "Deskripsi");

        when(campaignService.createCampaign(any(Campaign.class))).thenReturn(campaign);

        mockMvc.perform(post("/api/campaign/{campaignId}/create-campaign", campaign.getCampaignId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campaignId").value("campaign456"))
                .andExpect(jsonPath("$.judul").value("Judul Baru"));

        verify(campaignService).createCampaign(any(Campaign.class));
    }

    @Test
    void testActivateCampaign() throws Exception {
        String campaignId = "campaign123";

        mockMvc.perform(put("/api/campaign/{campaignId}/activate", campaignId))
                .andExpect(status().isOk());

        verify(campaignService).activateCampaign(campaignId);
    }

    @Test
    void testInactivateCampaign() throws Exception {
        String campaignId = "campaign456";

        mockMvc.perform(put("/api/campaign/{campaignId}/inactivate", campaignId))
                .andExpect(status().isOk());

        verify(campaignService).inactivateCampaign(campaignId);
    }

    @Test
    void testGetCampaignByFundraiserId() throws Exception {
        String fundraiserId = "fundraiser789";
        Campaign c1 = new Campaign("1", fundraiserId, "A", "ACTIVE", LocalDateTime.now(), 1000, "desc");
        Campaign c2 = new Campaign("2", fundraiserId, "B", "ACTIVE", LocalDateTime.now(), 2000, "desc");

        when(campaignService.getCampaignByFundraiserId(fundraiserId)).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/api/campaign/get-by-fundraiser/{fundraiserId}", fundraiserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].campaignId").value("1"));
    }

    @Test
    void testGetCampaignByCampaignId() throws Exception {
        Campaign campaign = new Campaign("c123", "f123", "Judul", "ACTIVE", LocalDateTime.now(), 3000, "desc");

        when(campaignService.getCampaignByCampaignId("c123")).thenReturn(campaign);

        mockMvc.perform(get("/api/campaign/get-by-id/{campaignId}", "c123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaignId").value("c123"));
    }

    @Test
    public void testUpdateCampaignJudul() throws Exception {
        Campaign campaign = new Campaign("123", "f1", "Updated", "ACTIVE", LocalDateTime.now(), 100, "desc");

        mockMvc.perform(put("/api/campaign/update-judul")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().isOk());

        Mockito.verify(campaignService).updateCampaignJudul("123", "Updated");
    }

    @Test
    public void testUpdateCampaignTarget() throws Exception {
        Campaign campaign = new Campaign("123", "f1", "Judul", "ACTIVE", LocalDateTime.now(), 50000, "desc");

        mockMvc.perform(put("/api/campaign/update-target")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().isOk());

        Mockito.verify(campaignService).updateCampaignTarget("123", 50000);
    }

    @Test
    public void testUpdateCampaignDeskripsi() throws Exception {
        Campaign campaign = new Campaign("123", "f1", "Judul", "ACTIVE", LocalDateTime.now(), 123, "Updated Desc");

        mockMvc.perform(put("/api/campaign/update-deskripsi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().isOk());

        Mockito.verify(campaignService).updateCampaignDeskripsi("123", "Updated Desc");
    }

    @Test
    public void testDeleteCampaign() throws Exception {
        mockMvc.perform(delete("/api/campaign/{campaignId}/delete", "123"))
                .andExpect(status().isOk());

        Mockito.verify(campaignService).deleteCampaign("123");
    }
}
