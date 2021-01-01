package com.sec.lending.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.sec.lending.marketplace")
@EntityScan("com.sec.lending.marketplace")
public class MarketplaceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketplaceApplication.class, args);
    }
}
