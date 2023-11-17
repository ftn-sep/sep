package org.paypal;

import org.sep.exceptions.ExceptionResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@Import(ExceptionResolver.class)
public class PaypalServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaypalServiceApplication.class, args);
    }
}