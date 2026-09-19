package com.segurosbolivar.polizas.dto;

import com.segurosbolivar.polizas.entity.EstadoRiesgo;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RiesgoResponseDTO {
    private Long id;
    private Long polizaId;
    private String descripcion;
    private String direccionInmueble;
    private String arrendatario;
    private String arrendador;
    private EstadoRiesgo estado;
}
