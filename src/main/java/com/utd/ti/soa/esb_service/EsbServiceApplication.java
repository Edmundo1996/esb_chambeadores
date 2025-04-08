package com.utd.ti.soa.esb_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class EsbServiceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EsbServiceApplication.class, args);

        // Obtener el puerto desde el entorno
        Environment env = context.getEnvironment();
        String port = env.getProperty("server.port", "8080"); // Por defecto, usa el puerto 8080

        // Log para mostrar el puerto
        System.out.println("ESB corriendo en: http://localhost:" + port + "/esb");
    }
}