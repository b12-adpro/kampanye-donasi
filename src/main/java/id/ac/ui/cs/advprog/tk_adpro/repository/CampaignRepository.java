package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    Campaign findByCampaignId(UUID campaignId);
    List<Campaign> findByFundraiserId(UUID fundraiserId);
    void deleteByCampaignId(UUID campaignId);
}