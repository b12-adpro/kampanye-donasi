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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/campaign")
public class CampaignController {
    @Autowired
    private CampaignService campaignService;

    @GetMapping("/{fundraiserId}/create-campaign-page")
    public String createCampaignPage(@PathVariable String fundraiserId, Model model) {
        Campaign campaign = new Campaign();
        campaign.setFundraiserId(fundraiserId);
        model.addAttribute("campaign", campaign);
        return "CampaignForm";
    }

    @PostMapping("/{fundraiserId}/campaign")
    public String verifyCampaign(@PathVariable String fundraiserId, @ModelAttribute("campaign") Campaign campaign) {
        campaignService.createCampaign(campaign);
        return "VerifyCampaign";
    }

    @PostMapping("/{campaignId}/create-campaign")
    public String createCampaign(@PathVariable String campaignId, @ModelAttribute Campaign campaign) {
        campaignService.createCampaign(campaign);
        return "redirect:/";
    }

    @PutMapping("/{campaignId}/activate")
    public String activateCampaign(@PathVariable String campaignId) {
        campaignService.activateCampaign(campaignId);
        return "redirect:/";
    }

    @PutMapping("/{campaignId}/inactivate")
    public String inactivateCampaign(@PathVariable String campaignId) {
        campaignService.inactivateCampaign(campaignId);
        return "redirect:/";
    }

    @GetMapping("/get-by-fundraiser/{fundraiserId}")
    public String getCampaignByFundraiserId(@PathVariable String fundraiserId, Model model) {
        List<Campaign> campaigns = campaignService.getCampaignByFundraiserId(fundraiserId);
        model.addAttribute("campaigns", campaigns);
        return "CampaignList";
    }

    @GetMapping("/get-by-id/{campaignId}")
    public String getCampaignByCampaignId(@PathVariable String campaignId, Model model) {
        Campaign campaign = campaignService.getCampaignByCampaignId(campaignId);
        model.addAttribute("campaign", campaign);
        return "CampaignDetail";
    }

    @PutMapping("/update-judul")
    public String updateCampaignJudul(@RequestBody Campaign campaign) {
        campaignService.updateCampaignJudul(campaign.getCampaignId(), campaign.getJudul());
        return "redirect:/campaign/get-by-id/" + campaign.getCampaignId();
    }

    @PutMapping("/update-target")
    public String updateCampaignTarget(@RequestBody Campaign campaign) {
        campaignService.updateCampaignTarget(campaign.getCampaignId(), campaign.getTarget());
        return "redirect:/campaign/get-by-id/" + campaign.getCampaignId();
    }

    @PutMapping("/update-deskripsi")
    public String updateCampaignDeskripsi(@RequestBody Campaign campaign) {
        campaignService.updateCampaignDeskripsi(campaign.getCampaignId(), campaign.getDeskripsi());
        return "redirect:/campaign/get-by-id/" + campaign.getCampaignId();
    }

    @PostMapping("/{campaignId}/delete")
    public String deleteCampaign(@PathVariable String campaignId) {
        campaignService.deleteCampaign(campaignId);
        return "redirect:/";
    }
}