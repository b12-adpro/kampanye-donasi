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

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    @Autowired
    private DonationService donationService;

    @PostMapping("/campaigns/{campaignId}")
    public ResponseEntity<Object> createDonation(@PathVariable String campaignId, @RequestBody Donation donation) {
        try {
            donation.setCampaignId(campaignId);
            if (donation.getDatetime() == null) donation.setDatetime(LocalDateTime.now());

            donationService.checkBalance(donation);
            Donation createdDonation = donationService.createDonation(donation);
            return new ResponseEntity<>(createdDonation, HttpStatus.CREATED);
        } catch (InsufficientBalanceException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create donation: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{donationId}/status")
    public ResponseEntity<Object> updateDonationStatus(@PathVariable String donationId, @RequestBody Map<String, String> statusUpdate) {
        try {
            String status = statusUpdate.get("status");

            Donation updatedDonation;
            if (DonationStatus.COMPLETED.getValue().equals(status)) {
                updatedDonation = donationService.completeDonation(donationId);
            } else if (DonationStatus.CANCELLED.getValue().equals(status)) {
                updatedDonation = donationService.cancelDonation(donationId);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid status value. Accepted values: COMPLETED, CANCELED");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(updatedDonation, HttpStatus.OK);
        } catch (InsufficientBalanceException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update donation status: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{donationId}")
    public ResponseEntity<Object> getDonation(@PathVariable String donationId) {
        try {
            Donation donation = donationService.getDonationByDonationId(donationId);
            if (donation == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(donation, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve donation: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/donaturs/{donaturId}")
    public ResponseEntity<Object> getDonationsByDonatur(@PathVariable long donaturId) {
        try {
            List<Donation> donations = donationService.getDonationsByDonaturId(donaturId);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve donations: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<Object> getDonationsByCampaign(@PathVariable String campaignId) {
        try {
            List<Donation> donations = donationService.getDonationsByCampaignId(campaignId);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve donations: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{donationId}/message")
    public ResponseEntity<Object> updateDonationMessage(@PathVariable String donationId, @RequestBody Map<String, String> payload) {
        try { 
            String newMessage = payload.get("message");
            Donation updatedDonation = donationService.updateDonationMessage(donationId, newMessage);
            if (updatedDonation == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(updatedDonation, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update message: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{donationId}/message")
    public ResponseEntity<Object> deleteDonationMessage(@PathVariable String donationId) {
        try {
            Donation updatedDonation = donationService.deleteDonationMessage(donationId);
            if (updatedDonation == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(updatedDonation, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete message: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}