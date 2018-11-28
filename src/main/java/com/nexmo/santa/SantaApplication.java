package com.nexmo.santa;

import com.nexmo.client.NexmoClient;
import com.nexmo.santa.location.SantaLocationLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class SantaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SantaApplication.class, args);
    }

    @Autowired
    private SantaLocationLookupService santaLocationLookupService;

    @Bean
    public NexmoClient getNexmoClient(@Value("${nexmo.api.key}") String key, @Value("${nexmo.api.secret}") String secret) {
        return new NexmoClient.Builder().apiKey(key).apiSecret(secret).build();
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        santaLocationLookupService.seedLocations();
    }

}
