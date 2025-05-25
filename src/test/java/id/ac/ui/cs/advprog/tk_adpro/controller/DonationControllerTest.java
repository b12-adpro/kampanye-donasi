package id.ac.ui.cs.advprog.tk_adpro.controller;

import id.ac.ui.cs.advprog.tk_adpro.dto.DonationDTO;
import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.service.DonationService;
import id.ac.ui.cs.advprog.tk_adpro.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationControllerTest {

    @Mock
    private DonationService donationService;

    @InjectMocks
    private DonationController donationController;

    private UUID donationId;
    private UUID campaignId;
    private UUID donaturId;
    private DonationDTO donationDTO;
    private Donation donation;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        donationId = UUID.randomUUID();
        campaignId = UUID.randomUUID();
        donaturId = UUID.randomUUID();
        testDateTime = LocalDateTime.now();

        donationDTO = new DonationDTO();
        donationDTO.setDonationId(donationId);
        donationDTO.setCampaignId(campaignId);
        donationDTO.setDonaturId(donaturId);
        donationDTO.setAmount(100000);
        donationDTO.setStatus(DonationStatus.PENDING.getValue());
        donationDTO.setDatetime(testDateTime);
        donationDTO.setMessage("Test donation message");

        donation = new Donation(
            donationId,
            campaignId,
            donaturId,
            100000,
            DonationStatus.PENDING.getValue(),
            testDateTime,
            "Test donation message"
        );
    }

    @Test
    void createDonation_Success() {
        // Given
        when(donationService.createDonation(any(Donation.class))).thenReturn(donation);

        // When
        ResponseEntity<Object> response = donationController.createDonation(donationDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(donation, response.getBody());
        verify(donationService, times(1)).createDonation(any(Donation.class));
    }

    @Test
    void createDonation_WithNullDateTime_Success() {
        // Given
        donationDTO.setDatetime(null);
        when(donationService.createDonation(any(Donation.class))).thenReturn(donation);

        // When
        ResponseEntity<Object> response = donationController.createDonation(donationDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(donation, response.getBody());
        verify(donationService, times(1)).createDonation(any(Donation.class));
    }

    @Test
    void createDonation_InsufficientBalanceException() {
        // Given
        String errorMessage = "Insufficient balance";
        when(donationService.createDonation(any(Donation.class)))
                .thenThrow(new InsufficientBalanceException(errorMessage));

        // When
        ResponseEntity<Object> response = donationController.createDonation(donationDTO);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("error"));
    }

    @Test
    void createDonation_GenericException() {
        // Given
        String errorMessage = "Database connection failed";
        when(donationService.createDonation(any(Donation.class)))
                .thenThrow(new RuntimeException(errorMessage));

        // When
        ResponseEntity<Object> response = donationController.createDonation(donationDTO);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("Failed to create donation: " + errorMessage, errorResponse.get("error"));
    }

    @Test
    void cancelDonation_Success() {
        // Given
        Donation pendingDonation = new Donation(
                donationId, campaignId, donaturId, 100000,
                DonationStatus.PENDING.getValue(), testDateTime, "Test message"
        );
        Donation cancelledDonation = new Donation(
                donationId, campaignId, donaturId, 100000,
                DonationStatus.CANCELED.getValue(), testDateTime, "Test message"
        );

        when(donationService.getDonationByDonationId(donationId)).thenReturn(pendingDonation);
        when(donationService.cancelDonation(donationId)).thenReturn(cancelledDonation);

        // When
        ResponseEntity<Object> response = donationController.cancelDonation(donationId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cancelledDonation, response.getBody());
        verify(donationService, times(1)).getDonationByDonationId(donationId);
        verify(donationService, times(1)).cancelDonation(donationId);
    }

    @Test
    void cancelDonation_NotPendingStatus() {
        // Given
        Donation completedDonation = new Donation(
                donationId, campaignId, donaturId, 100000,
                DonationStatus.COMPLETED.getValue(), testDateTime, "Test message"
        );

        when(donationService.getDonationByDonationId(donationId)).thenReturn(completedDonation);

        // When
        ResponseEntity<Object> response = donationController.cancelDonation(donationId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("The current status of Donation can't be canceled!", errorResponse.get("error"));
        verify(donationService, times(1)).getDonationByDonationId(donationId);
        verify(donationService, never()).cancelDonation(donationId);
    }

    @Test
    void cancelDonation_InsufficientBalanceException() {
        // Given
        Donation pendingDonation = new Donation(
                donationId, campaignId, donaturId, 100000,
                DonationStatus.PENDING.getValue(), testDateTime, "Test message"
        );
        String errorMessage = "Insufficient balance for cancellation";

        when(donationService.getDonationByDonationId(donationId)).thenReturn(pendingDonation);
        when(donationService.cancelDonation(donationId))
                .thenThrow(new InsufficientBalanceException(errorMessage));

        // When
        ResponseEntity<Object> response = donationController.cancelDonation(donationId);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("error"));
    }

    @Test
    void cancelDonation_GenericException() {
        // Given
        String errorMessage = "Database error";
        when(donationService.getDonationByDonationId(donationId))
                .thenThrow(new RuntimeException(errorMessage));

        // When
        ResponseEntity<Object> response = donationController.cancelDonation(donationId);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("Failed to cancel Donation: " + errorMessage, errorResponse.get("error"));
    }

    @Test
    void getDonation_Success() {
        // Given
        when(donationService.getDonationByDonationId(donationId)).thenReturn(donation);

        // When
        ResponseEntity<Object> response = donationController.getDonation(donationId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(donation, response.getBody());
        verify(donationService, times(1)).getDonationByDonationId(donationId);
    }

    @Test
    void getDonation_NotFound() {
        // Given
        when(donationService.getDonationByDonationId(donationId)).thenReturn(null);

        // When
        ResponseEntity<Object> response = donationController.getDonation(donationId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService, times(1)).getDonationByDonationId(donationId);
    }

    @Test
    void getDonation_Exception() {
        // Given
        String errorMessage = "Database connection failed";
        when(donationService.getDonationByDonationId(donationId))
                .thenThrow(new RuntimeException(errorMessage));

        // When
        ResponseEntity<Object> response = donationController.getDonation(donationId);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("Failed to retrieve donation: " + errorMessage, errorResponse.get("error"));
    }

    @Test
    void getDonationsByDonatur_Success() {
        // Given
        List<Donation> donations = Arrays.asList(donation);
        when(donationService.getDonationsByDonaturId(donaturId)).thenReturn(donations);

        // When
        ResponseEntity<Object> response = donationController.getDonationsByDonatur(donaturId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(donations, response.getBody());
        verify(donationService, times(1)).getDonationsByDonaturId(donaturId);
    }

    @Test
    void getDonationsByDonatur_EmptyList() {
        // Given
        List<Donation> emptyList = Collections.emptyList();
        when(donationService.getDonationsByDonaturId(donaturId)).thenReturn(emptyList);

        // When
        ResponseEntity<Object> response = donationController.getDonationsByDonatur(donaturId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyList, response.getBody());
        verify(donationService, times(1)).getDonationsByDonaturId(donaturId);
    }

    @Test
    void getDonationsByDonatur_Exception() {
        // Given
        String errorMessage = "Service unavailable";
        when(donationService.getDonationsByDonaturId(donaturId))
                .thenThrow(new RuntimeException(errorMessage));

        // When
        ResponseEntity<Object> response = donationController.getDonationsByDonatur(donaturId);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("Failed to retrieve donations: " + errorMessage, errorResponse.get("error"));
    }

    @Test
    void getDonationsByCampaign_Success() {
        // Given
        List<Donation> donations = Arrays.asList(donation);
        when(donationService.getDonationsByCampaignId(campaignId)).thenReturn(donations);

        // When
        ResponseEntity<Object> response = donationController.getDonationsByCampaign(campaignId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(donations, response.getBody());
        verify(donationService, times(1)).getDonationsByCampaignId(campaignId);
    }

    @Test
    void getDonationsByCampaign_EmptyList() {
        // Given
        List<Donation> emptyList = Collections.emptyList();
        when(donationService.getDonationsByCampaignId(campaignId)).thenReturn(emptyList);

        // When
        ResponseEntity<Object> response = donationController.getDonationsByCampaign(campaignId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyList, response.getBody());
        verify(donationService, times(1)).getDonationsByCampaignId(campaignId);
    }

    @Test
    void getDonationsByCampaign_Exception() {
        // Given
        String errorMessage = "Network timeout";
        when(donationService.getDonationsByCampaignId(campaignId))
                .thenThrow(new RuntimeException(errorMessage));

        // When
        ResponseEntity<Object> response = donationController.getDonationsByCampaign(campaignId);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("Failed to retrieve donations: " + errorMessage, errorResponse.get("error"));
    }

    @Test
    void updateDonationMessage_Success() {
        // Given
        String newMessage = "Updated message";
        Donation updatedDonation = new Donation(
                donationId, campaignId, donaturId, 100000,
                DonationStatus.PENDING.getValue(), testDateTime, newMessage
        );
        when(donationService.updateDonationMessage(donationId, newMessage)).thenReturn(updatedDonation);

        // When
        ResponseEntity<Object> response = donationController.updateDonationMessage(donationId, newMessage);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDonation, response.getBody());
        verify(donationService, times(1)).updateDonationMessage(donationId, newMessage);
    }

    @Test
    void updateDonationMessage_NotFound() {
        // Given
        String newMessage = "Updated message";
        when(donationService.updateDonationMessage(donationId, newMessage)).thenReturn(null);

        // When
        ResponseEntity<Object> response = donationController.updateDonationMessage(donationId, newMessage);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService, times(1)).updateDonationMessage(donationId, newMessage);
    }

    @Test
    void updateDonationMessage_Exception() {
        // Given
        String newMessage = "Updated message";
        String errorMessage = "Update failed";
        when(donationService.updateDonationMessage(donationId, newMessage))
                .thenThrow(new RuntimeException(errorMessage));

        // When
        ResponseEntity<Object> response = donationController.updateDonationMessage(donationId, newMessage);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("Failed to update message: " + errorMessage, errorResponse.get("error"));
    }

    @Test
    void badRequest_ReturnsCorrectFormat() {
        // This test is to ensure the private method works correctly
        // We can test it indirectly through other methods
        when(donationService.createDonation(any(Donation.class)))
                .thenThrow(new InsufficientBalanceException("Test error"));

        ResponseEntity<Object> response = donationController.createDonation(donationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertTrue(errorResponse.containsKey("error"));
        assertEquals("Test error", errorResponse.get("error"));
    }

    @Test
    void serverError_ReturnsCorrectFormat() {
        // This test is to ensure the private method works correctly
        // We can test it indirectly through other methods
        when(donationService.createDonation(any(Donation.class)))
                .thenThrow(new RuntimeException("Test server error"));

        ResponseEntity<Object> response = donationController.createDonation(donationDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertTrue(errorResponse.containsKey("error"));
        assertEquals("Failed to create donation: Test server error", errorResponse.get("error"));
    }
}