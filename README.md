# Banco NovaBank

Sistema bancario implementado con arquitectura de microservicios en Spring Boot.
Proyecto académico de tercer semestre.

## Microservicios

| Microservicio | Puerto | Responsabilidad |
|---|---|---|
| ms-clientes | 8080 | Clientes, profesiones, direcciones, usuarios |
| ms-cuentas | 8081 | Cuentas bancarias, tipos de cuenta, tarjetas, operaciones |
| ms-transacciones | 8082 | Registro y orquestación de transacciones financieras |
| ms-organizacion | 8083 | Sucursales, ejecutivos, direcciones de sucursales |

Cada microservicio tiene su propia base de datos PostgreSQL con schema separado (`novabank-clientes`, `novabank-cuentas`, `novabank-transacciones`, `novabank-organizacion`).

## Comunicación entre microservicios

Vía HTTP usando OpenFeign:

- ms-cuentas → ms-clientes: valida que el cliente exista antes de crear una cuenta.
- ms-transacciones → ms-cuentas: valida cuentas y ejecuta operaciones (depositar, retirar, transferir).

## Stack

- Java 21
- Spring Boot 4.0.6
- Spring Cloud 2025.1.1 (OpenFeign)
- PostgreSQL + Flyway
- Spring Data JPA
- Jakarta Bean Validation
- Lombok
- SpringDoc OpenAPI (Swagger)
- Spring Security Crypto (BCrypt) en ms-clientes
- Maven Wrapper

## Arquitectura

Cada microservicio sigue el patrón Controller-Service-Repository:

```
ms-{nombre}/
├── pom.xml
├── mvnw, mvnw.cmd
└── src/main/
    ├── java/com/novabank/ms{nombre}/
    │   ├── controller/         REST controllers
    │   ├── service/            Lógica de negocio
    │   ├── repository/         JPA repositories
    │   ├── model/              Entidades JPA y enums
    │   ├── dto/
    │   │   ├── request/        DTOs de entrada con validaciones
    │   │   └── response/       DTOs de salida + ErrorResponse
    │   ├── client/             Feign clients (solo ms-cuentas y ms-transacciones)
    │   ├── exception/          Excepciones de dominio + GlobalExceptionHandler
    │   └── config/             SwaggerConfig (y SecurityConfig en ms-clientes)
    └── resources/
        ├── application.yaml
        └── db/migration/       Migraciones Flyway
```

## Cómo ejecutar

Pre-requisitos:

- Java 21 instalado
- PostgreSQL accesible (configurado en cada `application.yaml`)

Arrancar los 4 microservicios en terminales separadas:

```bash
cd ms-clientes && ./mvnw spring-boot:run
cd ms-cuentas && ./mvnw spring-boot:run
cd ms-transacciones && ./mvnw spring-boot:run
cd ms-organizacion && ./mvnw spring-boot:run
```

Orden de arranque: ms-clientes → ms-cuentas → ms-transacciones (ms-organizacion es autónomo).

Detener todo:

```bash
pkill -f "spring-boot:run"
```

## Swagger UI

Una vez levantados:

- http://localhost:8080/swagger-ui/index.html
- http://localhost:8081/swagger-ui/index.html
- http://localhost:8082/swagger-ui/index.html
- http://localhost:8083/swagger-ui/index.html

## Endpoints REST

Todos siguen el prefijo `/api/v1/`:

- `/api/v1/clientes`, `/api/v1/profesiones`, `/api/v1/direcciones`, `/api/v1/usuarios`
- `/api/v1/cuentas`, `/api/v1/tipos-cuenta`, `/api/v1/tarjetas`
- `/api/v1/transacciones`
- `/api/v1/sucursales`, `/api/v1/ejecutivos`, `/api/v1/direcciones-sucursal`

## Manejo de errores

Respuestas JSON consistentes con `@RestControllerAdvice`:

| Excepción | HTTP Status |
|---|---|
| `ResourceNotFoundException` | 404 |
| `DuplicateResourceException` | 409 |
| `BusinessRuleException` | 400 |
| `RemoteServiceException` | 503 |

Formato:

```json
{
  "timestamp": "2026-05-15T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cliente no encontrado",
  "path": "/api/v1/clientes/00000000-0"
}
```

## Diagrama Entidad-Relación

Ver [docs/DIAGRAMA-ENTIDAD-RELACION.md](docs/DIAGRAMA-ENTIDAD-RELACION.md) con el modelo relacional de los 4 microservicios.

## Estado actual

Los 4 microservicios compilan correctamente y se ejecutan en local.
