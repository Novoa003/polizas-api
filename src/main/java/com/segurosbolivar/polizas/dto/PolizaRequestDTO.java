package com.segurosbolivar.polizas.dto;

import com.segurosbolivar.polizas.entity.TipoPoliza;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PolizaRequestDTO {

    @NotBlank
    private String numeroPoliza;

    @NotNull
    private TipoPoliza tipo;

    private String tomador;
    private String asegurado;
    private String beneficiario;

    @NotNull
    private LocalDate fechaInicioVigencia;

    @NotNull
    private LocalDate fechaFinVigencia;

    @NotNull @Positive
    private Double canonMensual;

    @NotNull @Positive
    private Integer mesesVigencia;
}