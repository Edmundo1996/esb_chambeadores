package com.utd.ti.soa.esb_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.ReactorClientHttpConnector;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;

import com.utd.ti.soa.esb_service.model.Client;
import com.utd.ti.soa.esb_service.model.User;
import com.utd.ti.soa.esb_service.model.Product;
import com.utd.ti.soa.esb_service.model.Descuento;
import com.utd.ti.soa.esb_service.utils.Auth;

@RestController
@RequestMapping("/esb")
public class ESBController {

    private final WebClient webClient;
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

    // Constructor para inicializar el WebClient con soporte HTTPS
    public ESBController() throws SSLException {
        HttpClient httpClient = HttpClient.create()
            .secure(sslContextSpec -> {
                try {
                    sslContextSpec.sslContext(SslContextBuilder.forClient().build());
                } catch (SSLException e) {
                    throw new RuntimeException("Error configurando SSL", e);
                }
            });

        this.webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

    // ========================= MÉTODOS AUXILIARES =========================

    private Mono<ResponseEntity<String>> handleError(Throwable e) {
        return Mono.just(ResponseEntity.status(500).body("Error interno: " + e.getMessage()));
    }

    private boolean isAdmin(String token) {
        return "admin".equals(auth.getRoleFromToken(token));
    }

    private boolean isAuthorized(String token) {
        return auth.validateToken(token);
    }

    private ResponseEntity<String> validateToken(String token, boolean requireAdmin) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(400).body("El token no fue proporcionado");
        }
        if (!isAuthorized(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }
        if (requireAdmin && !isAdmin(token)) {
            return ResponseEntity.status(403).body("No tienes permisos para realizar esta acción");
        }
        return null; // Token válido
    }

    // ========================= USUARIOS =========================

    @PostMapping("/user/login")
    public Mono<ResponseEntity<String>> loginUser(@RequestBody User user) {
        return webClient.post()
            .uri(usersServiceUrl + "/app/users/login")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .map(ResponseEntity::ok)
            .onErrorResume(this::handleError);
    }

    @PostMapping("/user")
    public Mono<ResponseEntity<String>> createUser(@RequestBody User user, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        ResponseEntity<String> validationResponse = validateToken(token, true);
        if (validationResponse != null) return Mono.just(validationResponse);

        return webClient.post()
            .uri(usersServiceUrl + "/app/users/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .map(ResponseEntity::ok)
            .onErrorResume(this::handleError);
    }

    @GetMapping("/user")
    public Mono<ResponseEntity<String>> getUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        ResponseEntity<String> validationResponse = validateToken(token, true);
        if (validationResponse != null) return Mono.just(validationResponse);

        return webClient.get()
            .uri(usersServiceUrl + "/app/users/all")
            .retrieve()
            .bodyToMono(String.class)
            .map(ResponseEntity::ok)
            .onErrorResume(this::handleError);
    }

    @PatchMapping("/user/update/{id}")
    public Mono<ResponseEntity<String>> updateUser(@PathVariable String id, @RequestBody User user, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        ResponseEntity<String> validationResponse = validateToken(token, true);
        if (validationResponse != null) return Mono.just(validationResponse);

        return webClient.patch()
            .uri(usersServiceUrl + "/app/users/update/" + id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(user))
            .retrieve()
            .bodyToMono(String.class)
            .map(ResponseEntity::ok)
            .onErrorResume(this::handleError);
    }

    @DeleteMapping("/user/delete/{id}")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        ResponseEntity<String> validationResponse = validateToken(token, true);
        if (validationResponse != null) return Mono.just(validationResponse);

        return webClient.delete()
            .uri(usersServiceUrl + "/app/users/delete/" + id)
            .retrieve()
            .bodyToMono(String.class)
            .map(ResponseEntity::ok)
            .onErrorResume(this::handleError);
    }

    // ========================= CLIENTES =========================

    @PostMapping("/client")
    public Mono<ResponseEntity<String>> createClient(@RequestBody Client client, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        ResponseEntity<String> validationResponse = validateToken(token, true);
        if (validationResponse != null) return Mono.just(validationResponse);

        return webClient.post()
            .uri(clientsServiceUrl + "/app/clients/create")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(client))
            .retrieve()
            .bodyToMono(String.class)
            .map(ResponseEntity::ok)
            .onErrorResume(this::handleError);
    }

    // ========================= OTROS MÉTODOS =========================
    // Aplica la misma lógica para los demás métodos (productos, descuentos, etc.)
}