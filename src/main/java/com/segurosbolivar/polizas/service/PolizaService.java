package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.*;
import com.segurosbolivar.polizas.entity.*;
import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.repository.PolizaRepository;
import com.segurosbolivar.polizas.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final CoreIntegrationService coreIntegrationService;

    @Value("${app.negocio.ipc}")
    private Double ipc;

    // ---------- LISTAR ----------
    public List<PolizaResponseDTO> listar(TipoPoliza tipo, EstadoPoliza estado) {
        List<Poliza> polizas;
        if (tipo != null && estado != null) {
            polizas = polizaRepository.findByTipoAndEstado(tipo, estado);
        } else if (tipo != null) {
            polizas = polizaRepository.findByTipo(tipo);
        } else if (estado != null) {
            polizas = polizaRepository.findByEstado(estado);
        } else {
            polizas = polizaRepository.findAll();
        }
        return polizas.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ---------- CREAR ----------
    @Transactional
    public PolizaResponseDTO crear(PolizaRequestDTO req) {
        if (req.getFechaFinVigencia().isBefore(req.getFechaInicioVigencia())) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        double prima = req.getCanonMensual() * req.getMesesVigencia();

        Poliza poliza = Poliza.builder()
                .numeroPoliza(req.getNumeroPoliza())
                .tipo(req.getTipo())
                .estado(EstadoPoliza.ACTIVA)
                .tomador(req.getTomador())
                .asegurado(req.getAsegurado())
                .beneficiario(req.getBeneficiario())
                .fechaInicioVigencia(req.getFechaInicioVigencia())
                .fechaFinVigencia(req.getFechaFinVigencia())
                .canonMensual(req.getCanonMensual())
                .mesesVigencia(req.getMesesVigencia())
                .prima(prima)
                .fechaCreacion(LocalDate.now())
                .fechaActualizacion(LocalDate.now())
                .build();

        Poliza guardada = polizaRepository.save(poliza);
        coreIntegrationService.enviarEventoCore("CREACION", guardada.getId());
        log.info("Póliza creada: {}", guardada.getNumeroPoliza());
        return toDTO(guardada);
    }

    // ---------- RENOVAR ----------
    @Transactional
    public PolizaResponseDTO renovar(Long id) {
        Poliza poliza = polizaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Póliza no encontrada: " + id));

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No se puede renovar una póliza cancelada");
        }

        double nuevoCanon = poliza.getCanonMensual() * (1 + ipc);
        double nuevaPrima = nuevoCanon * poliza.getMesesVigencia();

        poliza.setCanonMensual(redondear(nuevoCanon));
        poliza.setPrima(redondear(nuevaPrima));
        poliza.setEstado(EstadoPoliza.RENOVADA);
        poliza.setFechaInicioVigencia(poliza.getFechaFinVigencia());
        poliza.setFechaFinVigencia(poliza.getFechaFinVigencia().plusMonths(poliza.getMesesVigencia()));
        poliza.setFechaActualizacion(LocalDate.now());

        Poliza actualizada = polizaRepository.save(poliza);
        coreIntegrationService.enviarEventoCore("ACTUALIZACION", actualizada.getId());
        log.info("Póliza renovada: {} | nuevo canon: {}", actualizada.getId(), actualizada.getCanonMensual());
        return toDTO(actualizada);
    }

    // ---------- CANCELAR ----------
    @Transactional
    public PolizaResponseDTO cancelar(Long id) {
        Poliza poliza = polizaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Póliza no encontrada: " + id));

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("La póliza ya está cancelada");
        }

        poliza.setEstado(EstadoPoliza.CANCELADA);
        poliza.setFechaActualizacion(LocalDate.now());

        poliza.getRiesgos().forEach(r -> r.setEstado(EstadoRiesgo.CANCELADO));

        Poliza actualizada = polizaRepository.save(poliza);
        coreIntegrationService.enviarEventoCore("CANCELACION", actualizada.getId());
        log.info("Póliza cancelada con todos sus riesgos: {}", actualizada.getId());
        return toDTO(actualizada);
    }

    // ---------- HELPERS ----------
    public Poliza obtenerEntidad(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Póliza no encontrada: " + id));
    }

    public PolizaResponseDTO toDTO(Poliza p) {
        List<RiesgoResponseDTO> riesgos = p.getRiesgos() == null ? List.of() :
                p.getRiesgos().stream().map(r -> RiesgoResponseDTO.builder()
                        .id(r.getId())
                        .polizaId(p.getId())
                        .descripcion(r.getDescripcion())
                        .direccionInmueble(r.getDireccionInmueble())
                        .arrendatario(r.getArrendatario())
                        .arrendador(r.getArrendador())
                        .estado(r.getEstado())
                        .build()).collect(Collectors.toList());

        return PolizaResponseDTO.builder()
                .id(p.getId())
                .numeroPoliza(p.getNumeroPoliza())
                .tipo(p.getTipo())
                .estado(p.getEstado())
                .tomador(p.getTomador())
                .asegurado(p.getAsegurado())
                .beneficiario(p.getBeneficiario())
                .fechaInicioVigencia(p.getFechaInicioVigencia())
                .fechaFinVigencia(p.getFechaFinVigencia())
                .canonMensual(p.getCanonMensual())
                .prima(p.getPrima())
                .mesesVigencia(p.getMesesVigencia())
                .riesgos(riesgos)
                .build();
    }

    private double redondear(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
