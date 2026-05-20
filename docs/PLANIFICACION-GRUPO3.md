# Planificación — Grupo 3

Distribución de tareas del proyecto **Banco NovaBank**.

## Integrantes

- **Claudia Cardoza** — ms-clientes, ms-cuentas, documentación
- **Cristopher Camus** — ms-transacciones, ms-organizacion

---

## Tareas

| Tarea | Responsable | Microservicio | Estado | Fecha |
|---|---|---|---|---|
| Setup inicial del repositorio GitHub | Claudia Cardoza | General | Hecho | 03-05-2026 |
| Configuración base Spring Boot + Maven | Claudia Cardoza | General | Hecho | 03-05-2026 |
| Crear entidades Cliente / Profesion / DireccionCliente | Claudia Cardoza | ms-clientes | Hecho | 04-05-2026 |
| Implementar ClienteRepository y ProfesionRepository | Claudia Cardoza | ms-clientes | Hecho | 04-05-2026 |
| Implementar ClienteService con reglas de negocio | Claudia Cardoza | ms-clientes | Hecho | 04-05-2026 |
| Implementar ClienteController con endpoints REST | Claudia Cardoza | ms-clientes | Hecho | 04-05-2026 |
| Bean Validation en RequestDTOs ms-clientes | Claudia Cardoza | ms-clientes | Hecho | 05-05-2026 |
| GlobalExceptionHandler ms-clientes | Claudia Cardoza | ms-clientes | Hecho | 05-05-2026 |
| Flyway V1 con seeds iniciales ms-clientes | Claudia Cardoza | ms-clientes | Hecho | 05-05-2026 |
| Crear entidades Cuenta / TipoCuenta / Tarjeta | Claudia Cardoza | ms-cuentas | Hecho | 13-05-2026 |
| Implementar CuentaService con depósitos y retiros | Claudia Cardoza | ms-cuentas | Hecho | 13-05-2026 |
| Implementar CuentaController y TarjetaController | Claudia Cardoza | ms-cuentas | Hecho | 14-05-2026 |
| Feign Client ms-cuentas hacia ms-clientes | Claudia Cardoza | ms-cuentas | Hecho | 14-05-2026 |
| Manejo de FeignException en ms-cuentas | Claudia Cardoza | ms-cuentas | Hecho | 14-05-2026 |
| Seeds Flyway V2 con cuentas y tarjetas | Claudia Cardoza | ms-cuentas | Hecho | 19-05-2026 |
| Crear entidad Transaccion con estados | Cristopher Camus | ms-transacciones | Hecho | 04-05-2026 |
| Implementar TransaccionService con lógica de transferencia | Cristopher Camus | ms-transacciones | Hecho | 05-05-2026 |
| Feign Client ms-transacciones hacia ms-cuentas | Cristopher Camus | ms-transacciones | Hecho | 13-05-2026 |
| `@Transactional` en transferencia (apoyo Claudia) | Compartido | ms-transacciones | Hecho | 14-05-2026 |
| Implementar TransaccionController | Cristopher Camus | ms-transacciones | Hecho | 05-05-2026 |
| Seeds Flyway V2 ms-transacciones | Cristopher Camus | ms-transacciones | Hecho | 15-05-2026 |
| Crear entidades Sucursal / Ejecutivo / DireccionSucursal | Cristopher Camus | ms-organizacion | Hecho | 06-05-2026 |
| Services y Controllers ms-organizacion | Cristopher Camus | ms-organizacion | Hecho | 06-05-2026 |
| Configurar Flyway ms-organizacion (apoyo Claudia) | Compartido | ms-organizacion | Hecho | 14-05-2026 |
| Refactor ms-organizacion (apoyo Claudia) | Compartido | ms-organizacion | Hecho | 15-05-2026 |
| Configurar Swagger en los 4 microservicios | Claudia Cardoza | General | Hecho | 15-05-2026 |
| README con stack, arquitectura y pasos | Claudia Cardoza | General | Hecho | 16-05-2026 |
| Diagrama entidad-relación en `docs/` | Claudia Cardoza | General | Hecho | 15-05-2026 |
| Resolver conflictos de merge | Compartido | General | Hecho | 15-05-2026 |
| Pruebas de integración vía Swagger | Compartido | General | Hecho | 19-05-2026 |

---

## Resumen de distribución

| Responsable | Tareas |
|---|---|
| Claudia Cardoza | 17 |
| Cristopher Camus | 8 |
| Compartido | 5 |

**Total:** 30 tareas completadas.
