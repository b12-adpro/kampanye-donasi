package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.repository.DonationRepository;
import id.ac.ui.cs.advprog.tk_adpro.state.DonationStatusState;
import id.ac.ui.cs.advprog.tk_adpro.state.DonationStatusStateFactory;
import id.ac.ui.cs.advprog.tk_adpro.strategy.PaymentStrategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceImplTest {
    @Mock
    private DonationRepository donationRepository;

    @Mock
    private PaymentStrategy paymentStrategy;

    @InjectMocks
    private DonationServiceImpl donationService;

    @Test
    void testCheckBalance_SufficientBalance() {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

        when(paymentStrategy.checkBalance(donation.getDonaturId())).thenReturn(169500);

        Donation result = donationService.checkBalance(donation);
        assertEquals(donation, result);
    }

    @Test
    void testCheckBalance_InsufficientBalance() {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

        when(paymentStrategy.checkBalance(donation.getDonaturId())).thenReturn(169499);

        assertThrows(InsufficientBalanceException.class, () -> donationService.checkBalance(donation));
    }

    @Test
    void testCreateDonation_PaymentSuccess() {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

        when(paymentStrategy.processPayment(donation.getDonaturId(), donation.getAmount())).thenReturn(true);
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation result = donationService.createDonation(donation);
        verify(paymentStrategy, times(1)).processPayment(donation.getDonaturId(), donation.getAmount());
        verify(donationRepository, times(1)).save(donation);
        assertEquals(donation, result);
    }

    @Test
    void testCreateDonation_PaymentFailure() {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

        when(paymentStrategy.processPayment(donation.getDonaturId(), donation.getAmount())).thenReturn(false);

        assertThrows(InsufficientBalanceException.class, () -> donationService.createDonation(donation));
    }

    @Test
    void testCreateDonation_NotCompletedStatus() {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

        when(donationRepository.save(donation)).thenReturn(donation);

        Donation result = donationService.createDonation(donation);
        verify(donationRepository, times(1)).save(donation);
        assertEquals(donation, result);
    }

    @Test
    void testCompleteDonation() {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

        DonationStatusState mockState = mock(DonationStatusState.class);
        doAnswer(invocation -> {
            donation.setStatus(DonationStatus.COMPLETED.getValue());
            return null;
        }).when(mockState).complete(any(Donation.class));

        try (MockedStatic<DonationStatusStateFactory> factoryMock = mockStatic(DonationStatusStateFactory.class)) {
            when(donationRepository.findByDonationId(donation.getDonationId())).thenReturn(donation);
    
            factoryMock.when(() -> DonationStatusStateFactory.getState(donation)).thenReturn(mockState);

            when(paymentStrategy.processPayment(donation.getDonaturId(), donation.getAmount())).thenReturn(true);
            when(donationRepository.save(donation)).thenReturn(donation);

            donationService.completeDonation(donation.getDonationId());

            verify(mockState, times(1)).complete(donation);
            verify(paymentStrategy, times(1)).processPayment(donation.getDonaturId(), donation.getAmount());
            verify(donationRepository, times(1)).save(donation);
            assertEquals(DonationStatus.COMPLETED.getValue(), donation.getStatus());
        }
    }

    @Test
    void testCancelDonation() {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

        DonationStatusState mockState = mock(DonationStatusState.class);
        doAnswer(invocation -> {
            donation.setStatus(DonationStatus.CANCELLED.getValue());
            return null;
        }).when(mockState).cancel(any(Donation.class));

        try (MockedStatic<DonationStatusStateFactory> factoryMock = mockStatic(DonationStatusStateFactory.class)) {
            when(donationRepository.findByDonationId(donation.getDonationId())).thenReturn(donation);

            factoryMock.when(() -> DonationStatusStateFactory.getState(donation)).thenReturn(mockState);

            when(donationRepository.save(donation)).thenReturn(donation);

            donationService.cancelDonation(donation.getDonationId());

            verify(mockState, times(1)).cancel(donation);
            verify(donationRepository, times(1)).save(donation);
            assertEquals(DonationStatus.CANCELLED.getValue(), donation.getStatus());
        }
    }

    @Test
    void testGetDonationByDonationId() {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        when(donationRepository.findByDonationId(donation.getDonationId())).thenReturn(donation);

        Donation result = donationService.getDonationByDonationId(donation.getDonationId());
        assertEquals(donation, result);
    }

    @Test
    void testGetDonationsByDonaturId() {
        long donaturId = 123L;
        Donation donation1 = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            donaturId,
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        Donation donation2 = new Donation(
            "13652556-012a-4c07-b546-54fb1396d79b",
            "eb558e9f-1c39-460e-8860-71zf6af63bd6",
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
        String campaignId = "eb558e9f-1c39-460e-8860-71af6af63bd6";
        Donation donation1 = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            campaignId,
            123L,
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        Donation donation2 = new Donation(
            "13652556-012a-4c07-b546-54fb1396d79b",
            campaignId,
            456L,
            169500,
            DonationStatus.PENDING.getValue(),
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
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

        when(donationRepository.findByDonationId(donation.getDonationId())).thenReturn(donation);
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation result = donationService.updateDonationMessage(donation.getDonationId(), "Simpansini Bananini");
        assertEquals("Simpansini Bananini", result.getMessage());
    }

    @Test
    void testUpdateDonationMessage_DonationNotFound() {
        when(donationRepository.findByDonationId("13652556-012a-4c07-b546-54eb1396d79b")).thenReturn(null);

        Donation result = donationService.updateDonationMessage("13652556-012a-4c07-b546-54eb1396d79b", "new message");
        assertNull(result);
    }

    @Test
    void testDeleteDonationMessage() {
        Donation donation = new Donation(
            "13652556-012a-4c07-b546-54eb1396d79b",
            "eb558e9f-1c39-460e-8860-71af6af63bd6",
            123L,
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

        when(donationRepository.findByDonationId(donation.getDonationId())).thenReturn(donation);
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation result = donationService.deleteDonationMessage(donation.getDonationId());
        assertNull(result.getMessage());
    }

    @Test
    void testDeleteDonationMessage_DonationNotFound() {
        when(donationRepository.findByDonationId("don1")).thenReturn(null);

        Donation result = donationService.deleteDonationMessage("don1");
        assertNull(result);
    }
}