package com.novabank.msorganizacion.repository;

import com.novabank.msorganizacion.model.Ejecutivo;
import com.novabank.msorganizacion.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjecutivoRepository extends JpaRepository<Ejecutivo, Long> {

    boolean existsByEmail(String email);

    boolean existsBySucursalIdSucursal(Long idSucursal);

    List<Ejecutivo> findBySucursalIdSucursal(Long idSucursal);

    List<Ejecutivo> findBySucursalIdSucursalAndEstado(Long idSucursal, Estado estado);

    List<Ejecutivo> findByEstado(Estado estado);
}
