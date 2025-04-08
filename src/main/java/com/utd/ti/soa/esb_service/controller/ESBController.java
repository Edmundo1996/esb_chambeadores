package com.utd.ti.soa.esb_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.utd.ti.soa.esb_service.model.Client;
import com.utd.ti.soa.esb_service.model.User;
import com.utd.ti.soa.esb_service.model.Product;
import com.utd.ti.soa.esb_service.model.Descuento;
import com.utd.ti.soa.esb_service.utils.Auth;

@RestController
@RequestMapping("/esb")
public class ESBController {
    private final WebClient webClient = WebClient.create();
    private final Auth auth = new Auth();

    // Variables de entorno para las URLs de los servicios
    @Value("${USERS_SERVICE_URL}")
    private String usersServiceUrl;

    @Value("${CLIENTS_SERVICE_URL}")
    private String clientsServiceUrl;

    @Value("${PRODUCTS_SERVICE_URL}")
    private String productsServiceUrl;

    @Value("${DISCOUNTS_SERVICE_URL}")
    private String discountsServiceUrl;

    // ========================= USUARIOS =========================

    @PostMapping("/user/login")
    public ResponseEntity loginUser(@RequestBody User user) {
        String response = webClient.post()
            .uri(usersServiceUrl + "/app/users/login")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user")
    public ResponseEntity createUser(@RequestBody User user, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.post()
            .uri(usersServiceUrl + "/app/users/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity getUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.get()
            .uri(usersServiceUrl + "/app/users/all")
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/user/update/{id}")
    public ResponseEntity updateUser(@PathVariable String id, @RequestBody User user, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.patch()
            .uri(usersServiceUrl + "/app/users/update/" + id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.delete()
            .uri(usersServiceUrl + "/app/users/delete/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/reset-password")
    public ResponseEntity resetPassword(@RequestBody User user) {
        String response = webClient.post()
            .uri(usersServiceUrl + "/app/users/reset-password")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    // ========================= CLIENTES =========================

    @PostMapping("/client")
    public ResponseEntity createClient(@RequestBody Client client, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.post()
            .uri(clientsServiceUrl + "/app/clients/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(client))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client")
    public ResponseEntity getClients(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.get()
            .uri(clientsServiceUrl + "/app/clients/all")
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/client/update/{id}")
    public ResponseEntity updateClient(@PathVariable String id, @RequestBody Client client, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        String response = webClient.patch()
            .uri(clientsServiceUrl + "/app/clients/update/" + id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(client))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/client/delete/{id}")
    public ResponseEntity deleteClient(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.delete()
            .uri(clientsServiceUrl + "/app/clients/delete/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    // ========================= PRODUCTOS =========================

    @PostMapping("/product")
    public ResponseEntity createProduct(@RequestBody Product product, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.post()
            .uri(productsServiceUrl + "/app/products/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(product))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product")
    public ResponseEntity getProducts(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.get()
            .uri(productsServiceUrl + "/app/products/all")
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/product/update/{id}")
    public ResponseEntity updateProduct(@PathVariable String id, @RequestBody Product product, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.patch()
            .uri(productsServiceUrl + "/app/products/update/" + id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(product))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/product/delete/{id}")
    public ResponseEntity deleteProduct(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.delete()
            .uri(productsServiceUrl + "/app/products/delete/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    // ========================= DESCUENTOS =========================

    @PostMapping("/descuento")
    public ResponseEntity createDescuento(@RequestBody Descuento descuento, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.post()
            .uri(discountsServiceUrl + "/app/descuentos/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(descuento))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/descuento")
    public ResponseEntity getDescuentos(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.get()
            .uri(discountsServiceUrl + "/app/descuentos/all")
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/descuento/update/{id}")
    public ResponseEntity updateDescuento(@PathVariable String id, @RequestBody Descuento descuento, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.patch()
            .uri(discountsServiceUrl + "/app/descuentos/update/" + id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(descuento))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/descuento/delete/{id}")
    public ResponseEntity deleteDescuento(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) return ResponseEntity.status(401).body("Token inválido o expirado");
        if (!"admin".equals(auth.getRoleFromToken(token))) return ResponseEntity.status(403).body("No tienes permisos");
        String response = webClient.delete()
            .uri(discountsServiceUrl + "/app/descuentos/delete/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/descuento/apply")
    public ResponseEntity applyDescuento(@RequestBody Descuento descuento, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (token == null || token.isEmpty()) {
            System.err.println("El token no fue proporcionado o está vacío.");
            return ResponseEntity.status(400).body("El token no fue proporcionado");
        }
        if (!auth.validateToken(token)) {
            System.err.println("Token inválido o expirado.");
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        String role = auth.getRoleFromToken(token);
        if (!"admin".equals(role) && !"client".equals(role)) {
            System.err.println("Rol no autorizado: " + role);
            return ResponseEntity.status(403).body("No tienes permisos para realizar esta acción");
        }

        try {
            String response = webClient.post()
                .uri(discountsServiceUrl + "/app/descuentos/apply")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, token) // Pasar el token al servicio externo
                .body(BodyInserters.fromValue(descuento))
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno al procesar la solicitud");
        }
    }
}