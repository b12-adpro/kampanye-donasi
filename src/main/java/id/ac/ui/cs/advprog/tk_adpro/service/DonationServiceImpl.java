package id.ac.ui.cs.advprog.tk_adpro.service;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.repository.DonationRepository;
import id.ac.ui.cs.advprog.tk_adpro.state.DonationStatusState;
import id.ac.ui.cs.advprog.tk_adpro.state.DonationStatusStateFactory;
import id.ac.ui.cs.advprog.tk_adpro.strategy.PaymentStrategy;
import id.ac.ui.cs.advprog.tk_adpro.exception.InsufficientBalanceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DonationServiceImpl implements DonationService {
    @Autowired
    private DonationRepository donationRepository;

    private PaymentStrategy paymentStrategy;

    @Override
    public Donation checkBalance(Donation donation) {
        if (paymentStrategy.checkBalance(donation.getDonaturId()) < donation.getAmount()) {
            throw new InsufficientBalanceException("Not enough balance!");
        }
        return donation;
    }

    @Override
    public Donation createDonation(Donation donation) {
        if (donation.getStatus().equals(DonationStatus.COMPLETED.getValue())) {
            boolean paymentSuccess = paymentStrategy.processPayment(donation.getDonaturId(), donation.getAmount());

            if (!paymentSuccess) {
                throw new InsufficientBalanceException("Not enough balance!");
            }
            //TODO: Notify Campaign
        }
        donationRepository.save(donation);
        return donation;
    }

    @Override
    public Donation completeDonation(UUID donationId) {
        Donation donation = donationRepository.findByDonationId(donationId);
        DonationStatusState currentState = DonationStatusStateFactory.getState(donation);
        currentState.complete(donation);
        return createDonation(donation);
    }

    @Override
    public Donation cancelDonation(UUID donationId) {
        Donation donation = donationRepository.findByDonationId(donationId);
        DonationStatusState currentState = DonationStatusStateFactory.getState(donation);
        currentState.cancel(donation);
        donationRepository.save(donation);
        return donation;
    }

    @Override
    public Donation getDonationByDonationId(UUID donationId) {
        return donationRepository.findByDonationId(donationId);
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
    public Donation updateDonationMessage(UUID donationId, String newMessage) {
        Donation donation = donationRepository.findByDonationId(donationId);
        if (donation != null) {
            donation.setMessage(newMessage);
            return donationRepository.save(donation);
        }
        return null;
    }

    @Override
    public Donation deleteDonationMessage(UUID donationId) {
        Donation donation = donationRepository.findByDonationId(donationId);
        if (donation != null) {
            donation.setMessage(null);
            return donationRepository.save(donation);
        }
        return null;
    }
}