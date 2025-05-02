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

    @Test
    void testSaveUpdateAtNonZeroIndex() {
        donationRepository.save(donation);

        Donation other = new Donation(
            "other-id",
            "campaignX",
            999L,
            1000,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Other"
        );
        donationRepository.save(other);

        Donation updated = new Donation(
            donation.getDonationId(),
            donation.getCampaignId(),
            donation.getDonaturId(),
            555,
            DonationStatus.COMPLETED.getValue(),
            LocalDateTime.now(),
            "Updated"
        );
        donationRepository.save(updated);

        Donation first = donationRepository.findByDonationId(other.getDonationId());
        Donation second = donationRepository.findByDonationId(donation.getDonationId());
        assertEquals(1000, first.getAmount());
        assertEquals(555, second.getAmount());
        assertEquals("Updated", second.getMessage());
    }

    @Test
    void testFindByCampaignIdMultipleEntries() {
        Donation d1 = new Donation(
            "id1",
            "campA",
            1L,
            10,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now()
        );
        donationRepository.save(d1);

        Donation d2 = new Donation(
            "id2",
            "campA",
            2L,
            20,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now()
        );
        donationRepository.save(d2);

        Donation d3 = new Donation(
            "id3",
            "campB",
            3L,
            30,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now()
        );
        donationRepository.save(d3);

        List<Donation> listA = donationRepository.findByCampaignId("campA");
        List<Donation> listB = donationRepository.findByCampaignId("campB");
        assertEquals(2, listA.size());
        assertEquals(1, listB.size());
    }

    @Test
    void testFindByDonaturIdMultipleEntries() {
        Donation d1 = new Donation(
            "id1",
            "campX",
            10L,
            100,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now()
        );
        donationRepository.save(d1);

        Donation d2 = new Donation(
            "id2",
            "campY",
            10L,
            200,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now()
        );
        donationRepository.save(d2);

        Donation d3 = new Donation(
            "id3",
            "campZ",
            20L,
            300,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now()
        );
        donationRepository.save(d3);

        List<Donation> list10 = donationRepository.findByDonaturId(10L);
        List<Donation> list20 = donationRepository.findByDonaturId(20L);
        assertEquals(2, list10.size());
        assertEquals(1, list20.size());
    }

    @Test
    void testFindByDonationIdMultipleEntries() {
        Donation d1 = new Donation(
            "idA",
            "camp1",
            1L,
            10,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "A"
        );
        Donation saved1 = donationRepository.save(d1);

        Donation d2 = new Donation(
            "idB",
            "camp2",
            2L,
            20,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "B"
        );
        Donation saved2 = donationRepository.save(d2);

        Donation foundA = donationRepository.findByDonationId(saved1.getDonationId());
        Donation foundB = donationRepository.findByDonationId(saved2.getDonationId());
        assertNotNull(foundA);
        assertNotNull(foundB);
        assertEquals("A", foundA.getMessage());
        assertEquals(20, foundB.getAmount());
    }

    @Test
    void testSaveWithoutDonationId() {
        Donation newDonation = new Donation(
            null,
            "campX",
            42L,
            500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now()
        );

        Donation result = donationRepository.save(newDonation);
        assertNotNull(result.getDonationId());
        assertNotEquals("nonexistent-id", result.getDonationId());

        Donation found = donationRepository.findByDonationId(result.getDonationId());
        assertNotNull(found.getDonationId());
        assertEquals(500, found.getAmount());
        assertEquals(1, donationRepository.findByCampaignId("campX").size());
    }
}