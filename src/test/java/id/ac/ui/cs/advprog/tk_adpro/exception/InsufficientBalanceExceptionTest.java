package id.ac.ui.cs.advprog.tk_adpro.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InsufficientBalanceExceptionTest {
    @Test
    void testExceptionMessage() {
        String errorMessage = "Insufficient balance for transaction.";
        InsufficientBalanceException exception = new InsufficientBalanceException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionMessageAndCause() {
        String errorMessage = "Insufficient balance for transaction.";
        Throwable cause = new RuntimeException("Underlying cause");
        InsufficientBalanceException exception = new InsufficientBalanceException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}