package id.ac.ui.cs.advprog.tk_adpro.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DonationStatusTest {
    @Test
    void testEnumValues() {
        assertEquals(3, DonationStatus.values().length);
        assertNotNull(DonationStatus.valueOf("PENDING"));
        assertNotNull(DonationStatus.valueOf("CANCELLED"));
        assertNotNull(DonationStatus.valueOf("COMPLETED"));
    }

    @Test
    void testGetValue() {
        assertEquals("PENDING", DonationStatus.PENDING.getValue());
        assertEquals("CANCELLED", DonationStatus.CANCELLED.getValue());
        assertEquals("COMPLETED", DonationStatus.COMPLETED.getValue());
    }

    @Test
    void testContainsMethod_WithValidValues() {
        assertTrue(DonationStatus.contains("PENDING"));
        assertTrue(DonationStatus.contains("CANCELLED"));
        assertTrue(DonationStatus.contains("COMPLETED"));
    }

    @Test
    void testContainsMethod_WithInvalidValues() {
        assertFalse(DonationStatus.contains("REJECTED"));
        assertFalse(DonationStatus.contains("IN_PROGRESS"));
        assertFalse(DonationStatus.contains(""));
        assertFalse(DonationStatus.contains(null));
    }

    @Test
    void testContainsMethod_CaseSensitivity() {
        assertFalse(DonationStatus.contains("pending"));
        assertFalse(DonationStatus.contains("Pending"));
        assertFalse(DonationStatus.contains("cancelled"));
        assertFalse(DonationStatus.contains("completed"));
    }
}