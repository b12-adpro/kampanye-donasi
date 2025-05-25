package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.repository.DonationRepository;
import id.ac.ui.cs.advprog.tk_adpro.state.DonationStatusState;
import id.ac.ui.cs.advprog.tk_adpro.state.DonationStatusStateFactory;
import id.ac.ui.cs.advprog.tk_adpro.strategy.PaymentServiceApiStrategy;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceImplTest {
    @Mock
    private DonationRepository donationRepository;

    @Mock
    private PaymentServiceApiStrategy paymentStrategy;

    @InjectMocks
    private DonationServiceImpl donationService;

    @Test
    void testCheckBalance_SufficientBalance() {
        UUID donaturId = UUID.randomUUID();
        Donation donation = new Donation(UUID.randomUUID(), UUID.randomUUID(), donaturId, 169500, DonationStatus.COMPLETED.getValue(), LocalDateTime.now(), "Get well soon!");
        when(paymentStrategy.checkBalance(donaturId)).thenReturn(169500.0);

        Donation result = donationService.checkBalance(donation);
        assertEquals(donation, result);
    }

    @Test
    void testCheckBalance_InsufficientBalance() {
        UUID donaturId = UUID.randomUUID();
        Donation donation = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            donaturId,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        when(paymentStrategy.checkBalance(donaturId)).thenReturn(169499.0);

        assertThrows(InsufficientBalanceException.class, () -> donationService.checkBalance(donation));
    }

    @Test
    void testCreateDonation_PaymentSuccess() {
        UUID donationId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();
        UUID donaturId = UUID.randomUUID();
        Donation donation = new Donation(
            donationId,
            campaignId,
            donaturId,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        when(paymentStrategy.checkBalance(donaturId)).thenReturn(200000.0);
        CompletableFuture<Void> completedFuture = CompletableFuture.completedFuture(null);
        when(paymentStrategy.processPayment(donationId, campaignId, donaturId, 169500)).thenReturn(completedFuture);
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation result = donationService.createDonation(donation);
        verify(paymentStrategy).checkBalance(donaturId);
        verify(paymentStrategy).processPayment(donationId, campaignId, donaturId, 169500);
        verify(donationRepository).save(donation);
        assertEquals(donation, result);
    }

    @Test
    void testCreateDonation_PaymentFailure() {
        UUID donaturId = UUID.randomUUID();
        Donation donation = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            donaturId,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

        DonationStatusState mockState = mock(DonationStatusState.class);
        doAnswer(invocation -> {
            donation.setStatus(DonationStatus.PENDING.getValue());
            return null;
        }).when(mockState).pending(any(Donation.class));

        try (MockedStatic<DonationStatusStateFactory> factoryMock = mockStatic(DonationStatusStateFactory.class)) {
            factoryMock.when(() -> DonationStatusStateFactory.getState(donation)).thenReturn(mockState);
            when(paymentStrategy.checkBalance(donaturId)).thenThrow(new InsufficientBalanceException("Not enough balance"));
            when(donationRepository.save(donation)).thenReturn(donation);

            Donation result = donationService.createDonation(donation);
            verify(paymentStrategy).checkBalance(donaturId);
            verify(mockState).pending(donation);
            verify(donationRepository).save(donation);
            assertEquals(DonationStatus.PENDING.getValue(), result.getStatus());
        }
    }

    @Test
    void testCreateDonation_NotCompletedStatus() {
        Donation donation = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation result = donationService.createDonation(donation);
        verify(donationRepository).save(donation);
        verify(paymentStrategy, never()).checkBalance(any());
        verify(paymentStrategy, never()).processPayment(any(), any(), any(), anyInt());
        assertEquals(donation, result);
    }

    @Test
    void testCancelDonation() {
        UUID donationId = UUID.randomUUID();
        Donation donation = new Donation(
            donationId,
            UUID.randomUUID(),
            UUID.randomUUID(),
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        DonationStatusState mockState = mock(DonationStatusState.class);
        doAnswer(invocation -> {
            donation.setStatus(DonationStatus.CANCELED.getValue());
            return null;
        }).when(mockState).cancel(any(Donation.class));

        try (MockedStatic<DonationStatusStateFactory> factoryMock = mockStatic(DonationStatusStateFactory.class)) {
            when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));
            factoryMock.when(() -> DonationStatusStateFactory.getState(donation)).thenReturn(mockState);
            when(donationRepository.save(donation)).thenReturn(donation);

            Donation result = donationService.cancelDonation(donationId);

            verify(mockState).cancel(donation);
            verify(donationRepository).save(donation);
            assertEquals(DonationStatus.CANCELED.getValue(), result.getStatus());
        }
    }

    @Test
    void testCancelDonation_DonationNotFound() {
        UUID donationId = UUID.randomUUID();
        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> donationService.cancelDonation(donationId));
    }

    @Test
    void testGetDonationByDonationId() {
        UUID donationId = UUID.randomUUID();
        Donation donation = new Donation(
            donationId,
            UUID.randomUUID(),
            UUID.randomUUID(),
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));

        Donation result = donationService.getDonationByDonationId(donationId);
        assertEquals(donation, result);
    }

    @Test
    void testGetDonationByDonationId_NotFound() {
        UUID donationId = UUID.randomUUID();
        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        Donation result = donationService.getDonationByDonationId(donationId);
        assertNull(result);
    }

    @Test
    void testGetDonationsByDonaturId() {
        UUID donaturId = UUID.randomUUID();
        Donation donation1 = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            donaturId,
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        Donation donation2 = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            donaturId,
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        List<Donation> donations = Arrays.asList(donation1, donation2);
        when(donationRepository.findByDonaturId(donaturId)).thenReturn(donations);

        List<Donation> result = donationService.getDonationsByDonaturId(donaturId);
        assertEquals(donations, result);
    }

    @Test
    void testGetDonationsByCampaignId() {
        UUID campaignId = UUID.randomUUID();
        Donation donation1 = new Donation(
            UUID.randomUUID(),
            campaignId,
            UUID.randomUUID(),
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        Donation donation2 = new Donation(
            UUID.randomUUID(),
            campaignId,
            UUID.randomUUID(),
            169500,
            DonationStatus.CANCELED.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        List<Donation> donations = Arrays.asList(donation1, donation2);
        when(donationRepository.findByCampaignId(campaignId)).thenReturn(donations);

        List<Donation> result = donationService.getDonationsByCampaignId(campaignId);
        assertEquals(donations, result);
    }

    @Test
    void testUpdateDonationMessage() {
        UUID donationId = UUID.randomUUID();
        Donation donation = new Donation(
            donationId,
            UUID.randomUUID(),
            UUID.randomUUID(),
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation result = donationService.updateDonationMessage(donationId, "New Message");
        assertEquals("New Message", result.getMessage());
    }

    @Test
    void testUpdateDonationMessage_DonationNotFound() {
        UUID id = UUID.randomUUID();
        when(donationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> donationService.updateDonationMessage(id, "new message"));
    }
}