package com.novabank.msclientes.repository;

import com.novabank.msclientes.model.Profesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfesionRepository extends JpaRepository<Profesion, Long> {

    boolean existsByNombreProfesion(String nombreProfesion);

    List<Profesion> findByNombreProfesionContainingIgnoreCase(String nombre);
}
