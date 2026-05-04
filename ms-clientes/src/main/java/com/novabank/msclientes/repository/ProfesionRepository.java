package com.novabank.msclientes.repository;

import com.novabank.msclientes.model.Profesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesionRepository extends JpaRepository <Profesion, Long>{}
