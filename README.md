# Banco NovaBank

Sistema bancario distribuido implementado con arquitectura de microservicios en Spring Boot.
Proyecto académico de tercer semestre — Sumativa 3 (Cierre Técnico).

## Integrantes — Grupo 3

- **Claudia Cardoza**
- **Cristopher Camus**

**Asignatura:** DSY1103 — Desarrollo FullStack 1
**Docente:** David Larrondo N.

---

## Microservicios

El sistema cuenta con **6 microservicios**: 4 de negocio + 2 de infraestructura.

| Microservicio | Tipo | Puerto | Responsabilidad |
|---|---|---|---|
| `ms-clientes` | Negocio | 8080 | Clientes del banco, profesiones, direcciones, usuarios |
| `ms-cuentas` | Negocio | 8081 | Cuentas bancarias, tipos de cuenta, tarjetas, operaciones (depositar/retirar) |
| `ms-transacciones` | Negocio | 8082 | Registro y orquestación de operaciones financieras (depósitos, retiros, transferencias) |
| `ms-organizacion` | Negocio | 8083 | Sucursales, ejecutivos y direcciones de sucursales |
| `ms-eureka` | Infraestructura | 8761 | Service Discovery — registro de microservicios |
| `ms-gateway` | Infraestructura | 8090 | API Gateway — único punto de entrada con enrutamiento |

Cada microservicio de negocio tiene su propia base de datos PostgreSQL con schema separado (`novabank-clientes`, `novabank-cuentas`, `novabank-transacciones`, `novabank-organizacion`) siguiendo el patrón **Database per Service**.

---

## Comunicación entre microservicios

### OpenFeign (clientes HTTP declarativos)

- `ms-cuentas → ms-clientes`: valida que el cliente exista antes de crear una cuenta
- `ms-transacciones → ms-cuentas`: valida cuentas y ejecuta operaciones (depositar, retirar)

### Service Discovery (Eureka)

Todos los microservicios se registran automáticamente en `ms-eureka` al arrancar.

### API Gateway

`ms-gateway` recibe las peticiones externas en el puerto **8090** y enruta a cada microservicio interno usando Eureka para resolver las URLs.

---

## Stack tecnológico

| Categoría | Herramienta | Versión |
|---|---|---|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 4.0.6 |
| Microservicios | Spring Cloud | 2025.1.1 |
| Persistencia | PostgreSQL + Spring Data JPA + Hibernate | 16 |
| Migraciones | Flyway | 11 |
| Comunicación | OpenFeign | (incluido en Spring Cloud) |
| Service Discovery | Netflix Eureka | (incluido en Spring Cloud) |
| API Gateway | Spring Cloud Gateway | (incluido en Spring Cloud) |
| Documentación | SpringDoc OpenAPI / Swagger UI | 2.8.13 |
| HATEOAS | Spring HATEOAS | (Boot starter) |
| Validación | Jakarta Bean Validation | (incluido en Boot) |
| Hash de claves | Spring Security Crypto (BCrypt) | (solo ms-clientes) |
| Logging | SLF4J + Logback (vía Lombok `@Slf4j`) | |
| Testing | JUnit 5 + Mockito + AssertJ + DataFaker | |
| Cobertura | JaCoCo | 0.8.12 |
| Contenedores | Docker + docker-compose | |
| Despliegue remoto | Render | |
| Build | Maven Wrapper | |

---

## Arquitectura

Cada microservicio sigue el patrón **Controller-Service-Repository (CSR)**:

```
ms-{nombre}/
├── Dockerfile                  Multi-stage build (Maven + JRE)
├── pom.xml
├── mvnw, mvnw.cmd
└── src/main/
    ├── java/com/novabank/ms{nombre}/
    │   ├── controller/         REST controllers
    │   ├── service/            Lógica de negocio
    │   ├── repository/         JPA repositories (51 métodos custom totales)
    │   ├── model/              Entidades JPA y enums
    │   ├── dto/
    │   │   ├── request/        DTOs de entrada con Bean Validation
    │   │   └── response/       DTOs de salida + ErrorResponse + HATEOAS
    │   ├── client/             Feign clients (solo ms-cuentas y ms-transacciones)
    │   ├── exception/          4 excepciones de dominio + GlobalExceptionHandler
    │   └── config/             SwaggerConfig
    └── resources/
        ├── application.yaml          Configuración común + activa perfil dev
        ├── application-dev.yaml      Perfil desarrollo (BD local + Eureka local)
        ├── application-prod.yaml     Perfil producción (variables de entorno)
        └── db/migration/             Migraciones SQL versionadas (Flyway)
```

---

## Cómo ejecutar localmente

### Opción A — Con Docker (recomendado, todo en un comando)

Pre-requisito: tener **Docker Desktop** corriendo.

```bash
docker-compose up --build
```

Esto levanta los 6 microservicios en contenedores con red privada. Eureka arranca primero (`depends_on`), los microservicios se registran automáticamente y el Gateway queda accesible al final.

Para detener todo:

```bash
docker-compose down
```

### Opción B — Sin Docker (manual, 6 terminales)

Pre-requisito: Java 21 instalado y PostgreSQL accesible.

Orden de arranque:

```bash
# Terminal 1
cd ms-eureka && ./mvnw spring-boot:run

# (esperar 30 segundos hasta que Eureka esté listo)

# Terminales 2-5
cd ms-clientes && ./mvnw spring-boot:run
cd ms-cuentas && ./mvnw spring-boot:run
cd ms-transacciones && ./mvnw spring-boot:run
cd ms-organizacion && ./mvnw spring-boot:run

# Terminal 6 (al final)
cd ms-gateway && ./mvnw spring-boot:run
```

---

## URLs locales

### Service Discovery — Eureka Dashboard
- http://localhost:8761

### Swagger UI por microservicio
- http://localhost:8080/swagger-ui/index.html (ms-clientes)
- http://localhost:8081/swagger-ui/index.html (ms-cuentas)
- http://localhost:8082/swagger-ui/index.html (ms-transacciones)
- http://localhost:8083/swagger-ui/index.html (ms-organizacion)

### API Gateway — punto único de entrada
- `http://localhost:8090/clientes/**` → ms-clientes
- `http://localhost:8090/cuentas/**` → ms-cuentas
- `http://localhost:8090/transacciones/**` → ms-transacciones
- `http://localhost:8090/organizacion/**` → ms-organizacion

### Ejemplo de llamada vía Gateway
```bash
curl http://localhost:8090/clientes/11111111-1
```

---

## Despliegue remoto (Render)

Los siguientes microservicios están desplegados en producción en la plataforma **Render**:

| Microservicio | URL pública |
|---|---|
| ms-clientes | https://ms-clientes-5r0e.onrender.com |
| ms-organizacion | (URL se completa al desplegar) |

> **Nota:** El plan free de Render hiberna los servicios tras 15 min de inactividad. La primera petición tras inactividad puede tardar ~50 segundos en despertar la instancia.

### Probar en producción

```bash
curl https://ms-clientes-5r0e.onrender.com/api/v1/clientes/11111111-1
```

Devuelve el JSON del cliente con sus `_links` HATEOAS.

---

## Endpoints REST principales

Todos siguen el prefijo `/api/v1/`:

| Microservicio | Endpoints principales |
|---|---|
| ms-clientes | `/clientes`, `/profesiones`, `/direcciones`, `/usuarios` |
| ms-cuentas | `/cuentas`, `/tipos-cuenta`, `/tarjetas` |
| ms-transacciones | `/transacciones` |
| ms-organizacion | `/sucursales`, `/ejecutivos`, `/direcciones-sucursal` |

Las respuestas incluyen enlaces **HATEOAS** (`_links`) a las acciones disponibles, elevando la API a REST nivel 3 del Richardson Maturity Model.

---

## Manejo de errores

Respuestas JSON consistentes manejadas por `@RestControllerAdvice`:

| Excepción de dominio | HTTP Status |
|---|---|
| `ResourceNotFoundException` | 404 Not Found |
| `DuplicateResourceException` | 409 Conflict |
| `BusinessRuleException` | 400 Bad Request |
| `RemoteServiceException` | 503 Service Unavailable |

Formato del `ErrorResponse`:

```json
{
  "timestamp": "2026-06-21T20:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cliente no encontrado",
  "path": "/api/v1/clientes/00000000-0"
}
```

---

## Pruebas

### Cobertura

Se desarrollaron **79 pruebas unitarias** distribuidas entre los 4 microservicios de negocio:

| Microservicio | Tests | Cobertura objetivo |
|---|---|---|
| ms-clientes | 11 | ≥ 80% |
| ms-cuentas | 17 | ≥ 80% |
| ms-transacciones | 15 | ≥ 80% |
| ms-organizacion | 36 (3 services) | ≥ 80% |

### Estructura

Todos los tests siguen el patrón **Given-When-Then** usando:
- **JUnit 5** como framework
- **Mockito** para mockear repositorios y FeignClient
- **AssertJ** para aserciones legibles
- **DataFaker** para generar datos fake realistas
- **JaCoCo** para medir cobertura

### Ejecutar las pruebas

```bash
# Por microservicio
cd ms-clientes && ./mvnw test
cd ms-cuentas && ./mvnw test
cd ms-transacciones && ./mvnw test
cd ms-organizacion && ./mvnw test
```

El reporte JaCoCo se genera en `target/site/jacoco/index.html`.

---

## Perfiles de configuración

Cada microservicio tiene 3 archivos de configuración:

- `application.yaml` — configuración común + activa el perfil `dev` por defecto
- `application-dev.yaml` — entorno de desarrollo (BD local, Eureka local)
- `application-prod.yaml` — entorno de producción (variables de entorno, sin Eureka)

Para forzar un perfil distinto:

```bash
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

---

## Documentación adicional

| Archivo | Contenido |
|---|---|
| [docs/DIAGRAMA-ENTIDAD-RELACION.md](docs/DIAGRAMA-ENTIDAD-RELACION.md) | Modelo entidad-relación de los 4 microservicios |
| [docs/PLAN-DE-PRUEBAS.md](docs/PLAN-DE-PRUEBAS.md) | Plan de pruebas completo con 12 casos detallados |
| [docs/REGISTRO-DEFECTOS.md](docs/REGISTRO-DEFECTOS.md) | Bitácora de los 16 bugs detectados y resueltos |
| [docs/HISTORIAL-AVANCES.md](docs/HISTORIAL-AVANCES.md) | Historial cronológico de cambios |
| [docs/PLANIFICACION-GRUPO3.md](docs/PLANIFICACION-GRUPO3.md) | Distribución de tareas del equipo |
| [docs/capturas/](docs/capturas/) | Capturas de evidencia (Eureka, Swagger, HATEOAS, Render) |

---

## Estado actual

✅ Los 6 microservicios funcionando localmente con Docker (`docker-compose up`)
✅ 79 pruebas unitarias pasando con cobertura ≥ 80%
✅ Documentación Swagger accesible
✅ HATEOAS implementado en los 4 ResponseDTOs principales
✅ Service Discovery operativo con Eureka
✅ API Gateway con enrutamiento configurado
✅ Perfiles dev/prod separados con variables de entorno
✅ Despliegue remoto en Render (ms-clientes y ms-organizacion)
