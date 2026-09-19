package com.segurosbolivar.polizas.dto;

import com.segurosbolivar.polizas.entity.EstadoPoliza;
import com.segurosbolivar.polizas.entity.TipoPoliza;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data @Builder
public class PolizaResponseDTO {
    private Long id;
    private String numeroPoliza;
    private TipoPoliza tipo;
    private EstadoPoliza estado;
    private String tomador;
    private String asegurado;
    private String beneficiario;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private Double canonMensual;
    private Double prima;
    private Integer mesesVigencia;
    private List<RiesgoResponseDTO> riesgos;
}
