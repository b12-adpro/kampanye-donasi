package id.ac.ui.cs.advprog.tk_adpro.enums;
import lombok.Getter;

@Getter
public enum CampaignStatus {
    PENDING("PENDING"),
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String value;

    CampaignStatus(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (CampaignStatus status : CampaignStatus.values()) {
            if (status.getValue().equals(param)) return true;
        }
        return false;
    }
}