package com.utd.ti.soa.esb_service.utils;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class Auth {
    // Clave secreta del JWT (no segura, codificada directamente)
    private final String SECRET_KEY = "aJksd9QzPl+sVdK7vYc/L4dK8HgQmPpQ5K9yApUsj3w=";

    // Credenciales de la base de datos leídas desde variables de entorno
    private final String DB_URL = System.getenv("DB_URL");
    private final String DB_USER = System.getenv("DB_USER");
    private final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    // LOGIN: valida credenciales, obtiene rol y genera token
    public String loginAndGenerateToken(String username, String password) {
        String query = "SELECT rol FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String rol = resultSet.getString("rol");
                if (rol != null && (rol.equals("admin") || rol.equals("cliente"))) {
                    return generateToken(username, rol);
                } else {
                    System.out.println("Rol no válido para el usuario.");
                }
            } else {
                System.out.println("Credenciales inválidas.");
            }

        } catch (Exception e) {
            System.out.println("Error al intentar iniciar sesión: " + e.getMessage());
        }

        return null;
    }

    // Validar token
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace("Bearer", "").trim())
                .getBody();

            String username = claims.getSubject();
            String rol = claims.get("rol", String.class);

            if (username == null || username.isEmpty() || rol == null) {
                System.out.println("Token inválido: falta username o rol.");
                return false;
            }

            // Verificar que el rol del token coincida con el de la BD
            String dbRol = getRoleFromDatabase(username);
            if (dbRol != null && !dbRol.equals(rol)) {
                System.out.println("El rol del token no coincide con el de la base de datos.");
                return false;
            }

            System.out.println("Token válido, usuario: " + username + ", rol: " + rol);
            return true;

        } catch (Exception e) {
            System.out.println("Error al validar el Token: " + e.getMessage());
            return false;
        }
    }

    // Obtener rol del token
    public String getRoleFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace("Bearer", "").trim())
                .getBody();

            String rol = claims.get("rol", String.class);
            System.out.println("Rol obtenido del token: " + rol);
            return rol;

        } catch (Exception e) {
            System.out.println("Error al obtener el rol del Token: " + e.getMessage());
            return null;
        }
    }

    // Obtener rol desde la BD
    public String getRoleFromDatabase(String username) {
        String rol = null;
        String query = "SELECT rol FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                rol = resultSet.getString("rol");
                System.out.println("Rol obtenido de la base de datos: " + rol);
            } else {
                System.out.println("No se encontró el usuario en la base de datos.");
            }

        } catch (Exception e) {
            System.out.println("Error al consultar la base de datos: " + e.getMessage());
        }

        return rol;
    }

    // Generar token
    public String generateToken(String username, String rol) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            String token = Jwts.builder()
                .setSubject(username)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
                .signWith(key)
                .compact();

            System.out.println("Token generado: " + token);
            return token;

        } catch (Exception e) {
            System.out.println("Error al generar el Token: " + e.getMessage());
            return null;
        }
    }
}