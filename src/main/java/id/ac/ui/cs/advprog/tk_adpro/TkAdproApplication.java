package id.ac.ui.cs.advprog.tk_adpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TkAdproApplication {
    public static void main(String[] args) {
        SpringApplication.run(TkAdproApplication.class, args);
    }
}