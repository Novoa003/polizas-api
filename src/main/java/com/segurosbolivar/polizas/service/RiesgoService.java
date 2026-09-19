package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.RiesgoRequestDTO;
import com.segurosbolivar.polizas.dto.RiesgoResponseDTO;
import com.segurosbolivar.polizas.entity.*;
import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final PolizaService polizaService;
    private final CoreIntegrationService coreIntegrationService;

    public List<RiesgoResponseDTO> listarPorPoliza(Long polizaId) {
        polizaService.obtenerEntidad(polizaId);
        return riesgoRepository.findByPolizaId(polizaId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public RiesgoResponseDTO agregar(Long polizaId, RiesgoRequestDTO req) {
        Poliza poliza = polizaService.obtenerEntidad(polizaId);

        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new BusinessException("Solo se pueden agregar riesgos a pólizas de tipo COLECTIVA");
        }

        if (poliza.getTipo() == TipoPoliza.INDIVIDUAL) {
            long activos = riesgoRepository.countByPolizaIdAndEstado(polizaId, EstadoRiesgo.ACTIVO);
            if (activos >= 1) {
                throw new BusinessException("Una póliza individual solo puede tener 1 riesgo");
            }
        }

        Riesgo riesgo = Riesgo.builder()
                .poliza(poliza)
                .descripcion(req.getDescripcion())
                .direccionInmueble(req.getDireccionInmueble())
                .arrendatario(req.getArrendatario())
                .arrendador(req.getArrendador())
                .estado(EstadoRiesgo.ACTIVO)
                .fechaCreacion(LocalDate.now())
                .build();

        Riesgo guardado = riesgoRepository.save(riesgo);
        coreIntegrationService.enviarEventoCore("ACTUALIZACION", polizaId);
        log.info("Riesgo agregado a póliza {}: {}", polizaId, guardado.getId());
        return toDTO(guardado);
    }

    @Transactional
    public RiesgoResponseDTO cancelar(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new BusinessException("Riesgo no encontrado: " + riesgoId));

        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new BusinessException("El riesgo ya está cancelado");
        }

        riesgo.setEstado(EstadoRiesgo.CANCELADO);
        Riesgo actualizado = riesgoRepository.save(riesgo);
        coreIntegrationService.enviarEventoCore("ACTUALIZACION", riesgo.getPoliza().getId());
        log.info("Riesgo cancelado: {}", riesgoId);
        return toDTO(actualizado);
    }

    private RiesgoResponseDTO toDTO(Riesgo r) {
        return RiesgoResponseDTO.builder()
                .id(r.getId())
                .polizaId(r.getPoliza().getId())
                .descripcion(r.getDescripcion())
                .direccionInmueble(r.getDireccionInmueble())
                .arrendatario(r.getArrendatario())
                .arrendador(r.getArrendador())
                .estado(r.getEstado())
                .build();
    }
}