package id.ac.ui.cs.advprog.tk_adpro.controller;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.service.DonationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DonationControllerTest {
    @Mock
    private DonationService donationService;

    @InjectMocks
    private DonationController donationController;

    private Donation testDonation;
    private static final UUID DONATION_ID = UUID.randomUUID();
    private static final UUID CAMPAIGN_ID = UUID.randomUUID();
    private static final UUID DONATUR_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testDonation = Donation.builder()
            .donationId(DONATION_ID)
            .campaignId(CAMPAIGN_ID)
            .donaturId(DONATUR_ID)
            .amount(1000)
            .status(DonationStatus.PENDING.getValue())
            .datetime(LocalDateTime.now())
            .message("Test donation")
            .build();
    }

    @Test
    void testCreateDonationSuccess() {
        when(donationService.checkBalance(any(Donation.class))).thenReturn(testDonation);
        when(donationService.createDonation(any(Donation.class))).thenReturn(testDonation);

        ResponseEntity<Object> response = donationController.createDonation(CAMPAIGN_ID, testDonation);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testDonation, response.getBody());
        verify(donationService).checkBalance(any(Donation.class));
        verify(donationService).createDonation(any(Donation.class));
    }

    @Test
    void restCreateDonationWithNullDateTimeShouldSetCurrentTime() {
        testDonation.setDatetime(null);
        when(donationService.checkBalance(any(Donation.class))).thenReturn(testDonation);
        when(donationService.createDonation(any(Donation.class))).thenReturn(testDonation);

        ResponseEntity<Object> response = donationController.createDonation(CAMPAIGN_ID, testDonation);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(((Donation) response.getBody()).getDatetime());
        verify(donationService).checkBalance(any(Donation.class));
        verify(donationService).createDonation(any(Donation.class));
    }

    @Test
    void testCreateDonationWithInsufficientBalance() {
        when(donationService.checkBalance(any(Donation.class))).thenThrow(new InsufficientBalanceException("Not enough balance!"));

        ResponseEntity<Object> response = donationController.createDonation(CAMPAIGN_ID, testDonation);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> errorResponse = (Map<?, ?>) response.getBody();
        assertEquals("Not enough balance!", errorResponse.get("error"));
        verify(donationService).checkBalance(any(Donation.class));
        verify(donationService, never()).createDonation(any(Donation.class));
    }

    @Test
    void testCreateDonationGenericException() {
        when(donationService.checkBalance(any(Donation.class))).thenThrow(new RuntimeException("Generic error"));

        ResponseEntity<Object> response = donationController.createDonation(CAMPAIGN_ID, testDonation);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> errorResponse = (Map<?, ?>) response.getBody();
        assertEquals("Failed to create donation: Generic error", errorResponse.get("error"));
    }

    @Test
    void testUpdateDonationStatusCompletedSuccess() {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", DonationStatus.COMPLETED.getValue());
        when(donationService.completeDonation(DONATION_ID)).thenReturn(testDonation);

        ResponseEntity<Object> response = donationController.updateDonationStatus(DONATION_ID, statusUpdate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDonation, response.getBody());
        verify(donationService).completeDonation(DONATION_ID);
        verify(donationService, never()).cancelDonation(any(UUID.class));
    }

    @Test
    void testUpdateDonationStatusCanceledSuccess() {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", DonationStatus.CANCELED.getValue());
        when(donationService.cancelDonation(DONATION_ID)).thenReturn(testDonation);

        ResponseEntity<Object> response = donationController.updateDonationStatus(DONATION_ID, statusUpdate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDonation, response.getBody());
        verify(donationService, never()).completeDonation(any(UUID.class));
        verify(donationService).cancelDonation(DONATION_ID);
    }

    @Test
    void updateDonationStatusInvalidStatus() {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "INVALID_STATUS");

        ResponseEntity<Object> response = donationController.updateDonationStatus(DONATION_ID, statusUpdate);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> errorResponse = (Map<?, ?>) response.getBody();
        assertEquals("Invalid status value. Accepted values: COMPLETED, CANCELED", errorResponse.get("error"));
        verify(donationService, never()).completeDonation(any(UUID.class));
        verify(donationService, never()).cancelDonation(any(UUID.class));
    }

    @Test
    void updateDonationStatusInsufficientBalance() {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", DonationStatus.COMPLETED.getValue());
        when(donationService.completeDonation(DONATION_ID)).thenThrow(new InsufficientBalanceException("Not enough balance!"));

        ResponseEntity<Object> response = donationController.updateDonationStatus(DONATION_ID, statusUpdate);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> errorResponse = (Map<?, ?>) response.getBody();
        assertEquals("Not enough balance!", errorResponse.get("error"));
    }

    @Test
    void testUpdateDonationStatusGenericException() {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", DonationStatus.COMPLETED.getValue());
        when(donationService.completeDonation(DONATION_ID)).thenThrow(new RuntimeException("Generic error"));

        ResponseEntity<Object> response = donationController.updateDonationStatus(DONATION_ID, statusUpdate);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> errorResponse = (Map<?, ?>) response.getBody();
        assertEquals("Failed to update donation status: Generic error", errorResponse.get("error"));
    }

    @Test
    void testGetDonationSuccess() {
        when(donationService.getDonationByDonationId(DONATION_ID)).thenReturn(testDonation);

        ResponseEntity<Object> response = donationController.getDonation(DONATION_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDonation, response.getBody());
        verify(donationService).getDonationByDonationId(DONATION_ID);
    }

    @Test
    void testGetDonationNotFound() {
        when(donationService.getDonationByDonationId(DONATION_ID)).thenReturn(null);

        ResponseEntity<Object> response = donationController.getDonation(DONATION_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService).getDonationByDonationId(DONATION_ID);
    }

    @Test
    void testGetDonationException() {
        when(donationService.getDonationByDonationId(DONATION_ID)).thenThrow(new RuntimeException("Generic error"));

        ResponseEntity<Object> response = donationController.getDonation(DONATION_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> errorResponse = (Map<?, ?>) response.getBody();
        assertEquals("Failed to retrieve donation: Generic error", errorResponse.get("error"));
    }

    @Test
    void testGetDonationsByDonaturSuccess() {
        List<Donation> donations = Collections.singletonList(testDonation);
        when(donationService.getDonationsByDonaturId(DONATUR_ID)).thenReturn(donations);

        ResponseEntity<Object> response = donationController.getDonationsByDonatur(DONATUR_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(donations, response.getBody());
        verify(donationService).getDonationsByDonaturId(DONATUR_ID);
    }

    @Test
    void testGetDonationsByDonaturException() {
        when(donationService.getDonationsByDonaturId(DONATUR_ID)).thenThrow(new RuntimeException("Generic error"));

        ResponseEntity<Object> response = donationController.getDonationsByDonatur(DONATUR_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> errorResponse = (Map<?, ?>) response.getBody();
        assertEquals("Failed to retrieve donations: Generic error", errorResponse.get("error"));
    }

    @Test
    void testGetDonationsByCampaignSuccess() {
        List<Donation> donations = Collections.singletonList(testDonation);
        when(donationService.getDonationsByCampaignId(CAMPAIGN_ID)).thenReturn(donations);

        ResponseEntity<Object> response = donationController.getDonationsByCampaign(CAMPAIGN_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(donations, response.getBody());
        verify(donationService).getDonationsByCampaignId(CAMPAIGN_ID);
    }

    @Test
    void testGetDonationsByCampaignException() {
        when(donationService.getDonationsByCampaignId(CAMPAIGN_ID)).thenThrow(new RuntimeException("Generic error"));

        ResponseEntity<Object> response = donationController.getDonationsByCampaign(CAMPAIGN_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> errorResponse = (Map<?, ?>) response.getBody();
        assertEquals("Failed to retrieve donations: Generic error", errorResponse.get("error"));
    }

    @Test
    void testUpdateDonationMessageSuccess() {
        String newMessage = "Updated message";
        Map<String, String> payload = new HashMap<>();
        payload.put("message", newMessage);
        when(donationService.updateDonationMessage(DONATION_ID, newMessage)).thenReturn(testDonation);

        ResponseEntity<Object> response = donationController.updateDonationMessage(DONATION_ID, payload);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDonation, response.getBody());
        verify(donationService).updateDonationMessage(DONATION_ID, newMessage);
    }

    @Test
    void testUpdateDonationMessageNotFound() {
        String newMessage = "Updated message";
        Map<String, String> payload = new HashMap<>();
        payload.put("message", newMessage);
        when(donationService.updateDonationMessage(DONATION_ID, newMessage)).thenReturn(null);

        ResponseEntity<Object> response = donationController.updateDonationMessage(DONATION_ID, payload);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService).updateDonationMessage(DONATION_ID, newMessage);
    }

    @Test
    void testUpdateDonationMessageException() {
        String newMessage = "Updated message";
        Map<String, String> payload = new HashMap<>();
        payload.put("message", newMessage);
        when(donationService.updateDonationMessage(DONATION_ID, newMessage))
            .thenThrow(new RuntimeException("Generic error"));

        ResponseEntity<Object> response = donationController.updateDonationMessage(DONATION_ID, payload);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> errorResponse = (Map<?, ?>) response.getBody();
        assertEquals("Failed to update message: Generic error", errorResponse.get("error"));
    }

    @Test
    void testDeleteDonationMessageSuccess() {
        when(donationService.deleteDonationMessage(DONATION_ID)).thenReturn(testDonation);

        ResponseEntity<Object> response = donationController.deleteDonationMessage(DONATION_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testDonation, response.getBody());
        verify(donationService).deleteDonationMessage(DONATION_ID);
    }

    @Test
    void testDeleteDonationMessageNotFound() {
        when(donationService.deleteDonationMessage(DONATION_ID)).thenReturn(null);

        ResponseEntity<Object> response = donationController.deleteDonationMessage(DONATION_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService).deleteDonationMessage(DONATION_ID);
    }

    @Test
    void testDeleteDonationMessageException() {
        when(donationService.deleteDonationMessage(DONATION_ID)).thenThrow(new RuntimeException("Generic error"));

        ResponseEntity<Object> response = donationController.deleteDonationMessage(DONATION_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> errorResponse = (Map<?, ?>) response.getBody();
        assertEquals("Failed to delete message: Generic error", errorResponse.get("error"));
    }
}