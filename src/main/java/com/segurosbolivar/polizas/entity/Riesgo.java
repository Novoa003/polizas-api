package com.segurosbolivar.polizas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "riesgos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;

    private String descripcion;
    private String direccionInmueble;
    private String arrendatario;
    private String arrendador;

    @Enumerated(EnumType.STRING)
    private EstadoRiesgo estado;

    private LocalDate fechaCreacion;
}
