# API de Gestión de Pólizas — Prueba Técnica Seguros Bolívar

API REST desarollada con el  objetivo es gestionar pólizas de arrendamiento de inmuebles (Individuales y Colectivas), aplicando reglas de negocio, integración simulada con el CORE legado y buenas prácticas de desarrollo.

---

## Tabla de contenido

1. [Contexto del negocio](#-contexto-del-negocio)
2. [Alcance de la solución](#-alcance-de-la-solución)
3. [Tecnologías utilizadas](#-tecnologías-utilizadas)
4. [Arquitectura del proyecto](#-arquitectura-del-proyecto)
5. [Modelo de dominio](#-modelo-de-dominio)
6. [Reglas de negocio implementadas](#-reglas-de-negocio-implementadas)
7. [Seguridad](#-seguridad)
8. [Integración con el CORE (mock)](#-integración-con-el-core-mock)
9. [Instalación y ejecución](#-instalación-y-ejecución)
10. [Endpoints disponibles](#-endpoints-disponibles)
11. [Ejemplos de uso (curl)](#-ejemplos-de-uso-curl)
12. [Manejo de errores](#-manejo-de-errores)
13. [Decisiones técnicas](#-decisiones-técnicas)
14. [Posibles mejoras futuras](#-posibles-mejoras-futuras)

---

##  Contexto del negocio

Seguros Bolívar maneja dos tipos de pólizas para arrendamiento de inmuebles:

| Tipo | Tomador | Asegurado | Beneficiario | Riesgos |
|------|---------|-----------|--------------|---------|
| **Individual** | Arrendatario | Arrendatario | Arrendador | 1 |
| **Colectiva** | Inmobiliaria / Administración de copropiedades | Arrendatarios | Arrendadores | 1 a N |

Todas las pólizas cuentan con:

- Periodo de vigencia (fecha inicio – fecha fin)
- Valor de canon mensual de arrendamiento
- Prima = canon mensual × número de meses de vigencia
- Renovación por el mismo periodo inicial, ajustando el canon según el IPC

---

##  Alcance de la solución

Esta prueba implementa **solo lo esencial** solicitado en el Módulo 2:

- Listar pólizas filtrando por tipo y estado.
- Listar riesgos de una póliza.
- Renovar una póliza (incremento de canon y prima por IPC).
- Cancelar una póliza (cancela todos sus riesgos).
- Agregar riesgos a pólizas colectivas.
- Cancelar riesgos individualmente.
- Mock del CORE que registra en logs los eventos enviados.

---

## 🛠 Tecnologías utilizadas

| Tecnología | Versión    | Propósito |
|------------|------------|-----------|
| Java | 24         | Lenguaje base |
| Spring Boot | 4.1.1      | Framework principal |
| Spring Web MVC | (incluido) | Exposición de endpoints REST |
| Spring Data JPA | (incluido) | Persistencia |
| Hibernate | (incluido) | ORM |
| H2 Database | (incluido) | Base de datos en memoria para pruebas |
| Lombok | (incluido) | Reducción de código boilerplate |
| Jakarta Validation | (incluido) | Validación de DTOs |
| Maven | 3.9+       | Gestión de dependencias |

---

## Arquitectura del proyecto

El proyecto sigue una **arquitectura en capas** (Layered Architecture) con separación clara de responsabilidades:

