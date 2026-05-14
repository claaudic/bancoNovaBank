package com.novabank.msorganizacion.repository;

import com.novabank.msorganizacion.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
}
