package com.assistant.centralservicespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CentralServiceSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CentralServiceSpringApplication.class, args);
    }

}
