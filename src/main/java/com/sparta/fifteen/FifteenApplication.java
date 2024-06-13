package com.sparta.fifteen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FifteenApplication {

    public static void main(String[] args) {
        SpringApplication.run(FifteenApplication.class, args);
    }

}
