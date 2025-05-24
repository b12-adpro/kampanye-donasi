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
@CrossOrigin(origins = "http://localhost:3000")
public class DonationController {
    @Autowired
    private DonationService donationService;

    @PostMapping("/campaigns")
    public ResponseEntity<Object> createDonation(
            @RequestBody @Valid DonationDTO donationRequest
    ) {
        try {
            // Buat instance Donation dari DTO
            Donation newDonation = new Donation(
                null,
                donationRequest.getCampaignId(),
                donationRequest.getDonaturId(),
                donationRequest.getAmount(),
                donationRequest.get
            );

            // 1. Set campaignId dari @PathVariable (sumber kebenaran)
            newDonation.setCampaignId(campaignId);

            // 2. Set field lain dari DTO
            newDonation.set
            newDonation.setDonaturId(donationRequest.getDonaturId());
            newDonation.setAmount(donationRequest.getAmount());
            newDonation.setMessage(donationRequest.getMessage());

            // 3. Handle datetime
            // Jika klien mengirim datetime, gunakan itu. Jika tidak, set ke waktu sekarang.
            if (donationRequest.getDatetime() != null) {
                newDonation.setDatetime(donationRequest.getDatetime());
            } else {
                newDonation.setDatetime(LocalDateTime.now());
            }

            // 4. Set status donasi (tidak ada di DTO, di-set oleh server)
            newDonation.setStatus("PENDING"); // Asumsi Donation memiliki setStatus() dan field status

            // donationId akan di-generate oleh @PrePersist saat entitas disimpan.
            // Pastikan kelas Donation Anda memiliki setter yang sesuai.

            Donation createdDonation = donationService.createDonation(newDonation);
            return new ResponseEntity<>(createdDonation, HttpStatus.CREATED);

        } catch (InsufficientBalanceException e) {
            return badRequest(e.getMessage());
        } /* catch (MethodArgumentNotValidException e) { // Jika menggunakan @Valid dan validasi gagal
            // Handle validation errors, misalnya:
            // Map<String, String> errors = new HashMap<>();
            // e.getBindingResult().getAllErrors().forEach((error) -> {
            //     String fieldName = ((FieldError) error).getField();
            //     String errorMessage = error.getDefaultMessage();
            //     errors.put(fieldName, errorMessage);
            // });
            // return ResponseEntity.badRequest().body(errors);
        } */ catch (Exception e) {
            // log.error("Error creating donation: ", e); // Sebaiknya log error
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