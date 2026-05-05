package com.novabank.msclientes.repository;

import com.novabank.msclientes.model.Cliente;
import com.novabank.msclientes.model.DireccionCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionClienteRepository  extends JpaRepository <DireccionCliente, Long>{}

