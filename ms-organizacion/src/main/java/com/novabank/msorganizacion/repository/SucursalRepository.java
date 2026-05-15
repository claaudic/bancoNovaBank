package com.novabank.msorganizacion.repository;

import com.novabank.msorganizacion.model.Estado;
import com.novabank.msorganizacion.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    boolean existsByEmail(String email);

    boolean existsByNombre(String nombre);

    List<Sucursal> findByEstado(Estado estado);

    List<Sucursal> findByNombreContainingIgnoreCase(String nombre);
}
