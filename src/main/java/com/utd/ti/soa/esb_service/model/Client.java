package com.utd.ti.soa.esb_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Client {
    private String nombre;
    private String apellido_paterno;
    private String apellido_materno;
    private String correo;
    private String telefono;
    private String fecha_nacimiento;
    private String direccion;
}
