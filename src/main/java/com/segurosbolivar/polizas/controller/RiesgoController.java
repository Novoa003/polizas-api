package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.dto.RiesgoRequestDTO;
import com.segurosbolivar.polizas.dto.RiesgoResponseDTO;
import com.segurosbolivar.polizas.service.RiesgoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RiesgoController {

    private final RiesgoService riesgoService;

    @GetMapping("/polizas/{id}/riesgos")
    public ResponseEntity<List<RiesgoResponseDTO>> listar(@PathVariable Long id) {
        return ResponseEntity.ok(riesgoService.listarPorPoliza(id));
    }

    @PostMapping("/polizas/{id}/riesgos")
    public ResponseEntity<RiesgoResponseDTO> agregar(
            @PathVariable Long id,
            @Valid @RequestBody RiesgoRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(riesgoService.agregar(id, req));
    }

    @PostMapping("/riesgos/{id}/cancelar")
    public ResponseEntity<RiesgoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(riesgoService.cancelar(id));
    }
}