package id.ac.ui.cs.advprog.tk_adpro.enums;

import lombok.Getter;

@Getter
public enum DonationStatus {
    PENDING("PENDING"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED");

    private final String value;

    DonationStatus(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (DonationStatus status : DonationStatus.values()) {
            if (status.getValue().equals(param)) return true;
        }
        return false;
    }
}