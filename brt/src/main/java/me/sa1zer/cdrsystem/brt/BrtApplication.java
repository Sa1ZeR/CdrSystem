package me.sa1zer.cdrsystem.brt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "me.sa1zer")
public class BrtApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrtApplication.class, args);
    }

}
