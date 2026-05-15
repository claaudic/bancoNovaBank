package com.novabank.msorganizacion.repository;

import com.novabank.msorganizacion.model.DireccionSucursal;
import com.novabank.msorganizacion.model.TipoDireccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionSucursalRepository extends JpaRepository<DireccionSucursal, Long> {

    boolean existsBySucursalIdSucursal(Long idSucursal);

    List<DireccionSucursal> findBySucursalIdSucursal(Long idSucursal);

    List<DireccionSucursal> findByCiudadIgnoreCase(String ciudad);

    List<DireccionSucursal> findByTipoDireccion(TipoDireccion tipoDireccion);
}
