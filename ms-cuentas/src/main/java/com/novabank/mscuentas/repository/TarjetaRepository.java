package com.novabank.mscuentas.repository;

import com.novabank.mscuentas.model.EstadoTarjeta;
import com.novabank.mscuentas.model.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Long> {

    boolean existsByNumeroTarjeta(String numeroTarjeta);

    boolean existsByCuentaIdCuenta(Long idCuenta);

    List<Tarjeta> findByCuentaIdCuenta(Long idCuenta);

    List<Tarjeta> findByCuentaIdCuentaAndEstado(Long idCuenta, EstadoTarjeta estado);

    List<Tarjeta> findByEstado(EstadoTarjeta estado);

    List<Tarjeta> findByFechaVencimientoBefore(LocalDate fecha);

    List<Tarjeta> findByCuentaRutCliente(String rutCliente);
}
