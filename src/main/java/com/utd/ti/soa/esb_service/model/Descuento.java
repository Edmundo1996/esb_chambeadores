package com.utd.ti.soa.esb_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Descuento {
    private Long id; // ID del descuento
    private String descripcion; // Descripción del descuento
    private Double porcentaje_descuento; // Porcentaje de descuento (ej. 10.0 para 10%)
    private String fecha_inicio; // Fecha de inicio del descuento (formato: YYYY-MM-DD)
    private String fecha_fin; // Fecha de fin del descuento (formato: YYYY-MM-DD)
    private Boolean estatus; // Estado del descuento (activo/inactivo)

    // Campos necesarios para aplicar el descuento
    private String productId; // ID del producto al que se aplicará el descuento
    private String descuentoId; // ID del descuento que se aplicará
}