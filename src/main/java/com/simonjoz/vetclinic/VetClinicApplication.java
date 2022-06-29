package com.simonjoz.vetclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class VetClinicApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetClinicApplication.class, args);
    }

}
