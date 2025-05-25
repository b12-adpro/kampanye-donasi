package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.repository.DonationRepository;
import id.ac.ui.cs.advprog.tk_adpro.state.DonationStatusState;
import id.ac.ui.cs.advprog.tk_adpro.state.DonationStatusStateFactory;
import id.ac.ui.cs.advprog.tk_adpro.strategy.PaymentServiceApiStrategy;
import id.ac.ui.cs.advprog.tk_adpro.strategy.PaymentStrategy;
import id.ac.ui.cs.advprog.tk_adpro.exception.InsufficientBalanceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class DonationServiceImpl implements DonationService {
    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private PaymentServiceApiStrategy paymentStrategy;

    @Override
    public Donation checkBalance(Donation donation) {
        if (paymentStrategy.checkBalance(donation.getDonaturId()) < donation.getAmount()) {
            throw new InsufficientBalanceException("Not enough balance!");
        }
        return donation;
    }

    @Override
    @Transactional
    public Donation createDonation(Donation donation) {
        if (donation.getStatus().equals(DonationStatus.COMPLETED.getValue())) {
            try {
                checkBalance(donation);
                paymentStrategy.processPayment(
                    donation.getDonationId(),
                    donation.getCampaignId(),
                    donation.getDonaturId(),
                    donation.getAmount()
                );
                //TODO: Notify Campaign
            } catch (InsufficientBalanceException e) {
                DonationStatusState currentState = DonationStatusStateFactory.getState(donation);
                currentState.pending(donation);
            }
        }
        return donationRepository.save(donation);
    }

    @Override
    @Transactional
    public Donation cancelDonation(UUID donationId) {
        Donation donation = getDonationByIdOrThrow(donationId);
        DonationStatusState currentState = DonationStatusStateFactory.getState(donation);
        currentState.cancel(donation);
        return donationRepository.save(donation);
    }

    @Override
    public Donation getDonationByDonationId(UUID donationId) {
        Optional<Donation> donation = donationRepository.findById(donationId);
        return donation.orElse(null);
    }

    private Donation getDonationByIdOrThrow(UUID donationId) {
        return donationRepository.findById(donationId)
            .orElseThrow(() -> new RuntimeException("Donation not found with id: " + donationId));
    }

    @Override
    public List<Donation> getDonationsByDonaturId(UUID donaturId) {
        return donationRepository.findByDonaturId(donaturId);
    }

    @Override
    public List<Donation> getDonationsByCampaignId(UUID campaignId) {
        return donationRepository.findByCampaignId(campaignId);
    }

    @Override
    @Transactional
    public Donation updateDonationMessage(UUID donationId, String newMessage) {
        Donation donation = getDonationByIdOrThrow(donationId);
        donation.setMessage(newMessage);
        return donationRepository.save(donation);
    }
}