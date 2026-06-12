# Historial de Avances — Banco NovaBank

Registro cronologico de cambios y avances del proyecto.

Formato: `Agregado` (nuevo) · `Cambiado` (modificado) · `Corregido` (bug fix) · `Eliminado` (removido).

---

## [2026-06-12] — Tests unitarios ms-organizacion

### Agregado
- `SucursalServiceTest` con 12 pruebas unitarias (crear, listar, actualizar, activar, desactivar, eliminar con validacion de asociados).
- `EjecutivoServiceTest` con 12 pruebas unitarias (crear, validar sucursal existente/activa, CRUD, activar/desactivar).
- `DireccionSucursalServiceTest` con 12 pruebas unitarias (crear, validar sucursal, listar por ciudad/tipo, actualizar, eliminar).
- Plugin JaCoCo 0.8.12 en `ms-organizacion/pom.xml` para medicion de cobertura.
- DataFaker 2.4.2 en `ms-organizacion/pom.xml` para generacion de datos fake.

### Cambiado
- `ms-organizacion/pom.xml`: reemplazadas 2 dependencias de test inexistentes por `spring-boot-starter-test`.

### Estado
- **Total acumulado:** 79 pruebas unitarias (11 + 17 + 15 + 36).
- Tarjeta Trello: `Tests unitarios ms-organizacion (3 services)` movida a Hecho.

---

## [2026-06-11] — Tests unitarios ms-transacciones

### Agregado
- `TransaccionServiceTest` con 15 pruebas unitarias.
- Mock de `CuentaFeignClient` simulando respuestas remotas: `FeignException.NotFound` (404), `FeignException.InternalServerError` (503), `FeignException.BadRequest` (400).
- Caso de prueba que valida el cambio de estado de transaccion a RECHAZADA cuando falla el retiro remoto.
- Plugin JaCoCo 0.8.12 en `ms-transacciones/pom.xml`.
- DataFaker 2.4.2 en `ms-transacciones/pom.xml`.

### Cambiado
- `ms-transacciones/pom.xml`: reemplazadas 3 dependencias de test inexistentes por `spring-boot-starter-test`.

### Estado
- Tarjeta Trello: `Tests unitarios ms-transacciones (mock CuentaFeignClient)` movida a Hecho.

---

## [2026-06-10] — Tests unitarios ms-clientes y ms-cuentas

### Agregado
- `ClienteServiceTest` con 11 pruebas unitarias (crear, listar, actualizar, activar, desactivar, validaciones de unicidad).
- `CuentaServiceTest` con 17 pruebas unitarias incluyendo mock de `ClienteFeignClient` para validar comunicacion remota con ms-clientes.
- Casos de prueba para depositar, retirar, transferir y bloquear cuenta.
- Plugin JaCoCo 0.8.12 y DataFaker 2.4.2 en `ms-clientes/pom.xml` y `ms-cuentas/pom.xml`.
- Archivo `docs/REGISTRO-DEFECTOS.md` con bitacora de los 11 bugs detectados y resueltos durante el desarrollo.
- Archivo `docs/PLANIFICACION-GRUPO3.md` con distribucion de tareas del equipo.

### Cambiado
- `ms-clientes/pom.xml` y `ms-cuentas/pom.xml`: reemplazadas dependencias de test inexistentes por `spring-boot-starter-test`.

### Corregido
- BUG-006: dependencias `spring-boot-starter-data-jpa-test`, `spring-boot-starter-flyway-test`, `spring-boot-starter-validation-test`, `spring-boot-starter-webmvc-test` que no existen en Spring Boot.

### Estado
- Inicio formal del trabajo de Sumativa 3.
- Tablero Trello creado con 31 tarjetas distribuidas en 4 columnas (Por hacer, En proceso, Hecho, Bloqueado).

---

## [2026-05-19] — Cierre Sumativa 2

### Agregado
- Seeds adicionales `V2__seeds.sql` en ms-cuentas (4 cuentas, 3 tarjetas) y ms-transacciones (6 transacciones incluyendo una RECHAZADA para evidencia de auditoria).
- Seccion `Integrantes — Grupo 3` en `README.md` (Claudia Cardoza, Cristopher Camus).
- Documento `docs/PLANIFICACION-GRUPO3.md` reflejando la distribucion real de tareas con fechas.

### Estado
- Defensa Sumativa 2 realizada. Calificacion: 6.3.
- Feedback del docente: errores menores, incorporar metodos personalizados al CRUD y un endpoint agregador.

---

## [2026-05-15 / 2026-05-17] — Refactor ms-organizacion + integracion final Sumativa 2

### Agregado
- Microservicio `ms-organizacion` refactorizado al mismo nivel que los otros 3: entidades JPA, Flyway, Bean Validation, GlobalExceptionHandler, Swagger, configuracion completa de `application.yaml`.
- Diagrama entidad-relacion en `docs/DIAGRAMA-ENTIDAD-RELACION.md` con Mermaid.

### Cambiado
- Rutas REST normalizadas: `/api/v1/sucursales`, `/api/v1/ejecutivos`, `/api/v1/direcciones-sucursal`.
- Convencion `fromEntity()` renombrada a `toResponseDTO()` en todos los Response DTOs.
- Spring Cloud subido a 2025.1.1 para compatibilidad con Spring Boot 4.0.6.

### Corregido
- BUG-002: `NoClassDefFoundError: ServerProperties` al usar Spring Cloud 2025.0.0.
- BUG-003: ms-organizacion no compilaba por `ErrorResponse` faltante.
- BUG-005: conflictos de merge entre ramas `ms-clientes`, `ms-cuentas`, `ms-transaccion`.
- BUG-011: migracion Flyway V2 fallaba por emails duplicados en la tabla cliente.

---

## [2026-05-13 / 2026-05-14] — ms-cuentas y ms-transacciones

### Agregado
- Microservicio `ms-cuentas` completo: entidades Cuenta, TipoCuenta, Tarjeta; CRUD; operaciones `depositar`/`retirar`; integracion Feign hacia ms-clientes.
- Microservicio `ms-transacciones` completo: entidad Transaccion con estados PENDIENTE/COMPLETADA/RECHAZADA; orquestacion de operaciones via Feign hacia ms-cuentas; `@Transactional` con rollback en caso de fallo.
- 51 metodos personalizados distribuidos en los repositorios de los 4 microservicios.
- Query JPQL `@Query` con `SUM` y `COALESCE` para calcular saldo total activo por cliente.

### Corregido
- BUG-004: transferencia quedaba inconsistente si fallaba el deposito remoto despues del retiro.
- BUG-001: cliente con email duplicado podia registrarse (faltaba `existsByEmail`).

---

## [2026-05-04 / 2026-05-05] — Inicio del proyecto y ms-clientes

### Agregado
- Estructura base del repositorio `bancoNovaBank` con 4 microservicios.
- Patron CSR (Controller-Service-Repository) aplicado.
- Microservicio `ms-clientes` con CRUD completo, Bean Validation, GlobalExceptionHandler con 4 excepciones tipadas, Swagger, logs SLF4J.
- Configuracion Flyway en cada microservicio con migracion inicial.
- Bases de datos PostgreSQL por microservicio (Database per Service): `novabank-clientes`, `novabank-cuentas`, `novabank-transacciones`, `novabank-organizacion`.

### Corregido
- BUG-008: commit inicial solo contenia archivos compilados, sin codigo fuente.
- Se creo `.gitignore` raiz con exclusiones para `target/`, `.idea/`, `.DS_Store`.
