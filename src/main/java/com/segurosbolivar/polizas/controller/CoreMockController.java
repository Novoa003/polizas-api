package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.dto.CoreEventoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/core-mock")
@Slf4j
public class CoreMockController {

    @PostMapping("/evento")
    public ResponseEntity<Void> recibirEvento(@RequestBody CoreEventoDTO evento) {
        log.info("[CORE-MOCK] Evento recibido -> evento: {}, polizaId: {}",
                evento.getEvento(), evento.getPolizaId());
        return ResponseEntity.ok().build();
    }
}