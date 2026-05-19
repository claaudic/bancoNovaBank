-- Seeds de transacciones de ejemplo
-- Los idCuentaOrigen e idCuentaDestino referencian cuentas seed de ms-cuentas V2:
--   1 = cuenta de Juan Perez (CORRIENTE)
--   2 = cuenta de Maria Gonzalez (AHORRO)
--   3 = cuenta de Pedro Ramirez (VISTA)
--   4 = segunda cuenta de Juan Perez (AHORRO)

INSERT INTO transaccion (id_cuenta_origen, id_cuenta_destino, tipo_transaccion, monto_transaccion, fecha_transaccion, descripcion, estado) VALUES
(1, 1, 'DEPOSITO', 100000.00, NOW(), 'Deposito en sucursal', 'COMPLETADA'),
(2, 2, 'DEPOSITO', 250000.00, NOW(), 'Sueldo mensual', 'COMPLETADA'),
(1, 2, 'TRANSFERENCIA', 50000.00, NOW(), 'Pago a Maria', 'COMPLETADA'),
(3, 3, 'RETIRO', 20000.00, NOW(), 'Retiro cajero', 'COMPLETADA'),
(1, 4, 'TRANSFERENCIA', 30000.00, NOW(), 'Ahorro mensual', 'COMPLETADA'),
(2, 3, 'TRANSFERENCIA', 999999999.00, NOW(), 'Saldo insuficiente', 'RECHAZADA');
