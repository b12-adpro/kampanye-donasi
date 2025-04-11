package id.ac.ui.cs.advprog.tk_adpro.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class Campaign {
    private String campaignId;
    private String fundraiserId;
    private String judul;
    private String status;
    private LocalDateTime datetime;
    private int target;
    private String deskripsi;

    public Campaign(String campaignId, String fundraiserId, String judul, String status, LocalDateTime datetime, int target, String deskripsi) {
    }

    public Campaign(String campaignId, String fundraiserId, String judul, String status, LocalDateTime datetime, int target) {
    }
}
