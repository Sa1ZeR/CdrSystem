package me.sa1zer.cdrsystem.cdr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "me.sa1zer")
public class CdrApplication {

    public static void main(String[] args) {
        SpringApplication.run(CdrApplication.class, args);
    }

}
