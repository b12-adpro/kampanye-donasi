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

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.service.DonationService;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;  // Change to MockBean
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DonationController.class)
class DonationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private DonationService donationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateDonatePage() throws Exception {
        String campaignId = "campaign123";

        mockMvc.perform(get("/donation/{campaignId}/donate", campaignId))
                .andExpect(status().isOk())
                .andExpect(view().name("DonationForm"))
                .andExpect(model().attributeExists("donation"));
    }

    @Test
    void testVerifyDonation() throws Exception {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now()
        );
        
        mockMvc.perform(post("/donation/{campaignId}/donate", donation.getCampaignId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        // simulate form binding; here you can pass parameters if needed
                        .flashAttr("donation", donation))
                .andExpect(status().isOk())
                .andExpect(view().name("VerifyDonation"));

        verify(donationService).createDonation(any(Donation.class));
    }

    @Test
    void testCreateDonation() throws Exception {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now()
        );

        mockMvc.perform(post("/donation/{donationId}/create-donation", donation.getDonationId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("donation", donation))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(donationService).createDonation(any(Donation.class));
    }

    @Test
    void testCompleteDonation() throws Exception {
        String donationId = "donation123";

        mockMvc.perform(put("/donation/{donationId}/complete", donationId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(donationService).completeDonation(donationId);
    }

    @Test
    void testCancelDonation() throws Exception {
        String donationId = "donation123";
        mockMvc.perform(put("/donation/{donationId}/cancel", donationId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(donationService).cancelDonation(donationId);
    }

    @Test
    void testGetDonationByDonationId() throws Exception {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now()
        );

        when(donationService.getDonationByDonationId(donation.getDonationId())).thenReturn(donation);

        mockMvc.perform(get("/donation/get-by-id/{donationId}", donation.getDonationId()))
                .andExpect(status().isOk())
                .andExpect(view().name("DonationDetail"))
                .andExpect(model().attributeExists("donation"));
    }

    @Test
    void testGetDonationByCampaignId() throws Exception {
        String campaignId = "campaign123";
        Donation donation1 = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            campaignId,
            123L,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now()
        );
        Donation donation2 = new Donation(
            "23652556-012a-4c07-b546-54eb1396d79b",
            campaignId,
            1L,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now()
        );
        List<Donation> donations = Arrays.asList(donation1, donation2);

        when(donationService.getDonationsByCampaignId(campaignId)).thenReturn(donations);

        mockMvc.perform(get("/donation/get-by-campaign/{campaignId}", campaignId))
                .andExpect(status().isOk())
                .andExpect(view().name("DonationList"))
                .andExpect(model().attributeExists("donations"));
    }

    @Test
    void testUpdateDonationMessage() throws Exception {
        String donationId = "donation456";
        String message = "Thank you for your support!";
        Donation donation = new Donation(donationId, "campaign789", 123L, 1000, DonationStatus.PENDING.getValue(), LocalDateTime.now());
        donation.setMessage(message);

        mockMvc.perform(put("/donation/update-message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donation)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/donation/get-by-id/" + donationId));

        verify(donationService).updateDonationMessage(donationId, message);
    }

    @Test
    void testDeleteDonationMessage() throws Exception {
        String donationId = "donation789";
        Donation donation = new Donation(donationId, "campaign123", 456L, 2000, DonationStatus.PENDING.getValue(), LocalDateTime.now());

        mockMvc.perform(put("/donation/delete-message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donation)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/donation/get-by-id/" + donationId));

        verify(donationService).deleteDonationMessage(donationId);
    }
}