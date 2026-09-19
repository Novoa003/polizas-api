package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.CoreEventoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class CoreIntegrationService {

    @Value("${app.core.mock-url}")
    private String coreMockUrl;

    private final RestClient restClient = RestClient.create();

    /**
     * Simula el envío de un evento al servicio agnóstico de edición (WebLogic)
     * que a su vez actualiza el CORE de seguros.
     */
    public void enviarEventoCore(String evento, Long polizaId) {
        CoreEventoDTO dto = new CoreEventoDTO(evento, polizaId);
        try {
            restClient.post()
                    .uri(coreMockUrl)
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Evento enviado al CORE: {}", dto);
        } catch (Exception e) {
            log.error("Error enviando evento al CORE (se reintentará en producción): {}", e.getMessage());
        }
    }
}
