package com.treasury.kpstreasury;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableKafka
@EnableAsync
@EnableTransactionManagement
public class KpStreasuryApplication {
    public static void main(String[] args) {
        SpringApplication.run(KpStreasuryApplication.class, args);
    }



}
