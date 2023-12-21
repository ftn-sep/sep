package org.crypto;

import org.sep.exceptions.ExceptionResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@Import(ExceptionResolver.class)
@EnableScheduling
public class CryptoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CryptoServiceApplication.class, args);
    }
}