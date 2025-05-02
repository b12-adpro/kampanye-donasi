package id.ac.ui.cs.advprog.tk_adpro.controller;

import id.ac.ui.cs.advprog.tk_adpro.enums.DonationStatus;
import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import id.ac.ui.cs.advprog.tk_adpro.service.DonationService;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/donation")
public class DonationController {
    @Autowired
    private DonationService donationService;

    @GetMapping("/{campaignId}/donate")
    public String createDonatePage(@PathVariable String campaignId, Model model) {
        Donation donation = new Donation(
            null,
            campaignId,
            123L,
            0,
            DonationStatus.PENDING.getValue(),
            LocalDateTime.now()
        );
        model.addAttribute("donation", donation);
        return "DonationForm";
    }

    @PostMapping("/{campaignId}/donate")
    public String verifyDonation(@PathVariable String campaignId, @ModelAttribute("donation") Donation donation) {
        donationService.createDonation(donation);
        return "VerifyDonation";
    }

    @PostMapping("/{donationId}/create-donation")
    public String createDonation(@PathVariable String donationId, @ModelAttribute Donation donation) {
        donationService.createDonation(donation);
        return "redirect:/donation/get-by-id/" + donationId;
    }

    @PutMapping("/{donationId}/complete")
    public String completeDonation(@PathVariable String donationId) {
        donationService.completeDonation(donationId);
        return "redirect:/donation/get-by-id/" + donationId;
    }

    @PutMapping("/{donationId}/cancel")
    public String cancelDonation(@PathVariable String donationId) {
        donationService.cancelDonation(donationId);
        return "redirect:/donation/get-by-id/" + donationId;
    }

    @GetMapping("/get-by-id/{donationId}")
    public String getDonationByDonationId(@PathVariable String donationId, Model model) {
        Donation donation = donationService.getDonationByDonationId(donationId);
        model.addAttribute("donation", donation);
        return "DonationDetail";
    }

    @GetMapping("/get-by-campaign/{campaignId}")
    public String getDonationByCampaignId(@PathVariable String campaignId, Model model) {
        List<Donation> donations = donationService.getDonationsByCampaignId(campaignId);
        model.addAttribute("donations", donations);
        return "DonationList";
    }

    @PutMapping("/update-message")
    public String updateDonationMessage(@RequestBody Donation donation) {
        donationService.updateDonationMessage(donation.getDonationId(), donation.getMessage());
        return "redirect:/donation/get-by-id/" + donation.getDonationId();
    }

    @PutMapping("/delete-message")
    public String deleteDonationMessage(@RequestBody Donation donation) {
        donationService.deleteDonationMessage(donation.getDonationId());
        return "redirect:/donation/get-by-id/" + donation.getDonationId();
    }
}