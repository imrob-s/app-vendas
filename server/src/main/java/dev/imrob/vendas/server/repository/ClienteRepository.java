package dev.imrob.vendas.server.repository;

import dev.imrob.vendas.server.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
