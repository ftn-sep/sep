package org.sep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IssuerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IssuerServiceApplication.class, args);
    }
}