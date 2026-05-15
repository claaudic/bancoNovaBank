# Banco NovaBank - Documentación Completa

Sistema bancario implementado con arquitectura de microservicios en Spring Boot. Proyecto académico de tercer semestre.

---

## Índice

1. [Visión General](#1-visión-general)
2. [Stack Tecnológico](#2-stack-tecnológico)
3. [Arquitectura](#3-arquitectura)
4. [Estructura de Carpetas](#4-estructura-de-carpetas)
5. [Microservicio: ms-clientes](#5-microservicio-ms-clientes)
6. [Microservicio: ms-cuentas](#6-microservicio-ms-cuentas)
7. [Microservicio: ms-transacciones](#7-microservicio-ms-transacciones)
8. [Comunicación con Feign](#8-comunicación-con-feign)
9. [Manejo de Excepciones](#9-manejo-de-excepciones)
10. [Validaciones Bean Validation](#10-validaciones-bean-validation)
11. [Logs](#11-logs)
12. [Swagger / OpenAPI](#12-swagger--openapi)
13. [Seguridad: BCrypt](#13-seguridad-bcrypt)
14. [PostgreSQL y Flyway](#14-postgresql-y-flyway)
15. [Cómo Ejecutar el Proyecto](#15-cómo-ejecutar-el-proyecto)
16. [Cómo Probar el Proyecto](#16-cómo-probar-el-proyecto)
17. [Guía de Defensa Oral](#17-guía-de-defensa-oral)
18. [Preguntas Trampa Frecuentes](#18-preguntas-trampa-frecuentes)

---

## 1. Visión General

Banco NovaBank es un sistema bancario compuesto por **tres microservicios independientes** que se comunican entre sí mediante HTTP usando Feign Client. Cada microservicio tiene su propia base de datos PostgreSQL (schema separado), siguiendo el principio de **Database per Service**.

### Responsabilidades

| Microservicio | Puerto | Responsabilidad |
|---|---|---|
| ms-clientes | 8080 | Gestión de clientes, profesiones, direcciones y usuarios |
| ms-cuentas | 8081 | Cuentas bancarias, tipos de cuenta, tarjetas y operaciones monetarias |
| ms-transacciones | 8082 | Registro y orquestación de transacciones financieras |

### Principios aplicados

- **Database per Service:** cada microservicio dueño de su BD.
- **Sin entidades JPA compartidas:** las referencias cross-microservicio se guardan como tipos simples (`String rutCliente`, `Long idCuenta`).
- **Comunicación HTTP via Feign** para validar existencia de recursos externos.
- **Patrón CSR (Controller-Service-Repository):** separación estricta de responsabilidades.

---

## 2. Stack Tecnológico

| Componente | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje base |
| Spring Boot | 4.0.6 | Framework |
| Spring Cloud | 2025.1.1 | OpenFeign para HTTP entre servicios |
| PostgreSQL | - | Base de datos |
| Flyway | - | Migraciones controladas del schema |
| Jakarta Bean Validation | - | Validaciones declarativas en DTOs |
| Lombok | - | Reducción de boilerplate |
| SLF4J / `@Slf4j` | - | Logs estructurados |
| SpringDoc OpenAPI | 2.7.0 | Documentación automática (Swagger UI) |
| Spring Security Crypto | - | Solo `BCryptPasswordEncoder` (no toda la suite Security) |
| Maven Wrapper | - | Build sin Maven global instalado |

---

## 3. Arquitectura

### Diagrama de comunicación

```
                 [ Cliente / Postman / Swagger ]
                            │ HTTP
       ┌────────────────────┼────────────────────┐
       ▼                    ▼                    ▼
  ┌─────────┐         ┌──────────┐        ┌─────────────┐
  │ms-client│◄────────│ms-cuentas│◄───────│ms-transac.  │
  │  :8080  │  Feign  │  :8081   │ Feign  │   :8082     │
  └────┬────┘         └────┬─────┘        └──────┬──────┘
       │                   │                     │
       ▼                   ▼                     ▼
  schema:               schema:               schema:
  novabank-clientes     novabank-cuentas      novabank-transacciones
       │                   │                     │
       └───────────────────┴─────────────────────┘
                  PostgreSQL único, schemas separados
```

### Flujo de comunicación

1. **ms-cuentas → ms-clientes:** antes de crear una cuenta, valida que el `rutCliente` exista.
2. **ms-transacciones → ms-cuentas:** valida cuentas origen/destino, verifica estado ACTIVA, ejecuta operaciones (`depositar`/`retirar`).

No hay relaciones JPA cross-microservicio. Los ids externos se guardan como `String` o `Long` simples.

---

## 4. Estructura de Carpetas

Cada microservicio sigue la misma estructura:

```
ms-{nombre}/
├── pom.xml
├── mvnw, mvnw.cmd
└── src/
    ├── main/
    │   ├── java/com/novabank/ms{nombre}/
    │   │   ├── Ms{Nombre}Application.java
    │   │   ├── controller/          REST controllers (solo HTTP)
    │   │   ├── service/             Lógica de negocio
    │   │   ├── repository/          JPA repositories
    │   │   ├── model/               Entidades JPA y enums
    │   │   ├── dto/
    │   │   │   ├── request/         RequestDTOs con validaciones y toEntity()
    │   │   │   └── response/        ResponseDTOs con toResponseDTO() + ErrorResponse
    │   │   ├── client/              Feign clients (solo en ms-cuentas y ms-transacciones)
    │   │   ├── exception/           Excepciones de dominio + GlobalExceptionHandler
    │   │   └── config/              SwaggerConfig + SecurityConfig (solo ms-clientes)
    │   └── resources/
    │       ├── application.yaml
    │       └── db/migration/         V1__inicio.sql, V2__..., V3__...
    └── test/...
```

### Reglas por capa

- **Controller:** solo recibe requests, retorna `ResponseEntity`, delega al service. No tiene lógica de negocio ni SQL.
- **Service:** contiene reglas de negocio, valida condiciones, lanza excepciones de dominio, llama repositories y feign clients, anotado con `@Slf4j`.
- **Repository:** solo extiende `JpaRepository` + query methods. No lógica.
- **DTO request:** valida formato (`@NotBlank`, `@Pattern`, `@Email`, etc.) + método `toEntity()`.
- **DTO response:** método estático `toResponseDTO()`. Nunca expone fields sensibles (passwords, etc.).
- **Exception:** clases simples extendiendo `RuntimeException`. El `GlobalExceptionHandler` las traduce a HTTP.

---

## 5. Microservicio: ms-clientes

**Puerto:** 8080
**Schema:** `novabank-clientes`

### Entidades

#### Cliente
- `rutCliente` (String, PK, formato `12345678-9`)
- `numeroSerie` (String, único, 9 caracteres)
- `nombreCliente`, `apellidoCliente`, `telefonoCliente`, `emailCliente`
- `fechaCreacion` (LocalDateTime, automática)
- `estado` (enum `Estado`: ACTIVO / INACTIVO, almacenado como VARCHAR con `@Enumerated(EnumType.STRING)`)
- Relación `@ManyToOne` con `Profesion`
- Relación `@OneToMany` con `DireccionCliente` (cascade ALL, orphanRemoval)

#### Profesion
- `idProfesion` (Long, PK, auto-increment con `BIGINT GENERATED BY DEFAULT AS IDENTITY`)
- `nombreProfesion` (String, único)

#### DireccionCliente
- `id`, `calle`, `numero`, `depta`, `ciudad`
- `tipoDireccion` (enum `TipoDireccion`: RESIDENCIAL / COMERCIAL)
- Relación `@ManyToOne` con `Cliente`

#### Usuario (seguridad básica)
- `idUsuario` (Long, PK)
- `username` (String, único)
- `password` (String, **hash BCrypt** — nunca texto plano)
- `rol` (String, valores válidos: ADMIN / USER / OPERADOR)
- `fechaCreacion` (LocalDateTime)

### Reglas de negocio

- No permitir crear cliente con `rutCliente` duplicado.
- No permitir `numeroSerie` ni `emailCliente` duplicados.
- No actualizar ni agregar direcciones a un cliente INACTIVO.
- No eliminar una profesión que tenga clientes asociados.
- No permitir direcciones duplicadas para el mismo cliente (calle + número + depta).
- Al crear: `fechaCreacion = LocalDateTime.now()` y `estado = ACTIVO` automáticos.

### Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/clientes` | Listar todos |
| GET | `/api/v1/clientes/{rut}` | Buscar por rut |
| GET | `/api/v1/clientes/estado/{estado}` | Filtrar por estado |
| GET | `/api/v1/clientes/buscar?texto=` | Búsqueda parcial por nombre/apellido |
| GET | `/api/v1/clientes/profesion/{id}` | Clientes de una profesión |
| POST | `/api/v1/clientes` | Crear cliente con direcciones |
| PUT | `/api/v1/clientes/{rut}` | Actualizar (no permitido si INACTIVO) |
| PATCH | `/api/v1/clientes/{rut}/activar` | Reactivar cliente |
| PATCH | `/api/v1/clientes/{rut}/desactivar` | Desactivar cliente |
| DELETE | `/api/v1/clientes/{rut}` | Eliminar |
| POST | `/api/v1/usuarios` | Registrar usuario con password hasheada |
| POST | `/api/v1/usuarios/{username}/verificar` | Verificar password (matches contra hash) |

### Migraciones Flyway

- `V1__inicio.sql` — tablas cliente/profesion/direccion_cliente con seeds.
- `V2__enums_string_e_indices.sql` — convierte `estado` y `tipo_direccion` de SMALLINT a VARCHAR, agrega 7 índices y UNIQUE en email.
- `V3__usuario.sql` — tabla `usuario` con índices y CHECK constraint en rol.

---

## 6. Microservicio: ms-cuentas

**Puerto:** 8081
**Schema:** `novabank-cuentas`

### Entidades

#### Cuenta
- `idCuenta` (Long, PK)
- `numeroCuenta` (String, único, 8-20 dígitos)
- `fechaCreacion` (LocalDate, automática)
- `saldo` (BigDecimal, `precision=19, scale=2`, no negativo)
- `rutCliente` (String) — **sin FK JPA**, validado via Feign a ms-clientes
- `estado` (enum `EstadoCuenta`: ACTIVA / INACTIVA / BLOQUEADA)
- Relación `@ManyToOne` con `TipoCuenta`

#### TipoCuenta
- `idTipoCuenta` (Long, PK)
- `nombreTipoCuenta` (String, único: CORRIENTE / AHORRO / VISTA seed)
- Relación `@OneToMany` con `Cuenta`

#### Tarjeta
- `idTarjeta` (Long, PK)
- `numeroTarjeta` (String, único, 13-19 dígitos)
- `fechaVencimiento` (LocalDate, debe ser `@Future`)
- `cvv` (String, 3-4 dígitos)
- `estado` (enum `EstadoTarjeta`: ACTIVA / INACTIVA / BLOQUEADA / VENCIDA)
- Relación `@ManyToOne` con `Cuenta`

### Reglas de negocio

**Cuenta:**
- No duplicar `numeroCuenta`.
- Validar cliente vía Feign a ms-clientes antes de crear (si no existe → 400; si ms-clientes está caído → 503).
- `fechaCreacion = LocalDate.now()` y `estado = ACTIVA` automáticos.
- No actualizar cuenta INACTIVA.
- No eliminar cuenta con saldo positivo.
- **Depositar:** solo cuenta ACTIVA, suma al saldo.
- **Retirar:** solo cuenta ACTIVA, valida saldo suficiente.
- **Transferir:** ambas cuentas ACTIVAS y distintas, valida saldo origen, **atómico con `@Transactional`**.
- **Bloquear/Activar/Cerrar:** transiciones de estado controladas. Cerrar solo si saldo = 0.

**TipoCuenta:**
- No duplicar nombre.
- No eliminar si tiene cuentas asociadas.

**Tarjeta:**
- No duplicar `numeroTarjeta`.
- Solo se crea sobre cuenta ACTIVA.
- `fechaVencimiento` debe ser futura.
- No activar tarjeta vencida.
- `marcarTarjetasVencidas` batch que mueve tarjetas con fecha pasada a estado VENCIDA.

### Endpoints destacados

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/cuentas` | Crear (valida cliente via Feign) |
| GET | `/api/v1/cuentas/cliente/{rut}/saldo-total` | Suma saldos ACTIVOS |
| GET | `/api/v1/cuentas/fechas?desde=&hasta=` | Rango de fechas |
| POST | `/api/v1/cuentas/{id}/depositar` | Operación monetaria |
| POST | `/api/v1/cuentas/{id}/retirar` | Operación monetaria |
| POST | `/api/v1/cuentas/transferencia` | Transferencia atómica |
| PATCH | `/api/v1/cuentas/{id}/{bloquear\|activar\|cerrar}` | Cambios de estado |
| GET | `/api/v1/tarjetas/vencidas` | Listar tarjetas con fecha pasada |
| POST | `/api/v1/tarjetas/marcar-vencidas` | Batch: marcar VENCIDAS |

### Migración Flyway

- `V1__inicio.sql` — tres tablas, foreign keys internas, CHECK constraints en enums + saldo no negativo, 6 índices, seeds (CORRIENTE/AHORRO/VISTA).

---

## 7. Microservicio: ms-transacciones

**Puerto:** 8082
**Schema:** `novabank-transacciones`

### Entidad

#### Transaccion
- `idTransaccion` (Long, PK)
- `idCuentaOrigen`, `idCuentaDestino` (Long) — **sin FK JPA**, validados via Feign
- `tipoTransaccion` (enum `TipoTransaccion`: DEPOSITO / RETIRO / TRANSFERENCIA / PAGO)
- `montoTransaccion` (BigDecimal, `precision=19, scale=2`)
- `fechaTransaccion` (LocalDateTime, automática)
- `descripcion` (opcional, máx 35 chars)
- `estado` (enum `Estado`: PENDIENTE / COMPLETADA / RECHAZADA / REVERTIDA)

### Reglas de negocio

- Monto > 0 (`@DecimalMin("0.01")`).
- En TRANSFERENCIA: cuenta origen y destino distintas.
- Valida vía Feign que ambas cuentas existan en ms-cuentas y estén ACTIVAS.
- Flujo: crea en estado PENDIENTE → ejecuta operación remota vía Feign → marca COMPLETADA. Si falla la operación remota, marca RECHAZADA con motivo.
- No actualizar ni eliminar transacción COMPLETADA.

### Tipos de transacción y qué hacen via Feign

| Tipo | Operación remota en ms-cuentas |
|---|---|
| DEPOSITO | `POST /cuentas/{origen}/depositar` |
| RETIRO / PAGO | `POST /cuentas/{origen}/retirar` |
| TRANSFERENCIA | `retirar(origen)` + `depositar(destino)` |

### Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/transacciones` | Crear y ejecutar transacción |
| GET | `/api/v1/transacciones/cuenta/{id}` | Transacciones donde la cuenta es origen o destino |
| GET | `/api/v1/transacciones/estado/{estado}` | Filtrar por estado |
| GET | `/api/v1/transacciones/tipo/{tipo}` | Filtrar por tipo |
| GET | `/api/v1/transacciones/fechas?desde=&hasta=` | Rango de fechas |

---

## 8. Comunicación con Feign

### Qué es Feign

Feign convierte una interfaz Java anotada en un cliente HTTP automático. En lugar de escribir manualmente código con `RestTemplate` y parsing JSON, declaras una interfaz con anotaciones y Spring genera la implementación.

### Sin Feign vs con Feign

```java
// Sin Feign (manual)
RestTemplate rt = new RestTemplate();
String url = "http://localhost:8080/api/v1/clientes/" + rut;
ResponseEntity<ClienteResponseDTO> r = rt.getForEntity(url, ClienteResponseDTO.class);
ClienteResponseDTO cliente = r.getBody();

// Con Feign (1 línea)
ClienteResponseDTO cliente = clienteFeignClient.obtenerCliente(rut);
```

### Activación

En la clase principal:

```java
@SpringBootApplication
@EnableFeignClients   // ← escanea las interfaces @FeignClient
public class MsCuentasApplication { ... }
```

### Feign Client de ms-cuentas hacia ms-clientes

```java
@FeignClient(name = "ms-clientes", url = "${ms-clientes.url}")
public interface ClienteFeignClient {

    @GetMapping("/api/v1/clientes/{rutCliente}")
    ClienteResponseDTO obtenerCliente(@PathVariable("rutCliente") String rutCliente);
}
```

La URL viene de `application.yaml`:

```yaml
ms-clientes:
  url: http://localhost:8080
```

### Manejo de errores de Feign

```java
private void validarClienteExiste(String rutCliente) {
    try {
        clienteFeignClient.obtenerCliente(rutCliente);
    } catch (FeignException.NotFound e) {
        throw new BusinessRuleException("El cliente con rut " + rutCliente + " no existe");
    } catch (FeignException e) {
        log.error("Error invocando ms-clientes: {}", e.getMessage());
        throw new RemoteServiceException("No se pudo validar el cliente en ms-clientes");
    }
}
```

| Situación | Excepción Feign | Respuesta al cliente |
|---|---|---|
| ms-clientes devuelve 404 | `FeignException.NotFound` | 400 con mensaje "Cliente no existe" |
| ms-clientes caído / sin respuesta | `FeignException` genérico | 503 "ms-clientes no disponible" |

---

## 9. Manejo de Excepciones

### Excepciones de dominio

Cada microservicio define las mismas cuatro:

| Excepción | HTTP Status | Cuándo se lanza |
|---|---|---|
| `ResourceNotFoundException` | 404 | El recurso pedido no existe |
| `DuplicateResourceException` | 409 | Intentar crear algo con clave única ya usada |
| `BusinessRuleException` | 400 | Violación de regla de negocio (saldo insuficiente, cuenta no activa, etc.) |
| `RemoteServiceException` | 503 | Servicio remoto inalcanzable |

### GlobalExceptionHandler

Clase anotada con `@RestControllerAdvice` que intercepta cualquier excepción lanzada por los services o validaciones y la traduce a un JSON consistente.

### Respuesta JSON estándar (ErrorResponse)

```json
{
  "timestamp": "2026-05-14T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cliente no encontrado",
  "path": "/api/v1/clientes/12345678-9",
  "validationErrors": null
}
```

Para errores de validación Bean Validation:

```json
{
  "timestamp": "2026-05-14T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Errores de validacion",
  "path": "/api/v1/clientes",
  "validationErrors": {
    "rutCliente": "El rut debe tener formato 12345678-9",
    "emailCliente": "El email debe tener un formato valido"
  }
}
```

---

## 10. Validaciones Bean Validation

Las validaciones se declaran en los RequestDTOs y Spring las ejecuta automáticamente cuando el controller usa `@Valid`.

### Anotaciones usadas

| Anotación | Para qué | Ejemplo |
|---|---|---|
| `@NotBlank` | String obligatorio, no vacío ni espacios | `nombreCliente` |
| `@NotNull` | Cualquier tipo, no nulo | `idProfesion` |
| `@Email` | Formato email válido | `emailCliente` |
| `@Size(min=, max=)` | Longitud de string | `nombre` |
| `@Pattern(regexp=)` | Expresión regular | `rutCliente`, `numeroCuenta`, `cvv` |
| `@DecimalMin("0.0")` | Mínimo numérico | `saldo` |
| `@Digits(integer=, fraction=)` | Precisión decimal | `montoTransaccion` |
| `@Future` | Fecha en el futuro | `fechaVencimiento` |
| `@Valid` | Validación recursiva en objetos anidados | Lista de `DireccionClienteRequestDTO` |

### Ejemplo: CuentaRequestDTO

```java
@NotBlank
@Pattern(regexp = "^[0-9]{8,20}$", message = "El numero de cuenta debe contener entre 8 y 20 digitos")
private String numeroCuenta;

@NotNull
@DecimalMin(value = "0.0", inclusive = true, message = "El saldo no puede ser negativo")
private BigDecimal saldo;

@NotBlank
@Pattern(regexp = "^[0-9]{7,8}-[0-9kK]$", message = "El rut debe tener formato 12345678-9")
private String rutCliente;

@NotNull
private Long idTipoCuenta;
```

### Defensa en profundidad

Las validaciones Bean ocurren **antes** de entrar al service. Las reglas de negocio del service son **defensa en profundidad**: por ejemplo `@DecimalMin("0.0")` rechaza saldo negativo en el DTO, y el service además tiene `if (saldo.compareTo(ZERO) < 0)` por si alguien evita la validación.

---

## 11. Logs

Todos los services usan `@Slf4j` de Lombok, que inyecta automáticamente un `org.slf4j.Logger`.

### Niveles usados

- `log.info(...)` — operaciones exitosas: crear, actualizar, eliminar, operaciones monetarias.
- `log.debug(...)` — búsquedas y consultas.
- `log.warn(...)` — validaciones fallidas, reglas de negocio violadas.
- `log.error(...)` — errores remotos (Feign), excepciones no controladas.

### Ejemplo

```java
log.info("Cuenta creada id={} numero={} rutCliente={}",
        guardada.getIdCuenta(), guardada.getNumeroCuenta(), guardada.getRutCliente());

log.info("Transferencia origen={} destino={} monto={}",
        origen.getIdCuenta(), destino.getIdCuenta(), dto.getMonto());

log.warn("Regla de negocio violada: {}", ex.getMessage());

log.error("Error invocando ms-clientes para rut={}: {}", rutCliente, e.getMessage());
```

Los logs aparecen en la consola al ejecutar `./mvnw spring-boot:run`.

---

## 12. Swagger / OpenAPI

Cada microservicio expone documentación interactiva auto-generada.

### Acceso

```
http://localhost:8080/swagger-ui/index.html    (ms-clientes)
http://localhost:8081/swagger-ui/index.html    (ms-cuentas)
http://localhost:8082/swagger-ui/index.html    (ms-transacciones)
```

### Cómo está documentado

- Cada controller tiene `@Tag(name=..., description=...)`.
- Cada endpoint tiene `@Operation(summary=...)`.
- Cada endpoint tiene `@ApiResponses({@ApiResponse(responseCode=..., description=...), ...})` con todos los códigos posibles (200, 201, 204, 400, 404, 409, 503).

### Cómo probar un endpoint en Swagger

1. Click en el endpoint → se expande.
2. Click en "Try it out".
3. Editar el JSON o parámetros.
4. Click en "Execute".
5. Ver la respuesta en "Server response".

---

## 13. Seguridad: BCrypt

### Por qué BCrypt

Almacenar contraseñas en texto plano es un riesgo enorme. Cualquier acceso a la BD (un dump, un backup, una query mal restringida) expone todas las credenciales.

### Características de BCrypt

- **Salt aleatorio automático** por cada hash. El mismo password genera hashes diferentes.
- **Lento por diseño** (cost factor configurable), frenando ataques de fuerza bruta.
- Estándar para almacenar contraseñas.

### Configuración

`config/SecurityConfig.java`:

```java
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Uso en UsuarioService

```java
public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
    if (usuarioRepository.existsByUsername(dto.getUsername())) {
        throw new DuplicateResourceException("El username ya esta registrado");
    }

    Usuario usuario = dto.toEntity();
    usuario.setPassword(passwordEncoder.encode(dto.getPassword()));  // ← hash
    usuario.setFechaCreacion(LocalDateTime.now());

    return UsuarioResponseDTO.toResponseDTO(usuarioRepository.save(usuario));
}

public boolean verificarPassword(String username, String passwordPlano) {
    Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    return passwordEncoder.matches(passwordPlano, usuario.getPassword());
}
```

### Lo que se guarda en la BD

```
username   = "admin"
password   = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
                ^^^^ ^^   ^^^^^^^^^^^^^^^^^^^^^^   ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                algo cost  salt aleatorio          hash resultante
```

### Lo que NO se hace

- No se usa Spring Security completo (eso aseguraría todos los endpoints).
- No se implementa JWT ni OAuth.
- No hay autenticación por sesión, solo demostración de almacenamiento seguro.

---

## 14. PostgreSQL y Flyway

### Configuración PostgreSQL

Cada `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://34.16.103.65:5432/postgres
    username: panel-ciudadano-dev
    password: F^TzfiihrKz^QHy`

  jpa:
    hibernate:
      ddl-auto: validate         # NUNCA "create" ni "update"
    properties:
      hibernate:
        default_schema: novabank-{nombre}

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    default-schema: novabank-{nombre}
```

### `ddl-auto: validate` — Por qué

- `none` → Hibernate ignora el schema (peligroso si entidad y BD divergen).
- `update` → Hibernate modifica la BD silenciosamente (peligroso, ignora Flyway).
- `create` / `create-drop` → recrea tablas en cada arranque (destruye datos).
- **`validate`** → Hibernate solo verifica que el schema coincide con las entidades. Si hay desfase, **el servicio no arranca** (fail-fast).

Esto significa que las migraciones reales las controla **Flyway** y Hibernate solo valida.

### Tipos PostgreSQL usados

| Java | PostgreSQL | Razón |
|---|---|---|
| `Long @GeneratedValue` | `BIGINT GENERATED BY DEFAULT AS IDENTITY` | Estándar SQL, no AUTO_INCREMENT |
| `String` | `VARCHAR(n)` | - |
| `BigDecimal` con `precision=19, scale=2` | `NUMERIC(19, 2)` | Precisión exacta para dinero |
| `LocalDate` | `DATE` | - |
| `LocalDateTime` | `TIMESTAMP WITHOUT TIME ZONE` | No DATETIME |
| Enum con `@Enumerated(EnumType.STRING)` | `VARCHAR(n)` + CHECK constraint | Robusto ante reordenamiento del enum |

### Migraciones Flyway

Cada archivo en `src/main/resources/db/migration/`:

```
V1__inicio.sql                       (ms-clientes, ms-cuentas, ms-transacciones)
V2__enums_string_e_indices.sql       (solo ms-clientes)
V3__usuario.sql                       (solo ms-clientes)
```

Flyway aplica las migraciones en orden al arrancar y registra en `flyway_schema_history` cuáles ya fueron aplicadas. Si modificas una migración después de aplicada, falla con error de checksum — protege contra cambios accidentales.

---

## 15. Cómo Ejecutar el Proyecto

### Pre-requisitos

- Java 21 instalado.
- Conexión a la base de datos PostgreSQL configurada en `application.yaml`.
- (No requiere Maven instalado — usa `mvnw` incluido.)

### Orden de arranque (obligatorio)

Tres terminales separadas:

```bash
# Terminal 1
cd ms-clientes
./mvnw spring-boot:run
# Espera el mensaje "Started MsClientesApplication"
```

```bash
# Terminal 2
cd ms-cuentas
./mvnw spring-boot:run
# Espera "Started MsCuentasApplication"
```

```bash
# Terminal 3
cd ms-transacciones
./mvnw spring-boot:run
# Espera "Started MsTransaccionesApplication"
```

### Verificar que están corriendo

Abrir en el navegador:

```
http://localhost:8080/swagger-ui/index.html
http://localhost:8081/swagger-ui/index.html
http://localhost:8082/swagger-ui/index.html
```

Si las tres páginas cargan, todo OK.

### Para detener

`Ctrl + C` en cada terminal, o desde otra terminal:

```bash
pkill -f "spring-boot:run"
```

### Spring DevTools

Está habilitado. Si modificas código y haces `./mvnw compile`, los servicios se hot-recargan automáticamente sin reiniciar manualmente.

---

## 16. Cómo Probar el Proyecto

### Herramienta recomendada: Swagger UI

Ya viene integrado en cada microservicio. No requiere instalar nada externo.

### Flujo end-to-end completo

#### Paso 1 — Crear cliente (ms-clientes, puerto 8080)

`POST /api/v1/clientes`:

```json
{
  "rutCliente": "99887766-5",
  "numeroSerie": "789456123",
  "nombreCliente": "Carlos",
  "apellidoCliente": "Demo",
  "telefonoCliente": "+56912345678",
  "emailCliente": "carlos.demo@mail.com",
  "idProfesion": 1,
  "direcciones": [
    {
      "calle": "Avenida Demo",
      "numero": "123",
      "depta": "A1",
      "tipoDireccion": "RESIDENCIAL",
      "ciudad": "Santiago"
    }
  ]
}
```
**Esperado:** 201 con el cliente creado.

#### Paso 2 — Crear cuenta (ms-cuentas, puerto 8081)

`POST /api/v1/cuentas`:

```json
{
  "numeroCuenta": "12345678",
  "saldo": 100000,
  "rutCliente": "99887766-5",
  "idTipoCuenta": 1
}
```
**Esperado:** 201, estado ACTIVA. Por dentro Feign llamó a ms-clientes para validar el rut. Anotar el `idCuenta` retornado.

#### Paso 3 — Depositar (ms-cuentas)

`POST /api/v1/cuentas/{idCuenta}/depositar`:

```json
{ "monto": 50000 }
```
**Esperado:** 200 con saldo nuevo 150000.

#### Paso 4 — Crear tarjeta (ms-cuentas)

`POST /api/v1/tarjetas`:

```json
{
  "numeroTarjeta": "4532123456789012",
  "fechaVencimiento": "2028-12-31",
  "cvv": "123",
  "idCuenta": 1
}
```
**Esperado:** 201, estado ACTIVA.

#### Paso 5 — Segunda cuenta para otro cliente

`POST /api/v1/cuentas` con `rutCliente: "11111111-1"` (cliente del seed).

#### Paso 6 — Transferencia (ms-transacciones, puerto 8082)

`POST /api/v1/transacciones`:

```json
{
  "idCuentaOrigen": 1,
  "idCuentaDestino": 2,
  "tipoTransaccion": "TRANSFERENCIA",
  "montoTransaccion": 30000,
  "descripcion": "Pago demo"
}
```
**Esperado:** 201, estado COMPLETADA. Por dentro ms-transacciones consultó las dos cuentas vía Feign, retiró de origen y depositó en destino.

#### Paso 7 — Verificar saldos

`GET /api/v1/cuentas/1` y `GET /api/v1/cuentas/2`:
- Cuenta 1: 120000 (era 150000, -30000).
- Cuenta 2: saldo aumentado en 30000.

#### Paso 8 — Historial

`GET /api/v1/transacciones/cuenta/1` — debe mostrar la transferencia.

### Casos de error que demuestran robustez

**Validación Bean Validation (400):**

`POST /api/v1/clientes` con `rutCliente: "abc"` y `emailCliente: "no-es-email"` → 400 con `validationErrors` campo por campo.

**Cliente inexistente vía Feign (400):**

`POST /api/v1/cuentas` con `rutCliente: "00000000-0"` → 400 con mensaje "El cliente con rut 00000000-0 no existe".

**Saldo insuficiente en transferencia (400):**

`POST /api/v1/transacciones` con `montoTransaccion: 999999999` → 400 "Saldo insuficiente". La transacción queda persistida con estado RECHAZADA (auditable).

**Cuenta no existe (404):**

`GET /api/v1/cuentas/99999` → 404 con JSON estandarizado.

**Recurso duplicado (409):**

`POST /api/v1/clientes` dos veces con el mismo `rutCliente` → primera 201, segunda 409.

**Servicio remoto caído (503):**

Apagar ms-clientes y hacer `POST /api/v1/cuentas` → 503 "ms-clientes no disponible".

---

## 17. Guía de Defensa Oral

### Sobre la arquitectura general

> "El proyecto es un sistema bancario en arquitectura de microservicios. Hay tres servicios independientes — clientes, cuentas y transacciones — cada uno con su propia base de datos siguiendo el patrón Database per Service. Se comunican por HTTP usando Feign, no comparten entidades JPA. Cada servicio tiene la estructura controller-service-repository clásica con DTOs separados de request y response."

### Sobre Feign

> "Feign convierte una interfaz Java anotada en un cliente HTTP. Yo declaro la interfaz con `@FeignClient` y los métodos con `@GetMapping`/`@PostMapping`, y Spring genera la implementación que hace la llamada HTTP. Por ejemplo, antes de crear una cuenta, ms-cuentas llama a ms-clientes para validar que el rut existe. Si responde 404 traduzco a `BusinessRuleException`, si está caído lanzo `RemoteServiceException` y devuelvo 503."

### Sobre transacciones atómicas

> "El método de transferir está anotado con `@Transactional`. Spring abre una transacción JPA al entrar, y si todo el método se ejecuta sin excepciones hace commit al final. Si lanza una RuntimeException, hace rollback automático — el retiro y el depósito se deshacen juntos. No puede pasar que retire dinero de una cuenta y no llegue a la otra."

### Sobre Bean Validation

> "Las validaciones de formato están en los DTOs con anotaciones declarativas — `@NotBlank`, `@Pattern`, `@Email`, `@DecimalMin`. Cuando el controller recibe la request con `@Valid`, Spring ejecuta las validaciones antes de entrar al service. Si alguna falla, lanza `MethodArgumentNotValidException` que mi `GlobalExceptionHandler` traduce a 400 con el detalle campo por campo."

### Sobre el manejo de excepciones

> "Defino cuatro excepciones de dominio: `ResourceNotFoundException` para 404, `DuplicateResourceException` para 409, `BusinessRuleException` para 400 y `RemoteServiceException` para 503. Las lanzo desde los services. El `GlobalExceptionHandler` está anotado con `@RestControllerAdvice` y las captura, retornando siempre el mismo formato JSON con timestamp, status, error, message y path. Esto le da al cliente final una experiencia consistente sin importar qué falla."

### Sobre PostgreSQL

> "Uso PostgreSQL con tipos estándar SQL: BIGINT GENERATED BY DEFAULT AS IDENTITY para los ids autoincrementales, NUMERIC con precisión y escala para BigDecimal, VARCHAR con CHECK constraints para los enums. Hibernate corre en modo `validate`: no modifica el schema, solo verifica que coincide con las entidades. Las migraciones reales las controla Flyway con archivos V1, V2, V3 inmutables."

### Sobre BCrypt

> "Las contraseñas se almacenan hasheadas con BCrypt. BCrypt aplica un salt aleatorio automáticamente y es deliberadamente lento, lo que frena ataques de fuerza bruta. Comparo passwords con `passwordEncoder.matches(plano, hash)` que aplica el mismo algoritmo y compara hashes. No uso `equals()` porque comparar texto plano con hash siempre daría falso."

### Sobre Swagger

> "Cada microservicio expone su contrato OpenAPI en `/swagger-ui/index.html`. Documenté los endpoints con `@Tag` y `@Operation`, y los códigos de respuesta posibles con `@ApiResponses`. Esto reemplaza la necesidad de un documento Word con la lista de endpoints — la documentación se genera del código real."

### Sobre lo que NO implementé

> "Decidí no implementar Eureka, API Gateway, Kafka, JWT ni OAuth. La pauta académica no los exige y agregar capas enterprise que no se justifican vuelve el proyecto difícil de defender. Para tres microservicios fijos, URLs parametrizadas en `application.yaml` son suficientes. Si en producción real escalara, los agregaría."

---

## 18. Preguntas Trampa Frecuentes

### "¿Qué pasa si elimino `@Transactional` del método transferir?"

> Cada `cuentaRepository.save()` se ejecuta en su propia mini-transacción JPA. Si el retiro pasa pero el depósito falla, el retiro queda persistido y el dinero se pierde. Es exactamente el bug que `@Transactional` previene: garantiza atomicidad.

### "¿Qué pasa si quito `@Valid` del controller?"

> Spring no ejecuta las validaciones Bean Validation del DTO. Puedo mandar `saldo: -500`, `rutCliente: ""`, lo que sea, y se guarda sin error. Es la línea más crítica para evitar datos basura.

### "¿Por qué `rutCliente` es String y no FK a Cliente?"

> Porque Cliente vive en otro microservicio con su propia base de datos. Si pongo una FK JPA, acoplo los schemas y rompo Database per Service. La validación de existencia la hago via Feign, y la integridad lógica se mantiene a nivel de aplicación.

### "¿Qué pasa si elimino `@Enumerated(EnumType.STRING)`?"

> Hibernate guarda el ordinal del enum (0, 1, 2…). Si después reordeno el enum agregando un valor al principio, todas las filas existentes pasan a tener el estado equivocado. Con `STRING` se guarda el nombre literal y es inmune a reordenamientos.

### "¿Qué pasa si quito `@RestControllerAdvice` del GlobalExceptionHandler?"

> La clase deja de interceptar excepciones globalmente. Mis `ResourceNotFoundException` propagan hasta Spring que las convierte en 500 genéricos con stack trace. Pierdo todo el JSON consistente.

### "¿Por qué BCrypt y no SHA-256 o MD5?"

> BCrypt está diseñado para ser lento — frena ataques de fuerza bruta. SHA-256 y MD5 son hashes rápidos pensados para integridad de archivos; una GPU moderna calcula billones de SHA-256 por segundo. BCrypt también aplica salt automáticamente, con SHA tendría que gestionarlo yo mismo.

### "¿Qué pasa si dos transferencias simultáneas tocan la misma cuenta?"

> Con `@Transactional` simple tengo aislamiento READ_COMMITTED (default PostgreSQL), pero podría haber lost updates si dos transferencias leen el saldo simultáneamente. Para producción real usaría optimistic locking con `@Version`, o pessimistic con `@Lock(PESSIMISTIC_WRITE)`. Para el alcance académico esto está fuera de scope, pero identifico el problema y conozco la solución.

### "¿Por qué no usaste Eureka?"

> Para tres microservicios fijos no agrega valor. Las URLs están en `application.yaml`, cambiar de entorno se hace sin recompilar. Eureka tiene sentido con muchos servicios dinámicos con escalamiento horizontal. Acá complica sin justificación.

### "¿Por qué `ddl-auto: validate` y no `update`?"

> `update` es peligroso porque Hibernate modifica el schema según las entidades, ignorando mi control con Flyway. `validate` solo verifica coherencia: si modifico una entidad sin migración, el servicio no arranca — fail-fast.

### "¿Qué hace `@EnableFeignClients`?"

> Le dice a Spring que escanee las interfaces anotadas con `@FeignClient` y genere implementaciones inyectables. Sin esa anotación, mi `ClienteFeignClient` no se inyectaría — Spring lanzaría `NoSuchBeanDefinitionException`.

### "¿Por qué hay una excepción para 503 separada de FeignException?"

> `FeignException` es de la librería; podría escapársele al desarrollador del service y propagarse sin contexto. `RemoteServiceException` es mía, con un mensaje específico de qué servicio remoto falló. El `GlobalExceptionHandler` mapea ambas a 503, pero la mía tiene mensaje más limpio para el cliente final.

---

## Notas finales

- **Estado actual:** los tres microservicios compilan y corren correctamente.
- **Acceso desarrollo:** `http://localhost:{8080|8081|8082}/swagger-ui/index.html`.
- **Detener todo:** `pkill -f "spring-boot:run"`.
- **Modificar código:** Spring DevTools hot-recarga automáticamente al guardar.

Cualquier duda específica que aparezca en la defensa, mantén la estructura: **qué hace** → **por qué** → **qué se rompe sin esto**.
