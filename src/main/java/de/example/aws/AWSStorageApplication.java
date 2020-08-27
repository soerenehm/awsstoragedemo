package de.example.aws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class AWSStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(AWSStorageApplication.class, args);
    }
}
