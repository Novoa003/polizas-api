package com.segurosbolivar.polizas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RiesgoRequestDTO {
    @NotBlank
    private String descripcion;
    @NotBlank
    private String direccionInmueble;
    private String arrendatario;
    private String arrendador;
}
