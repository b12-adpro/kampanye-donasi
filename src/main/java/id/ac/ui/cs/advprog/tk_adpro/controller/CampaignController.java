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

@RestController
@RequestMapping("/api/campaign")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @PostMapping("/{fundraiserId}/campaign")
    public ResponseEntity<Campaign> verifyCampaign(@PathVariable String fundraiserId, @RequestBody Campaign campaign) {
        Campaign savedCampaign = campaignService.createCampaign(campaign);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCampaign);
    }

    @PostMapping("/{campaignId}/create-campaign")
    public ResponseEntity<Campaign> createCampaign(@PathVariable String campaignId, @RequestBody Campaign campaign) {
        Campaign savedCampaign = campaignService.createCampaign(campaign);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCampaign);
    }

    @PutMapping("/{campaignId}/activate")
    public ResponseEntity<Void> activateCampaign(@PathVariable String campaignId) {
        campaignService.activateCampaign(campaignId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{campaignId}/inactivate")
    public ResponseEntity<Void> inactivateCampaign(@PathVariable String campaignId) {
        campaignService.inactivateCampaign(campaignId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-by-fundraiser/{fundraiserId}")
    public ResponseEntity<List<Campaign>> getCampaignByFundraiserId(@PathVariable String fundraiserId) {
        List<Campaign> campaigns = campaignService.getCampaignByFundraiserId(fundraiserId);
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/get-by-id/{campaignId}")
    public ResponseEntity<Campaign> getCampaignByCampaignId(@PathVariable String campaignId) {
        Campaign campaign = campaignService.getCampaignByCampaignId(campaignId);
        if (campaign != null) {
            return ResponseEntity.ok(campaign);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update-judul")
    public ResponseEntity<Void> updateCampaignJudul(@RequestBody Campaign campaign) {
        campaignService.updateCampaignJudul(campaign.getCampaignId(), campaign.getJudul());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-target")
    public ResponseEntity<Void> updateCampaignTarget(@RequestBody Campaign campaign) {
        campaignService.updateCampaignTarget(campaign.getCampaignId(), campaign.getTarget());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-deskripsi")
    public ResponseEntity<Void> updateCampaignDeskripsi(@RequestBody Campaign campaign) {
        campaignService.updateCampaignDeskripsi(campaign.getCampaignId(), campaign.getDeskripsi());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{campaignId}/delete")
    public ResponseEntity<Void> deleteCampaign(@PathVariable String campaignId) {
        campaignService.deleteCampaign(campaignId);
        return ResponseEntity.ok().build();
    }
}