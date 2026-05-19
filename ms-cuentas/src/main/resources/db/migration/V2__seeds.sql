-- Seeds de cuentas y tarjetas
-- Los rutCliente referencian a clientes seed de ms-clientes:
--   11111111-1 (Juan Perez)
--   22222222-2 (Maria Gonzalez)
--   33333333-3 (Pedro Ramirez)
-- Los tipos de cuenta vienen de V1: 1=CORRIENTE, 2=AHORRO, 3=VISTA

INSERT INTO cuenta (numero_cuenta, fecha_creacion, saldo, rut_cliente, estado, id_tipo_cuenta) VALUES
('10000001', CURRENT_DATE, 500000.00, '11111111-1', 'ACTIVA', 1),
('10000002', CURRENT_DATE, 1200000.00, '22222222-2', 'ACTIVA', 2),
('10000003', CURRENT_DATE, 250000.00, '33333333-3', 'ACTIVA', 3),
('10000004', CURRENT_DATE, 80000.00, '11111111-1', 'ACTIVA', 2);

INSERT INTO tarjeta (numero_tarjeta, fecha_vencimiento, cvv, estado, id_cuenta) VALUES
('4532123456789012', '2028-12-31', '123', 'ACTIVA', 1),
('4532987654321098', '2027-06-30', '456', 'ACTIVA', 2),
('5500111122223333', '2029-03-31', '789', 'ACTIVA', 3);
