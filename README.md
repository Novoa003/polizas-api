# API de Gestión de Pólizas

API REST para la gestión de pólizas de arrendamiento (Individuales y Colectivas). Implementa las reglas de negocio del Módulo 2: crear, listar, renovar, cancelar pólizas y riesgos, con integración simulada al CORE legado.

---

## Tabla de contenido

1. Tecnologías
2. Instalación y ejecución
3. Seguridad
4. Endpoints
5. Reglas de negocio
6. Integración con el CORE (mock)
7. Ejemplos rápidos
8. Manejo de errores
9. Estructura del proyecto

---

## Tecnologías

| Tecnología | Versión |
|------------|---------|
| Java | 24 |
| Spring Boot | 4.1.1 |
| Spring Data JPA + Hibernate | (incluido) |
| H2 Database | (en memoria) |
| Lombok | 1.18.38 |
| Maven | 3.9+ |

---

## Instalación y ejecución

### Requisitos

- Java 24+
- Maven 3.9+

### Pasos

```bash
git clone https://github.com/Novoa003/polizas-api.git
cd polizas-api
mvn clean install
mvn spring-boot:run
```

La API queda disponible en: `http://localhost:8080`

Consola H2: `http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:polizasdb` - User: `sa` - Password: (vacío)

---

## Seguridad

Todos los endpoints (excepto `/core-mock/**` y `/h2-console/**`) requieren el header:

```
x-api-key: 123456
```

Sin el header, retorna `401 Unauthorized`.

La validación se realiza mediante el filtro `ApiKeyFilter` que extiende `OncePerRequestFilter`.

---

## Endpoints

| Método | Endpoint | Descripción | API Key |
|--------|----------|-------------|---------|
| GET | `/polizas?tipo=&estado=` | Lista pólizas filtrando por tipo y/o estado | Sí |
| POST | `/polizas` | Crea una póliza | Sí |
| POST | `/polizas/{id}/renovar` | Renueva (canon y prima +IPC) | Sí |
| POST | `/polizas/{id}/cancelar` | Cancela póliza y todos sus riesgos | Sí |
| GET | `/polizas/{id}/riesgos` | Lista riesgos de una póliza | Sí |
| POST | `/polizas/{id}/riesgos` | Agrega riesgo (solo COLECTIVA) | Sí |
| POST | `/riesgos/{id}/cancelar` | Cancela un riesgo | Sí |
| POST | `/core-mock/evento` | Mock del CORE (logs) | No |

---

## Reglas de negocio

| # | Regla |
|---|-------|
| 1 | Una póliza individual solo puede tener 1 riesgo |
| 2 | No se puede renovar una póliza cancelada |
| 3 | La cancelación de una póliza cancela todos sus riesgos |
| 4 | Agregar riesgo exige que la póliza sea COLECTIVA |
| 5 | Prima = canon x meses de vigencia |
| 6 | Renovación incrementa canon y prima según IPC (5% por defecto) |

El IPC se configura en `application.yml`:

```yaml
app:
  negocio:
    ipc: 0.05
```

---

## Integración con el CORE (mock)

Cada operación de escritura dispara un evento al CORE vía `CoreIntegrationService`:

| Acción | Evento |
|--------|--------|
| Crear póliza | `CREACION` |
| Renovar / modificar | `ACTUALIZACION` |
| Cancelar póliza | `CANCELACION` |

El mock registra en logs el evento recibido. En producción, este adapter apuntaría al servicio agnóstico de edición en WebLogic.

---

## Ejemplos rápidos

### Crear póliza individual

```bash
curl -X POST http://localhost:8080/polizas \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "numeroPoliza": "POL-IND-001",
    "tipo": "INDIVIDUAL",
    "tomador": "Juan Perez",
    "asegurado": "Juan Perez",
    "beneficiario": "Maria Lopez",
    "fechaInicioVigencia": "2026-01-01",
    "fechaFinVigencia": "2026-07-01",
    "canonMensual": 1500000,
    "mesesVigencia": 6
  }'
```

### Renovar

```bash
curl -X POST http://localhost:8080/polizas/1/renovar \
  -H "x-api-key: 123456"
```

### Cancelar póliza (cancela todos sus riesgos)

```bash
curl -X POST http://localhost:8080/polizas/1/cancelar \
  -H "x-api-key: 123456"
```

### Agregar riesgo a póliza colectiva

```bash
curl -X POST http://localhost:8080/polizas/2/riesgos \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Arriendo Apt 301",
    "direccionInmueble": "Calle 100 #15-30",
    "arrendatario": "Pedro Gomez",
    "arrendador": "Inmobiliaria XYZ"
  }'
```

### Mock del CORE

```bash
curl -X POST http://localhost:8080/core-mock/evento \
  -H "Content-Type: application/json" \
  -d '{"evento":"ACTUALIZACION","polizaId":555}'
```

---

## Manejo de errores

Todos los errores se manejan con `GlobalExceptionHandler` (`@RestControllerAdvice`):

| Excepción | HTTP | Ejemplo |
|-----------|------|---------|
| `BusinessException` | 400 | `"No se puede renovar una póliza cancelada"` |
| `MethodArgumentNotValidException` | 400 | Validaciones de campos |
| `Exception` | 500 | Errores inesperados |

Ejemplo de respuesta de error de negocio:

```json
{ "error": "Solo se pueden agregar riesgos a pólizas de tipo COLECTIVA" }
```

---

## Estructura del proyecto

```
src/main/java/com/segurosbolivar/polizas/
├── config/         ApiKeyFilter
├── controller/     PolizaController, RiesgoController, CoreMockController
├── dto/            Request/Response DTOs
├── entity/         Poliza, Riesgo, Enums
├── exception/      BusinessException, GlobalExceptionHandler
├── repository/     PolizaRepository, RiesgoRepository
└── service/        PolizaService, RiesgoService, CoreIntegrationService
```
