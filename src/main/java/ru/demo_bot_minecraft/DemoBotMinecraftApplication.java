package ru.demo_bot_minecraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoBotMinecraftApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoBotMinecraftApplication.class, args);
    }

}
