package com.novabank.msorganizacion.repository;

import com.novabank.msorganizacion.model.Ejecutivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EjecutivoRepository extends JpaRepository<Ejecutivo, Long> {

    List<Ejecutivo> findBySucursal_IdSucursal(Long idSucursal);
}
