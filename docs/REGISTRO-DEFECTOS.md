# Registro de Defectos — Banco NovaBank

Historial de bugs encontrados y resueltos durante el desarrollo del proyecto.

## Convenciones

- **Severidad:** Alta / Media / Baja
- **Estado:** Abierto / En progreso / Corregido
- **Commit:** hash corto del commit que corrige el bug

---

## BUG-001 — Email duplicado podía registrarse en ms-clientes

- **Fecha:** 05/05/2026
- **Severidad:** Alta
- **Descripción:** El endpoint `POST /api/v1/clientes` permitía crear clientes con emails ya registrados. Faltaba la validación `existsByEmailCliente` en el service.
- **Causa:** Solo se validaba duplicado de `rutCliente`, no de email.
- **Solución:** Se agregó query method `existsByEmailCliente` en el repository y validación previa en `ClienteService.crearCliente`.
- **Estado:** Corregido

---

## BUG-002 — Incompatibilidad de Spring Cloud con Spring Boot 4

- **Fecha:** 14/05/2026
- **Severidad:** Alta
- **Descripción:** ms-cuentas no arrancaba — `NoClassDefFoundError: ServerProperties` al usar Spring Cloud 2025.0.0.
- **Causa:** Spring Cloud 2025.0.0 no era compatible con Spring Boot 4.0.6.
- **Solución:** Subir Spring Cloud a versión `2025.1.1` en el `pom.xml` de ms-cuentas y ms-transacciones.
- **Estado:** Corregido

---

## BUG-003 — ms-organizacion no compilaba por ErrorResponse faltante

- **Fecha:** 15/05/2026
- **Severidad:** Alta
- **Descripción:** El `GlobalException` importaba `com.novabank.msorganizacion.dto.response.ErrorResponse` pero la clase no existía.
- **Causa:** ErrorResponse no había sido creada cuando se levantó el manejador global.
- **Solución:** Crear la clase `ErrorResponse` en `dto/response/` con timestamp, status, path, message.
- **Estado:** Corregido

---

## BUG-004 — Transferencia podía quedar inconsistente si fallaba el deposito remoto

- **Fecha:** 14/05/2026
- **Severidad:** Alta
- **Descripción:** En `TransaccionService.crearTransaccion`, si el retiro vía Feign funcionaba pero el depósito fallaba, los datos quedaban inconsistentes (origen retirada, destino sin cambios).
- **Causa:** Faltaba marcar la transacción como RECHAZADA y propagar el error.
- **Solución:** Envolver el bloque en `try/catch` con `FeignException`, marcar estado RECHAZADA y lanzar `BusinessRuleException`. Usar `@Transactional` para rollback local.
- **Estado:** Corregido

---

## BUG-005 — Conflictos de merge entre branches

- **Fecha:** 15/05/2026
- **Severidad:** Baja
- **Descripción:** Al integrar la rama `ms-cuentas` con `main`, hubo conflictos en archivos compartidos (poms, README).
- **Causa:** Cambios paralelos no comunicados entre integrantes.
- **Solución:** Resolución manual de conflictos preservando ambas implementaciones donde aplicaba. Se mejoró la coordinación vía Trello.
- **Estado:** Corregido

---

## BUG-006 — Dependencias de test inexistentes en pom.xml

- **Fecha:** 10/06/2026 (aplicado por microservicio entre 10/06 y 12/06)
- **Severidad:** Media
- **Descripción:** Los `pom.xml` de los 4 microservicios tenían dependencias como `spring-boot-starter-data-jpa-test`, `spring-boot-starter-flyway-test`, `spring-boot-starter-validation-test`, `spring-boot-starter-webmvc-test` que NO EXISTEN en Spring Boot.
- **Causa:** Error al copiar nombres de dependencias durante el setup inicial.
- **Solución:** Reemplazar las dependencias inventadas por la única correcta `spring-boot-starter-test` que incluye JUnit 5, Mockito, AssertJ. Adicionalmente, se agregaron `net.datafaker:datafaker:2.4.2` y el plugin `org.jacoco:jacoco-maven-plugin:0.8.12` para soportar la generacion de datos de prueba y la medicion de cobertura.
- **Microservicios afectados y corregidos:**
  - ms-clientes (10/06/2026)
  - ms-cuentas (10/06/2026)
  - ms-transacciones (11/06/2026)
  - ms-organizacion (12/06/2026)
- **Estado:** Corregido

---

## BUG-007 — TransaccionRequestDTO con campo correoElectronico inconsistente

- **Fecha:** 10/06/2026
- **Severidad:** Baja
- **Descripción:** Versiones previas del DTO incluían el campo `correoElectronico` con `@Email` y `@NotNull`, pero no aportaba al flujo de la transacción y obligaba a enviarlo en cada request.
- **Causa:** Diseño inicial agregaba el correo por simetría con el cliente, sin un caso de uso real.
- **Solución:** Eliminar el campo del DTO de request. El correo se obtiene del cliente vía Feign si es necesario.
- **Estado:** Corregido

---

## BUG-008 — Commit inicial solo contenia archivos compilados, sin codigo fuente

- **Fecha:** 14/05/2026
- **Severidad:** Alta
- **Descripción:** El primer push a GitHub solo incluyó la carpeta `target/` con archivos `.class` compilados. No estaba el código fuente de Java.
- **Causa:** Faltaba `.gitignore` en la raíz que excluyera `target/` y la carpeta se subió en vez del código.
- **Solución:** Crear `.gitignore` raíz con exclusiones (`target/`, `.idea/`, `.DS_Store`), eliminar `target/` del index con `git rm -r --cached`, y traer el código fuente desde la rama de trabajo.
- **Estado:** Corregido

---

## BUG-009 — ms-organizacion en estado no funcional al integrarse al proyecto

- **Fecha:** 15/05/2026
- **Severidad:** Alta
- **Descripción:** ms-organizacion no compilaba ni arrancaba: `application.yaml` vacío, sin Flyway, sin Bean Validation, sin Swagger, services con `RuntimeException` cruda, rutas REST inconsistentes (`/api/v1/sucursal`, `/api/ejecutivos`), CRUD incompleto, dependencia MySQL en vez de PostgreSQL.
- **Causa:** Microservicio creado de forma independiente sin alinear estándares con los otros 3.
- **Solución:** Refactor completo: cambiar a PostgreSQL, añadir Flyway con migración `V1__inicio.sql`, reorganizar DTOs en `request/response`, agregar `@Slf4j` y `@Transactional`, normalizar rutas a `/api/v1/sucursales`, `/api/v1/ejecutivos`, `/api/v1/direcciones-sucursal`, agregar Swagger y validaciones tipadas.
- **Estado:** Corregido

---

## BUG-010 — Swagger UI mostraba "OpenAPI definition" en el dropdown

- **Fecha:** 16/05/2026
- **Severidad:** Baja
- **Descripción:** En el `swagger-ui` de cada microservicio, el desplegable de selección mostraba el texto por defecto "OpenAPI definition" en vez del nombre real del microservicio.
- **Causa:** Faltaba la configuración `springdoc.swagger-ui.urls` en `application.yaml`.
- **Solución:** Agregar a cada `application.yaml` la sección `springdoc.swagger-ui.urls` con el `name` y `url` propio del microservicio.
- **Estado:** Corregido

---

## BUG-011 — Migracion V2 de Flyway fallaba por emails duplicados en la tabla cliente

- **Fecha:** 14/05/2026
- **Severidad:** Media
- **Descripción:** Al aplicar la migración V2 que añadía la constraint `UNIQUE` al campo `email_cliente`, Flyway fallaba porque la tabla `cliente` tenía 3 emails duplicados.
- **Causa:** El esquema inicial no validaba unicidad, y se insertaron clientes con email repetido.
- **Solución:** Script Java standalone que conectaba a la BD vía JDBC, identificaba duplicados con `GROUP BY email_cliente HAVING COUNT(*) > 1`, y dejaba en NULL los emails duplicados. Después la migración pudo aplicarse sin errores.
- **Estado:** Corregido

---

## Bugs activos / Pendientes

(Vacío — actualmente sin bugs abiertos. Se irán agregando los que aparezcan durante Sumativa 3.)

---

## Resumen

| Severidad | Total | Corregidos | Abiertos |
|---|---|---|---|
| Alta | 6 | 6 | 0 |
| Media | 2 | 2 | 0 |
| Baja | 3 | 3 | 0 |
| **TOTAL** | **11** | **11** | **0** |

---

*Documento actualizado a medida que aparecen y se resuelven nuevos defectos.*
