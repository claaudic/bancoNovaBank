package com.novabank.msclientes.repository;

import com.novabank.msclientes.model.Cliente;
import com.novabank.msclientes.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    boolean existsByNumeroSerie(String numeroSerie);

    boolean existsByEmailCliente(String emailCliente);

    List<Cliente> findByEstado(Estado estado);

    List<Cliente> findByNombreClienteContainingIgnoreCaseOrApellidoClienteContainingIgnoreCase(
            String nombre, String apellido);

    List<Cliente> findByProfesionIdProfesion(Long idProfesion);

    long countByEstado(Estado estado);
}
