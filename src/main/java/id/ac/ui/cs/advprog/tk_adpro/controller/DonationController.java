package id.ac.ui.cs.advprog.tk_adpro.controller;

import id.ac.ui.cs.advprog.tk_adpro.dto.DonationDTO;
import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.service.DonationService;
import id.ac.ui.cs.advprog.tk_adpro.exception.InsufficientBalanceException;

import jakarta.validation.Valid;
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
@CrossOrigin(origins = {"http://localhost:3000", "https://fe-aryaradityakusuma2006-gmailcoms-projects.vercel.app"})
public class DonationController {
    @Autowired
    private DonationService donationService;

    @PostMapping("/campaigns")
    public ResponseEntity<Object> createDonation(
            @RequestBody @Valid DonationDTO donationRequest
    ) {
        try {
            Donation newDonation = new Donation(
                donationRequest.getDonationId(),
                donationRequest.getCampaignId(),
                donationRequest.getDonaturId(),
                donationRequest.getAmount(),
                donationRequest.getStatus(),
                (donationRequest.getDatetime() != null) ? donationRequest.getDatetime() : LocalDateTime.now(),
                donationRequest.getMessage()
            );
            Donation createdDonation = donationService.createDonation(newDonation);
            return new ResponseEntity<>(createdDonation, HttpStatus.CREATED);
        } catch (InsufficientBalanceException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to create donation: " + e.getMessage());
        }
    }

    @PatchMapping("/cancel")
    public ResponseEntity<Object> cancelDonation(@RequestBody UUID donationId) {
        try {
            String donationStatus = donationService.getDonationByDonationId(donationId).getStatus();
            if (donationStatus.equals(DonationStatus.PENDING.getValue())) {
                Donation updatedDonation = donationService.cancelDonation(donationId);
                return new ResponseEntity<>(updatedDonation, HttpStatus.OK);
            } else {
                return badRequest("The current status of Donation can't be canceled!");
            }
        } catch (InsufficientBalanceException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError("Failed to cancel Donation: " + e.getMessage());
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

    @PatchMapping("/message")
    public ResponseEntity<Object> updateDonationMessage(
            @RequestParam("donationId") UUID donationId,
            @RequestParam("message") String message) {
        try {
            Donation updatedDonation = donationService.updateDonationMessage(donationId, message);
            return updatedDonation == null
                    ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(updatedDonation, HttpStatus.OK);
        } catch (Exception e) {
            return serverError("Failed to update message: " + e.getMessage());
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