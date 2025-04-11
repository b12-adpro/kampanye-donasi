package id.ac.ui.cs.advprog.tk_adpro;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@SpringBootTest
class TkAdproApplicationTests {
    @Test
    void testMainMethod() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            mockedSpringApplication.when(() -> SpringApplication.run(
                TkAdproApplication.class, new String[]{}
            )).thenReturn(null);

            TkAdproApplication.main(new String[]{});

            mockedSpringApplication.verify(() -> SpringApplication.run(TkAdproApplication.class, new String[]{}));
        }
    }
}