package com.renato.financas.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinancasEmailApiApplication {

    public static void main(String[] args) {
        // Sobe o servidor na porta configurada em application.properties (padrão: 8080)
        // O login.js do app Electron já espera http://localhost:8080/api
        SpringApplication.run(FinancasEmailApiApplication.class, args);
    }
}
