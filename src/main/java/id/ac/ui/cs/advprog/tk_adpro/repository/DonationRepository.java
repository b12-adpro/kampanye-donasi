package id.ac.ui.cs.advprog.tk_adpro.repository;

import id.ac.ui.cs.advprog.tk_adpro.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DonationRepository extends JpaRepository<Donation, UUID> {
    List<Donation> findByCampaignId(UUID campaignId);
    List<Donation> findByDonaturId(UUID donaturId);

    @Query("SELECT d FROM Donation d WHERE d.campaignId = :campaignId AND d.status = :status")
    List<Donation> findByCampaignIdAndStatusNamed(@Param("campaignId") UUID campaignId, @Param("status") String status);
}