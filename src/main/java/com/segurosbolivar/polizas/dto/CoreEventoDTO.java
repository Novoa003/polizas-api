package com.segurosbolivar.polizas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class CoreEventoDTO {
    private String evento;
    private Long polizaId;
}
