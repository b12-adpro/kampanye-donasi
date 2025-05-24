package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DonationRepositoryTest {
    @Autowired
    private DonationRepository donationRepository;

    private static final Donation donation = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            169500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now(),
            "Get well soon!"
        );

    @Test
    void testSaveCreate() {
        Donation saved = donationRepository.save(donation);
        assertNotNull(saved.getDonationId());

        Optional<Donation> foundOpt = donationRepository.findById(saved.getDonationId());
        assertTrue(foundOpt.isPresent());

        Donation found = foundOpt.get();
        assertEquals(donation.getCampaignId(), found.getCampaignId());
        assertEquals(donation.getAmount(), found.getAmount());
        assertEquals(donation.getDatetime(), found.getDatetime());
        assertEquals(donation.getMessage(), found.getMessage());
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

        Optional<Donation> findOpt = donationRepository.findById(donation.getDonationId());
        assertTrue(findOpt.isPresent());

        Donation findResult = findOpt.get();
        assertEquals(result.getDonationId(), findResult.getDonationId());
        assertEquals(result.getCampaignId(), findResult.getCampaignId());
        assertEquals(result.getAmount(), findResult.getAmount());
        assertEquals(result.getDatetime(), findResult.getDatetime());
        assertEquals(result.getMessage(), findResult.getMessage());
    }

    @Test
    void testFindByDonationIdIfDonationIdFound() {
        donationRepository.save(donation);
        Optional<Donation> result = donationRepository.findById(donation.getDonationId());
        assertTrue(result.isPresent());
        assertEquals(donation.getDonationId(), result.get().getDonationId());
    }

    @Test
    void testFindByIdDonationIfDonationIdNotFound() {
        Optional<Donation> result = donationRepository.findById(UUID.randomUUID());
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByCampaignIdIfIdCampaignFound() {
        donationRepository.save(donation);
        List<Donation> donationList = donationRepository.findByCampaignId(donation.getCampaignId());
        assertEquals(1, donationList.size());
    }

    @Test
    void testFindByCampaignIdIfIdCampaignNotFound() {
        List<Donation> donationList = donationRepository.findByCampaignId(UUID.randomUUID());
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
        List<Donation> donationList = donationRepository.findByDonaturId(UUID.randomUUID());
        assertTrue(donationList.isEmpty());
    }

    @Test
    void testSaveUpdateAtNonZeroIndex() {
        donationRepository.save(donation);

        Donation other = new Donation(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
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

        Optional<Donation> firstOpt = donationRepository.findById(other.getDonationId());
        Optional<Donation> secondOpt = donationRepository.findById(donation.getDonationId());
        assertTrue(firstOpt.isPresent());
        assertTrue(secondOpt.isPresent());

        Donation first = firstOpt.get();
        Donation second = secondOpt.get();
        assertEquals(1000, first.getAmount());
        assertEquals(555, second.getAmount());
        assertEquals("Updated", second.getMessage());
    }

    @Test
    void testFindByCampaignIdMultipleEntries() {
        UUID campA = UUID.randomUUID();
        UUID campB = UUID.randomUUID();

        Donation d1 = new Donation(UUID.randomUUID(), campA, UUID.randomUUID(), 10, DonationStatus.PENDING.getValue(), LocalDateTime.now());
        donationRepository.save(d1);

        Donation d2 = new Donation(UUID.randomUUID(), campA, UUID.randomUUID(), 20, DonationStatus.PENDING.getValue(), LocalDateTime.now());
        donationRepository.save(d2);

        Donation d3 = new Donation(UUID.randomUUID(), campB, UUID.randomUUID(), 30, DonationStatus.PENDING.getValue(), LocalDateTime.now());
        donationRepository.save(d3);

        List<Donation> listA = donationRepository.findByCampaignId(campA);
        List<Donation> listB = donationRepository.findByCampaignId(campB);
        assertEquals(2, listA.size());
        assertEquals(1, listB.size());
    }

    @Test
    void testFindByDonaturIdMultipleEntries() {
        UUID donatur1 = UUID.randomUUID();
        UUID donatur2 = UUID.randomUUID();

        Donation d1 = new Donation(UUID.randomUUID(), UUID.randomUUID(), donatur1, 100, DonationStatus.PENDING.getValue(), LocalDateTime.now());
        donationRepository.save(d1);

        Donation d2 = new Donation(UUID.randomUUID(), UUID.randomUUID(), donatur1, 200, DonationStatus.PENDING.getValue(), LocalDateTime.now());
        donationRepository.save(d2);

        Donation d3 = new Donation(UUID.randomUUID(), UUID.randomUUID(), donatur2, 300, DonationStatus.PENDING.getValue(), LocalDateTime.now());
        donationRepository.save(d3);

        List<Donation> list1 = donationRepository.findByDonaturId(donatur1);
        List<Donation> list2 = donationRepository.findByDonaturId(donatur2);
        assertEquals(2, list1.size());
        assertEquals(1, list2.size());
    }

    @Test
    void testFindByDonationIdMultipleEntries() {
        Donation d1 = new Donation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10, DonationStatus.PENDING.getValue(), LocalDateTime.now(), "A");
        Donation saved1 = donationRepository.save(d1);

        Donation d2 = new Donation(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 20, DonationStatus.PENDING.getValue(), LocalDateTime.now(), "B");
        Donation saved2 = donationRepository.save(d2);

        Optional<Donation> foundAOpt = donationRepository.findById(saved1.getDonationId()); // [UPDATED]
        Optional<Donation> foundBOpt = donationRepository.findById(saved2.getDonationId()); // [UPDATED]
        assertTrue(foundAOpt.isPresent());
        assertTrue(foundBOpt.isPresent());

        Donation foundA = foundAOpt.get();
        Donation foundB = foundBOpt.get();
        assertEquals("A", foundA.getMessage());
        assertEquals(20, foundB.getAmount());
    }

    @Test
    void testSaveWithoutDonationId() {
        Donation newDonation = new Donation(
            null,
            UUID.randomUUID(),
            UUID.randomUUID(),
            500,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now()
        );

        Donation result = donationRepository.save(newDonation);
        assertNotNull(result.getDonationId());

        Optional<Donation> foundOpt = donationRepository.findById(result.getDonationId());
        assertTrue(foundOpt.isPresent());

        Donation found = foundOpt.get();
        assertEquals(500, found.getAmount());
        assertEquals(1, donationRepository.findByCampaignId(newDonation.getCampaignId()).size());
    }
}