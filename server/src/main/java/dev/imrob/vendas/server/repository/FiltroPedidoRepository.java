package dev.imrob.vendas.server.repository;

import dev.imrob.vendas.server.dto.mapper.PedidoRowMapper;
import dev.imrob.vendas.server.entity.Pedido;
import dev.imrob.vendas.server.entity.StatusPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class FiltroPedidoRepository {
    private final JdbcClient jdbcClient;
    private final PedidoRowMapper rowMapper;

    public List<Pedido> filtrarPedidosPor(LocalDate dataInicio, LocalDate dataFim, Long clienteId, Long produtoId, StatusPedido status) {
        StringBuilder sql = new StringBuilder("SELECT p.* FROM tb_pedido p ");
        sql.append("LEFT JOIN tb_item_pedido ip ON p.id = ip.pedido_id ");
        sql.append("LEFT JOIN tb_produto prod ON ip.produto_id = prod.id ");
        sql.append("WHERE 1=1 ");
        if (dataInicio != null) {
            sql.append("AND p.data >= :dataInicio ");
        }
        if (dataFim != null) {
            sql.append("AND p.data <= :dataFim ");
        }
        if (clienteId != null) {
            sql.append("AND p.cliente_id = :clienteId ");
        }
        if (produtoId != null) {
            sql.append("AND ip.produto_id = :produtoId ");
        }
        if (status != null) {
            sql.append("AND p.status = :status ");
        }
        sql.append("GROUP BY p.id ");

        assert status != null;
        return jdbcClient
                .sql(sql.toString())
                .param("dataInicio", dataInicio)
                .param("dataFim", dataFim)
                .param("clienteId", clienteId)
                .param("produtoId", produtoId)
                .param("status", status.name())
                .query(rowMapper)
                .list();
    }
}
