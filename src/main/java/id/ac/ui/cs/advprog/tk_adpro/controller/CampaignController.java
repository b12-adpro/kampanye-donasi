package id.ac.ui.cs.advprog.tk_adpro.controller;

import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import id.ac.ui.cs.advprog.tk_adpro.service.CampaignService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.PostConstruct;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/campaign")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    private final Path rootLocation = Paths.get("uploads/campaign_proofs");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @PostMapping("/upload/bukti")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File tidak boleh kosong.");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = System.currentTimeMillis() + "_" + (originalFilename != null ? originalFilename.replaceAll("\\s+", "_") : "file");
            Path destinationFile = this.rootLocation.resolve(uniqueFilename).normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/campaign_proofs/")
                    .path(uniqueFilename)
                    .toUriString();

            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gagal mengunggah file.");
        }
    }

    @PostMapping("/{fundraiserId}/campaign")
    public ResponseEntity<Campaign> verifyCampaign(@PathVariable UUID fundraiserId, @RequestBody Campaign campaign) {
        Campaign savedCampaign = campaignService.createCampaign(campaign);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCampaign);
    }

    @PostMapping("/")
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        Campaign savedCampaign = campaignService.createCampaign(campaign);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCampaign);
    }

    @PutMapping("/{campaignId}/activate")
    public ResponseEntity<Void> activateCampaign(@PathVariable UUID campaignId) {
        campaignService.activateCampaign(campaignId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{campaignId}/inactivate")
    public ResponseEntity<Void> inactivateCampaign(@PathVariable UUID campaignId) {
        campaignService.inactivateCampaign(campaignId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fundraiserId/{fundraiserId}")
    public ResponseEntity<List<Campaign>> getCampaignByFundraiserId(@PathVariable UUID fundraiserId) {
        List<Campaign> campaigns = campaignService.getCampaignByFundraiserId(fundraiserId);
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/campaignId/{campaignId}")
    public ResponseEntity<Campaign> getCampaignByCampaignId(@PathVariable UUID campaignId) {
        Campaign campaign = campaignService.getCampaignByCampaignId(campaignId);
        if (campaign != null) {
            return ResponseEntity.ok(campaign);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{campaignId}/bukti")
    public ResponseEntity<String> getBuktiPenggalanganDana(@PathVariable UUID campaignId) {
        try {
            String bukti = campaignService.getBuktiPenggalanganDana(campaignId);
            if (bukti != null && !bukti.isEmpty()) {
                return ResponseEntity.ok(bukti);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{campaignId}/update")
    public ResponseEntity<Campaign> updateCampaign(@PathVariable UUID campaignId, @RequestBody Campaign campaign) {
        try {
            campaign.setCampaignId(campaignId);
            Campaign updated = campaignService.updateCampaign(campaignId, campaign);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{campaignId}/delete")
    public ResponseEntity<Void> deleteCampaign(@PathVariable UUID campaignId) {
        campaignService.deleteCampaign(campaignId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        List<Campaign> campaigns = campaignService.getAllCampaigns();
        return ResponseEntity.ok(campaigns);
    }
}