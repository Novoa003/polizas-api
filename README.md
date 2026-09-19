# API de Gestión de Pólizas

API REST para la gestión de pólizas de arrendamiento (Individuales y Colectivas). Implementa las reglas de negocio del Módulo 2: crear, listar, renovar, cancelar pólizas y riesgos, con integración simulada al CORE legado.

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
