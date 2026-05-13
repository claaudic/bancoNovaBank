ALTER TABLE cliente ADD COLUMN estado_str VARCHAR(15);
UPDATE cliente SET estado_str = CASE estado
    WHEN 0 THEN 'ACTIVO'
    WHEN 1 THEN 'INACTIVO'
    ELSE 'ACTIVO'
END;
ALTER TABLE cliente DROP COLUMN estado;
ALTER TABLE cliente RENAME COLUMN estado_str TO estado;
ALTER TABLE cliente ALTER COLUMN estado SET NOT NULL;
ALTER TABLE cliente ADD CONSTRAINT ck_cliente_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'));

ALTER TABLE direccion_cliente ADD COLUMN tipo_direccion_str VARCHAR(15);
UPDATE direccion_cliente SET tipo_direccion_str = CASE tipo_direccion
    WHEN 0 THEN 'RESIDENCIAL'
    WHEN 1 THEN 'COMERCIAL'
    ELSE 'RESIDENCIAL'
END;
ALTER TABLE direccion_cliente DROP COLUMN tipo_direccion;
ALTER TABLE direccion_cliente RENAME COLUMN tipo_direccion_str TO tipo_direccion;
ALTER TABLE direccion_cliente ALTER COLUMN tipo_direccion SET NOT NULL;
ALTER TABLE direccion_cliente ADD CONSTRAINT ck_direccion_tipo CHECK (tipo_direccion IN ('RESIDENCIAL', 'COMERCIAL'));

ALTER TABLE cliente ADD CONSTRAINT uc_cliente_email UNIQUE (email_cliente);
ALTER TABLE profesion ADD CONSTRAINT uc_profesion_nombre UNIQUE (nombre_profesion);

CREATE INDEX idx_cliente_estado ON cliente (estado);
CREATE INDEX idx_cliente_nombre ON cliente (nombre_cliente);
CREATE INDEX idx_cliente_apellido ON cliente (apellido_cliente);
CREATE INDEX idx_cliente_profesion ON cliente (id_profesion);

CREATE INDEX idx_direccion_cliente ON direccion_cliente (rut_cliente);
CREATE INDEX idx_direccion_ciudad ON direccion_cliente (ciudad);
CREATE INDEX idx_direccion_tipo ON direccion_cliente (tipo_direccion);
