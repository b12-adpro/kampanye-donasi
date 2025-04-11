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
<<<<<<< HEAD
                    TkAdproApplication.class, new String[]{}
=======
                TkAdproApplication.class, new String[]{}
>>>>>>> b799220a2d7f5ab0352d7336ac22bda3ee1b27cd
            )).thenReturn(null);

            TkAdproApplication.main(new String[]{});

            mockedSpringApplication.verify(() -> SpringApplication.run(TkAdproApplication.class, new String[]{}));
        }
    }
}