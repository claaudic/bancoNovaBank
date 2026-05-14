package com.novabank.mscuentas.repository;

import com.novabank.mscuentas.model.Cuenta;
import com.novabank.mscuentas.model.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    boolean existsByNumeroCuenta(String numeroCuenta);

    boolean existsByTipoCuentaIdTipoCuenta(Long idTipoCuenta);

    List<Cuenta> findByRutCliente(String rutCliente);

    List<Cuenta> findByRutClienteAndEstado(String rutCliente, EstadoCuenta estado);

    List<Cuenta> findByEstado(EstadoCuenta estado);

    List<Cuenta> findByFechaCreacionBetween(LocalDate desde, LocalDate hasta);

    long countByEstado(EstadoCuenta estado);

    @Query("SELECT COALESCE(SUM(c.saldo), 0) FROM Cuenta c WHERE c.rutCliente = :rutCliente AND c.estado = com.novabank.mscuentas.model.EstadoCuenta.ACTIVA")
    BigDecimal saldoTotalActivoPorRutCliente(@Param("rutCliente") String rutCliente);
}
