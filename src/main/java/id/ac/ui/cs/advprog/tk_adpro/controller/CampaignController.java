package id.ac.ui.cs.advprog.tk_adpro.controller;

import id.ac.ui.cs.advprog.tk_adpro.enums.CampaignStatus;
import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.service.CampaignService;

import java.time.LocalDateTime;
import java.util.List;

import id.ac.ui.cs.advprog.tk_adpro.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/campaign")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

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

    @PutMapping("/")
    public ResponseEntity<Campaign> updateCampaign(@RequestBody Campaign campaign) {
        campaignService.updateCampaign(campaign);
        return ResponseEntity.ok(campaign);
    }

    @DeleteMapping("/{campaignId}/delete")
    public ResponseEntity<Void> deleteCampaign(@PathVariable UUID campaignId) {
        campaignService.deleteCampaign(campaignId);
        return ResponseEntity.ok().build();
    }
}