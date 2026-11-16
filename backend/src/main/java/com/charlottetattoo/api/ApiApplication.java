package com.charlottetattoo.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Escludiamo UserDetailsServiceAutoConfiguration: crea un utente "user" con una password casuale
// che stampa nel log, ma a noi non serve perché il login lo gestiamo con i JWT.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
