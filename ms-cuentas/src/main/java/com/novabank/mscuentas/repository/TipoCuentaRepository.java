package com.novabank.mscuentas.repository;

import com.novabank.mscuentas.model.TipoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoCuentaRepository extends JpaRepository<TipoCuenta, Long> {

    boolean existsByNombreTipoCuenta(String nombreTipoCuenta);

    List<TipoCuenta> findByNombreTipoCuentaContainingIgnoreCase(String nombre);
}
