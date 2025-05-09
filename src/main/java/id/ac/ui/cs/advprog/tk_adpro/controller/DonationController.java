package id.ac.ui.cs.advprog.tk_adpro.controller;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.service.DonationService;
import id.ac.ui.cs.advprog.tk_adpro.exception.InsufficientBalanceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    @Autowired
    private DonationService donationService;

    @PostMapping("/campaigns/{campaignId}")
    public ResponseEntity<Object> createDonation(@PathVariable UUID campaignId, @RequestBody Donation donation) {
        try {
            donation.setCampaignId(campaignId);
            if (donation.getDatetime() == null) donation.setDatetime(LocalDateTime.now());

            donationService.checkBalance(donation);
            Donation createdDonation = donationService.createDonation(donation);
            return new ResponseEntity<>(createdDonation, HttpStatus.CREATED);
        } catch (InsufficientBalanceException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to create donation: " + e.getMessage());
        }
    }

    @PatchMapping("/{donationId}/status")
    public ResponseEntity<Object> updateDonationStatus(@PathVariable UUID donationId, @RequestBody Map<String, String> statusUpdate) {
        try {
            String status = statusUpdate.get("status");
            Donation updatedDonation;
            if (DonationStatus.COMPLETED.getValue().equals(status)) {
                updatedDonation = donationService.completeDonation(donationId);
            } else if (DonationStatus.CANCELED.getValue().equals(status)) {
                updatedDonation = donationService.cancelDonation(donationId);
            } else {
                return badRequest("Invalid status value. Accepted values: COMPLETED, CANCELED");
            }
            return new ResponseEntity<>(updatedDonation, HttpStatus.OK);
        } catch (InsufficientBalanceException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to update donation status: " + e.getMessage());
        }
    }

    @GetMapping("/{donationId}")
    public ResponseEntity<Object> getDonation(@PathVariable UUID donationId) {
        try {
            Donation donation = donationService.getDonationByDonationId(donationId);
            return donation == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(donation, HttpStatus.OK);
        } catch (Exception e) {
            return serverError("Failed to retrieve donation: " + e.getMessage());
        }
    }

    @GetMapping("/donaturs/{donaturId}")
    public ResponseEntity<Object> getDonationsByDonatur(@PathVariable UUID donaturId) {
        try {
            List<Donation> donations = donationService.getDonationsByDonaturId(donaturId);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            return serverError("Failed to retrieve donations: " + e.getMessage());
        }
    }

    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<Object> getDonationsByCampaign(@PathVariable UUID campaignId) {
        try {
            List<Donation> donations = donationService.getDonationsByCampaignId(campaignId);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            return serverError("Failed to retrieve donations: " + e.getMessage());
        }
    }

    @PatchMapping("/{donationId}/message")
    public ResponseEntity<Object> updateDonationMessage(@PathVariable UUID donationId, @RequestBody Map<String, String> payload) {
        try {
            String newMessage = payload.get("message");
            Donation updatedDonation = donationService.updateDonationMessage(donationId, newMessage);
            return updatedDonation == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(updatedDonation, HttpStatus.OK);
        } catch (Exception e) {
            return serverError("Failed to update message: " + e.getMessage());
        }
    }

    @DeleteMapping("/{donationId}/message")
    public ResponseEntity<Object> deleteDonationMessage(@PathVariable UUID donationId) {
        try {
            Donation updatedDonation = donationService.deleteDonationMessage(donationId);
            return updatedDonation == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(updatedDonation, HttpStatus.OK);
        } catch (Exception e) {
            return serverError("Failed to delete message: " + e.getMessage());
        }
    }

    private ResponseEntity<Object> badRequest(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> serverError(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}