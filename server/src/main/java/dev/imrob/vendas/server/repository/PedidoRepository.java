package dev.imrob.vendas.server.repository;

import dev.imrob.vendas.server.entity.Pedido;
import dev.imrob.vendas.server.entity.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    /**
     * Calcula o total de compras realizadas por um cliente a partir de uma data específica.
     *
     * @param clienteId O ID do cliente.
     * @param data      A data a partir da qual o total de compras deve ser calculado.
     * @return O valor total das compras realizadas pelo cliente desde a data especificada.
     */
    @Query(value = """
            SELECT sum(pr.preco * ip.quantidade) FROM tb_pedido p
            INNER JOIN tb_item_pedido ip ON ip.pedido_id = p.id
            INNER JOIN tb_produto pr ON ip.produto_id = pr.id
            WHERE p.cliente_id = :clienteId AND p.data > :data
            """, nativeQuery = true)
    BigDecimal totalComprasDesdeData(@Param("clienteId") Long clienteId, @Param("data") LocalDate data);

    /**
     * Retorna uma lista de pedidos agrupados por cliente, juntamente com o total gasto por cada cliente.
     *
     * @return Uma lista de arrays de objetos, onde cada array representa um cliente e seu total gasto:
     *         <ul>
     *         <li>Object[0]: {@link Long} - ID do cliente.</li>
     *         <li>Object[1]: {@link String} - Nome do cliente.</li>
     *         <li>Object[2]: {@link BigDecimal} - Total gasto pelo cliente.</li>
     *         </ul>
     */
    @Query(value = """
            SELECT c.id, c.nome, SUM(p.valor_total) AS total_compras
            FROM tb_pedido p
            JOIN tb_cliente c ON p.cliente_id = c.id
            GROUP BY c.id, c.nome
            """, nativeQuery = true)
    List<Object[]> pedidosAgrupadosPorCliente();

    /**
     * Retorna uma lista de produtos que foram incluídos em pedidos, juntamente com a
     * quantidade total pedida e o valor total para cada produto.
     *
     * @return Uma lista de arrays de objetos, onde cada array representa um produto e suas estatísticas de pedido:
     *         <ul>
     *         <li>Object[0]: {@link Long} - ID do produto.</li>
     *         <li>Object[1]: {@link String} - Nome do produto.</li>
     *         <li>Object[2]: {@link Long} - Quantidade total pedida do produto.</li>
     *         <li>Object[3]: {@link BigDecimal} - Valor total dos pedidos para o produto.</li>
     *         </ul>
     */
    @Query(value = """
            SELECT pr.id, pr.descricao, SUM(ip.quantidade) AS quantidade, SUM(pr.preco * ip.quantidade) AS total_vendido
            FROM tb_pedido p
            JOIN tb_item_pedido ip ON p.id = ip.pedido_id
            JOIN tb_produto pr ON ip.produto_id = pr.id
            GROUP BY pr.id, pr.descricao;
            """, nativeQuery = true)
    List<Object[]> pedidosAgrupadosPorProduto();
}
