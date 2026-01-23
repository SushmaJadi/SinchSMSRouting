package com.sinch.sms.routing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.sinch.sms.routing")
@EnableJpaRepositories
@EntityScan
@EnableTransactionManagement
public class SMSRoutingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SMSRoutingApplication.class, args);
    }

}
