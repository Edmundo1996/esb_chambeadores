package com.utd.ti.soa.esb_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private Long id; // ID del producto
    private String nombre; // Nombre del producto
    private Double precio; // Precio del producto
    private String categorias; // Categorías del producto (separadas por comas)
}