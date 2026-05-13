package com.novabank.mstransacciones.repository;

import com.novabank.mstransacciones.model.Estado;
import com.novabank.mstransacciones.model.TipoTransaccion;
import com.novabank.mstransacciones.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByIdCuentaOrigen(Long idCuentaOrigen);

    List<Transaccion> findByIdCuentaDestino(Long idCuentaDestino);

    List<Transaccion> findByIdCuentaOrigenOrIdCuentaDestino(Long idCuentaOrigen, Long idCuentaDestino);

    List<Transaccion> findByEstado(Estado estado);

    List<Transaccion> findByTipoTransaccion(TipoTransaccion tipoTransaccion);

    List<Transaccion> findByFechaTransaccionBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Transaccion> findByIdCuentaOrigenAndEstado(Long idCuentaOrigen, Estado estado);
}
