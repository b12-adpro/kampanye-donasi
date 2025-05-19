package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.repository.DonationRepository;
import id.ac.ui.cs.advprog.tk_adpro.state.DonationStatusState;
import id.ac.ui.cs.advprog.tk_adpro.state.DonationStatusStateFactory;
import id.ac.ui.cs.advprog.tk_adpro.strategy.PaymentStrategy;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        UUID donaturId = UUID.randomUUID();
        Donation donation = new Donation(UUID.randomUUID(), UUID.randomUUID(), donaturId, 169500, DonationStatus.COMPLETED.getValue(), LocalDateTime.now(), "Get well soon!");
        when(paymentStrategy.checkBalance(donaturId)).thenReturn(169500);

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
        when(paymentStrategy.checkBalance(donaturId)).thenReturn(169499);

        assertThrows(InsufficientBalanceException.class, () -> donationService.checkBalance(donation));
    }

    @Test
    void testCreateDonation_PaymentSuccess() {
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
        when(paymentStrategy.processPayment(donaturId, 169500)).thenReturn(true);
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation result = donationService.createDonation(donation);
        verify(paymentStrategy).processPayment(donaturId, 169500);
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
        when(paymentStrategy.processPayment(donaturId, 169500)).thenReturn(false);

        assertThrows(InsufficientBalanceException.class, () -> donationService.createDonation(donation));
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
        assertEquals(donation, result);
    }

    @Test
    void testCreateDonation_WithNullId_GeneratesUUID() {
        Donation donation = new Donation(
            null,
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
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("donationId")
            .isEqualTo(donation);
    }

    @Test
    void testCompleteDonation() {
        Donation donation = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
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
            when(donationRepository.findById(donation.getDonationId())).thenReturn(Optional.of(donation));
            factoryMock.when(() -> DonationStatusStateFactory.getState(donation)).thenReturn(mockState);
            when(paymentStrategy.processPayment(donation.getDonaturId(), donation.getAmount())).thenReturn(true);
            when(donationRepository.save(donation)).thenReturn(donation);

            donationService.completeDonation(donation.getDonationId());

            verify(mockState).complete(donation);
            verify(paymentStrategy).processPayment(donation.getDonaturId(), donation.getAmount());
            verify(donationRepository).save(donation);
            assertEquals(DonationStatus.COMPLETED.getValue(), donation.getStatus());
        }
    }

    @Test
    void testCompleteDonation_DonationNotFound() {
        UUID donationId = UUID.randomUUID();
        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> donationService.completeDonation(donationId));
    }

    @Test
    void testCancelDonation() {
        Donation donation = new Donation(
            UUID.randomUUID(),
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
            when(donationRepository.findById(donation.getDonationId())).thenReturn(Optional.of(donation));
            factoryMock.when(() -> DonationStatusStateFactory.getState(donation)).thenReturn(mockState);
            when(donationRepository.save(donation)).thenReturn(donation);

            donationService.cancelDonation(donation.getDonationId());

            verify(mockState).cancel(donation);
            verify(donationRepository).save(donation);
            assertEquals(DonationStatus.CANCELED.getValue(), donation.getStatus());
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
        Donation donation = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        when(donationRepository.findById(donation.getDonationId())).thenReturn(Optional.of(donation));

        Donation result = donationService.getDonationByDonationId(donation.getDonationId());
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
        Donation donation = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        when(donationRepository.findById(donation.getDonationId())).thenReturn(Optional.of(donation));
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation result = donationService.updateDonationMessage(donation.getDonationId(), "New Message");
        assertEquals("New Message", result.getMessage());
    }

    @Test
    void testUpdateDonationMessage_DonationNotFound() {
        UUID id = UUID.randomUUID();
        when(donationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> donationService.updateDonationMessage(id, "new message"));
    }

    @Test
    void testDeleteDonationMessage() {
        Donation donation = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );
        when(donationRepository.findById(donation.getDonationId())).thenReturn(Optional.of(donation));
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation result = donationService.deleteDonationMessage(donation.getDonationId());
        assertNull(result.getMessage());
    }

    @Test
    void testDeleteDonationMessage_DonationNotFound() {
        UUID id = UUID.randomUUID();
        when(donationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> donationService.deleteDonationMessage(id));
    }
}