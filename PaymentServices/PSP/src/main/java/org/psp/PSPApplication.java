package org.psp;

import org.sep.exceptions.ExceptionResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@Import(ExceptionResolver.class)
@EnableFeignClients
public class PSPApplication {
    public static void main(String[] args) {
        SpringApplication.run(PSPApplication.class, args);
    }
}