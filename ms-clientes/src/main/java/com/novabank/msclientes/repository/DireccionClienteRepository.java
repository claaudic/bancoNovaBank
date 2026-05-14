package com.novabank.msclientes.repository;

import com.novabank.msclientes.model.DireccionCliente;
import com.novabank.msclientes.model.TipoDireccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionClienteRepository extends JpaRepository<DireccionCliente, Long> {

    List<DireccionCliente> findByClienteRutCliente(String rutCliente);

    List<DireccionCliente> findByCiudadIgnoreCase(String ciudad);

    List<DireccionCliente> findByTipoDireccion(TipoDireccion tipoDireccion);
}
