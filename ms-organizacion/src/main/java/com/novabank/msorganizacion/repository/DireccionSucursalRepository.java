package com.novabank.msorganizacion.repository;

import com.novabank.msorganizacion.model.DireccionSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionSucursalRepository extends JpaRepository<DireccionSucursal, Long> {
    List<DireccionSucursal> findBySucursal_IdSucursal(Long idSucursal);
}