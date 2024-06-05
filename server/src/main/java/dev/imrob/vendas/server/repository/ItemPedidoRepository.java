package dev.imrob.vendas.server.repository;

import dev.imrob.vendas.server.dto.ItemPedidoDTO;
import dev.imrob.vendas.server.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    Set<ItemPedido> findAllByPedidoId(Long pedidoId);
}
