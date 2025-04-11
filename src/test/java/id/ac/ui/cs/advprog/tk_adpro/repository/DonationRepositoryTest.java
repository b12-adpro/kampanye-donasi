package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

class DonationRepositoryTest {
    DonationRepository donationRepository;
    Donation donation = new Donation(
        "13652556-012a-4c07-b546-54eb1396d79b",
        "eb558e9f-1c39-460e-8860-71af6af63bd6",
        123L,
        169500,
        DonationStatus.PENDING.getValue(),
        LocalDateTime.now(),
        "Get well soon!"
    );

    @BeforeEach
    void setUp() {
        donationRepository = new DonationRepository();
    }

    @Test
    void testSaveCreate() {
        Donation result = donationRepository.save(donation);
        assertEquals(donation.getDonationId(), result.getDonationId());
        assertEquals(donation.getCampaignId(), result.getCampaignId());
        assertEquals(donation.getAmount(), result.getAmount());
        assertEquals(donation.getDatetime(), result.getDatetime());
        assertEquals(donation.getMessage(), result.getMessage());
    }

    @Test
    void testSaveUpdate() {
        donationRepository.save(donation);

        Donation newDonation = new Donation(
            donation.getDonationId(),
            donation.getCampaignId(),
            donation.getDonaturId(),
            1,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now(),
            "Tung tung tung sahur!"
        );
        Donation result = donationRepository.save(newDonation);
        Donation findResult = donationRepository.findByDonationId(donation.getDonationId());

        assertEquals(result.getDonationId(), findResult.getDonationId());
        assertEquals(result.getCampaignId(), findResult.getCampaignId());
        assertEquals(result.getAmount(), findResult.getAmount());
        assertEquals(result.getDatetime(), findResult.getDatetime());
        assertEquals(result.getMessage(), findResult.getMessage());
    }

    @Test
    void testFindByDonationIdIfDonationIdFound() {
        donationRepository.save(donation);
        Donation result = donationRepository.findByDonationId(donation.getDonationId());
        assertEquals(donation.getDonationId(), result.getDonationId());
        assertEquals(donation.getCampaignId(), result.getCampaignId());
        assertEquals(donation.getAmount(), result.getAmount());
        assertEquals(donation.getDatetime(), result.getDatetime());
        assertEquals(donation.getMessage(), result.getMessage());
    }

    @Test
    void testFindByIdDonationIfDonationIdNotFound() {
        Donation result = donationRepository.findByDonationId("Bombardini Gusini");
        assertNull(result);
    }

    @Test
    void testFindByCampaignIdIfIdCampaignFound() {
        donationRepository.save(donation);
        List<Donation> donationList = donationRepository.findByCampaignId(donation.getCampaignId());
        assertEquals(1, donationList.size());
    }

    @Test
    void testFindByCampaignIdIfIdCampaignNotFound() {
        List<Donation> donationList = donationRepository.findByCampaignId("Bombardiro Crocodilo");
        assertTrue(donationList.isEmpty());
    }

    @Test
    void testFindByDonaturIdIfIdDonaturFound() {
        donationRepository.save(donation);
        List<Donation> donationList = donationRepository.findByDonaturId(donation.getDonaturId());
        assertEquals(1, donationList.size());
    }

    @Test
    void testFindByDonaturIdIfIdDonaturNotFound() {
        List<Donation> donationList = donationRepository.findByDonaturId(-69L);
        assertTrue(donationList.isEmpty());
    }
}