package id.ac.ui.cs.advprog.tk_adpro.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PaymentServiceExceptionTest {
    @Test
    void testExceptionMessage() {
        String errorMessage = "Payment service failed to process the request.";
        PaymentServiceException exception = new PaymentServiceException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }
}