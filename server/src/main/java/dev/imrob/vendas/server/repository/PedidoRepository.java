package dev.imrob.vendas.server.repository;

import dev.imrob.vendas.server.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
