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
import java.util.UUID;

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
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign campaign = new Campaign(
                uuidCampaign, uuidFundraiser, "Judul",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(), 100000, "Deskripsi");

        when(campaignService.createCampaign(any(Campaign.class))).thenReturn(campaign);

        mockMvc.perform(post("/api/campaign/{fundraiserId}/campaign", campaign.getFundraiserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campaignId").value(uuidCampaign.toString()))
                .andExpect(jsonPath("$.judul").value("Judul"));

        verify(campaignService).createCampaign(any(Campaign.class));
    }

    @Test
    void testCreateCampaign() throws Exception {
        UUID uuidCampaign = UUID.randomUUID();
        UUID uuidFundraiser = UUID.randomUUID();
        Campaign campaign = new Campaign(
                uuidCampaign, uuidFundraiser, "Judul Baru",
                CampaignStatus.ACTIVE.getValue(),
                LocalDateTime.now(), 50000, "Deskripsi");

        when(campaignService.createCampaign(any(Campaign.class))).thenReturn(campaign);

        mockMvc.perform(post("/api/campaign/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campaignId").value(uuidCampaign.toString()))
                .andExpect(jsonPath("$.judul").value("Judul Baru"));

        verify(campaignService).createCampaign(any(Campaign.class));
    }

    @Test
    void testActivateCampaign() throws Exception {
        UUID campaignId = UUID.randomUUID();

        mockMvc.perform(put("/api/campaign/{campaignId}/activate", campaignId))
                .andExpect(status().isOk());

        verify(campaignService).activateCampaign(campaignId);
    }

    @Test
    void testInactivateCampaign() throws Exception {
        UUID campaignId = UUID.randomUUID();

        mockMvc.perform(put("/api/campaign/{campaignId}/inactivate", campaignId))
                .andExpect(status().isOk());

        verify(campaignService).inactivateCampaign(campaignId);
    }

    @Test
    void testGetCampaignByFundraiserId() throws Exception {
        UUID campaignId = UUID.randomUUID();
        UUID fundraiserId = UUID.randomUUID();
        Campaign c1 = new Campaign(campaignId, fundraiserId, "A", "ACTIVE", LocalDateTime.now(), 1000, "desc");
        Campaign c2 = new Campaign(campaignId, fundraiserId, "B", "ACTIVE", LocalDateTime.now(), 2000, "desc");

        when(campaignService.getCampaignByFundraiserId(fundraiserId)).thenReturn(Arrays.asList(c1, c2));

        mockMvc.perform(get("/api/campaign/fundraiserId/{fundraiserId}", fundraiserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].campaignId").value(campaignId.toString()));
    }

    @Test
    void testGetCampaignByCampaignId() throws Exception {
        UUID campaignId = UUID.randomUUID();
        UUID fundraiserId = UUID.randomUUID();
        Campaign campaign = new Campaign(campaignId, fundraiserId, "Judul", "ACTIVE", LocalDateTime.now(), 3000, "desc");

        when(campaignService.getCampaignByCampaignId(campaignId)).thenReturn(campaign);

        mockMvc.perform(get("/api/campaign/campaignId/{campaignId}", campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaignId").value(campaignId.toString()));
    }

    @Test
    public void testUpdateCampaign() throws Exception {
        UUID campaignId = UUID.randomUUID();
        UUID fundraiserId = UUID.randomUUID();
        Campaign campaign = new Campaign(campaignId, fundraiserId, "Updated", "ACTIVE", LocalDateTime.now(), 100, "desc");
        mockMvc.perform(put("/api/campaign/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaign)))
                .andExpect(status().isOk());

        Mockito.verify(campaignService).updateCampaign(Mockito.argThat(updated ->
            updated.getJudul().equals("Updated") &&
            updated.getTarget() == 100 &&
                    updated.getDeskripsi().equals("desc")
        ));
    }

    @Test
    public void testDeleteCampaign() throws Exception {
        UUID campaignId = UUID.randomUUID();
        mockMvc.perform(delete("/api/campaign/{campaignId}/delete", campaignId))
                .andExpect(status().isOk());

        Mockito.verify(campaignService).deleteCampaign(campaignId);
    }
}
