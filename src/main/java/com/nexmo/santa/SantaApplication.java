package com.nexmo.santa;

import com.nexmo.client.NexmoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SantaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SantaApplication.class, args);
    }

    @Bean
    public NexmoClient getNexmoClient(@Value("${nexmo.api.key}") String key, @Value("${nexmo.api.secret}") String secret) {
        return new NexmoClient.Builder().apiKey(key).apiSecret(secret).build();
    }
}
