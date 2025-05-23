package id.ac.ui.cs.advprog.tk_adpro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import id.ac.ui.cs.advprog.tk_adpro.service.CampaignService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.nullValue;

@WebMvcTest(CampaignController.class)
public class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampaignService campaignService;

    @Autowired
    private ObjectMapper objectMapper;

    private Campaign campaignWithBukti;
    private Campaign campaignWithoutBukti;
    private UUID campaignId;
    private UUID fundraiserId;
    private String buktiUrl;

    @BeforeEach
    void setUp() {
        campaignId = UUID.randomUUID();
        fundraiserId = UUID.randomUUID();
        buktiUrl = "http://localhost:8080/uploads/campaign_proofs/12345_bukti.jpg";
        LocalDateTime now = LocalDateTime.now();

        campaignWithBukti = new Campaign(
                campaignId, fundraiserId, "Judul Dengan Bukti",
                CampaignStatus.ACTIVE.getValue(),
                now, 100000, "Deskripsi A", buktiUrl);

        campaignWithoutBukti = new Campaign(
                UUID.randomUUID(), fundraiserId, "Judul Tanpa Bukti",
                CampaignStatus.INACTIVE.getValue(),
                now, 50000, "Deskripsi B", null);
    }

    @Test
    void testVerifyCampaign() throws Exception {
        when(campaignService.createCampaign(any(Campaign.class))).thenReturn(campaignWithBukti);

        mockMvc.perform(post("/api/campaign/{fundraiserId}/campaign", fundraiserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignWithBukti)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campaignId").value(campaignId.toString()))
                .andExpect(jsonPath("$.judul").value("Judul Dengan Bukti"));
    }

    @Test
    void testCreateCampaign_Json() throws Exception {
        when(campaignService.createCampaign(any(Campaign.class))).thenReturn(campaignWithoutBukti);

        mockMvc.perform(post("/api/campaign/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignWithoutBukti)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.judul").value("Judul Tanpa Bukti"));
    }

    @Test
    void testHandleFileUpload_Success() throws Exception {
        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "test-bukti.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "some-pdf-content".getBytes()
        );

        mockMvc.perform(multipart("/api/campaign/upload/bukti")
                        .file(filePart))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("/uploads/campaign_proofs/")));
    }

    @Test
    void testHandleFileUpload_EmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/campaign/upload/bukti")
                        .file(emptyFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testActivateCampaign() throws Exception {
        mockMvc.perform(put("/api/campaign/{campaignId}/activate", campaignId))
                .andExpect(status().isOk());
        verify(campaignService).activateCampaign(campaignId);
    }

    @Test
    void testInactivateCampaign() throws Exception {
        mockMvc.perform(put("/api/campaign/{campaignId}/inactivate", campaignId))
                .andExpect(status().isOk());
        verify(campaignService).inactivateCampaign(campaignId);
    }

    @Test
    void testGetCampaignByFundraiserId() throws Exception {
        List<Campaign> campaigns = Arrays.asList(campaignWithBukti, campaignWithoutBukti);
        when(campaignService.getCampaignByFundraiserId(fundraiserId)).thenReturn(campaigns);

        mockMvc.perform(get("/api/campaign/fundraiserId/{fundraiserId}", fundraiserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].campaignId").value(campaignId.toString()));
    }

    @Test
    void testGetCampaignByCampaignId_Found() throws Exception {
        when(campaignService.getCampaignByCampaignId(campaignId)).thenReturn(campaignWithBukti);

        mockMvc.perform(get("/api/campaign/campaignId/{campaignId}", campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaignId").value(campaignId.toString()));
    }

    @Test
    void testGetCampaignByCampaignId_NotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        when(campaignService.getCampaignByCampaignId(randomId)).thenReturn(null);

        mockMvc.perform(get("/api/campaign/campaignId/{campaignId}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBuktiPenggalanganDana_Found() throws Exception {
        when(campaignService.getBuktiPenggalanganDana(campaignId)).thenReturn(buktiUrl);

        mockMvc.perform(get("/api/campaign/{campaignId}/bukti", campaignId))
                .andExpect(status().isOk())
                .andExpect(content().string(buktiUrl));
    }

    @Test
    void testGetBuktiPenggalanganDana_NoProof() throws Exception {
        when(campaignService.getBuktiPenggalanganDana(campaignId)).thenReturn(null);

        mockMvc.perform(get("/api/campaign/{campaignId}/bukti", campaignId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBuktiPenggalanganDana_CampaignNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        when(campaignService.getBuktiPenggalanganDana(randomId)).thenThrow(new RuntimeException("Not Found"));

        mockMvc.perform(get("/api/campaign/{campaignId}/bukti", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCampaign() throws Exception {
        when(campaignService.updateCampaign(any(Campaign.class))).thenReturn(campaignWithBukti);

        mockMvc.perform(put("/api/campaign/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campaignWithBukti)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaignId").value(campaignId.toString()));
        verify(campaignService).updateCampaign(any(Campaign.class));
    }

    @Test
    void testDeleteCampaign() throws Exception {
        mockMvc.perform(delete("/api/campaign/{campaignId}/delete", campaignId))
                .andExpect(status().isOk());
        verify(campaignService).deleteCampaign(campaignId);
    }
}