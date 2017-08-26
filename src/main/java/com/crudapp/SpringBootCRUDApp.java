package com.crudapp;

import com.crudapp.configuration.JpaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "com.crudapp")
@Import(JpaConfiguration.class)
public class SpringBootCRUDApp {

    public static void main( String[] args ) {
        SpringApplication.run(SpringBootCRUDApp.class, args);
    }

}
