package org.acquirer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AcquirerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AcquirerServiceApplication.class, args);
    }
}