package id.ac.ui.cs.advprog.tk_adpro.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CampaignStatusTest {
    @Test
    void testEnumValues() {
        assertEquals(2, CampaignStatus.values().length);
        assertNotNull(CampaignStatus.valueOf("ACTIVE"));
        assertNotNull(CampaignStatus.valueOf("INACTIVE"));
    }

    @Test
    void testGetValue() {
        assertEquals("ACTIVE", CampaignStatus.ACTIVE.getValue());
        assertEquals("INACTIVE", CampaignStatus.INACTIVE.getValue());
    }

    @Test
    void testContainsMethod_WithValidValues() {
        assertTrue(CampaignStatus.contains("ACTIVE"));
        assertTrue(CampaignStatus.contains("INACTIVE"));
    }

    @Test
    void testContainsMethod_WithInvalidValues() {
        assertFalse(CampaignStatus.contains("REJECTED"));
        assertFalse(CampaignStatus.contains("IN_PROGRESS"));
        assertFalse(CampaignStatus.contains(""));
        assertFalse(CampaignStatus.contains(null));
    }

    @Test
    void testContainsMethod_CaseSensitivity() {
        assertFalse(CampaignStatus.contains("active"));
        assertFalse(CampaignStatus.contains("Active"));
        assertFalse(CampaignStatus.contains("inactive"));
        assertFalse(CampaignStatus.contains("Inactive"));
    }
}