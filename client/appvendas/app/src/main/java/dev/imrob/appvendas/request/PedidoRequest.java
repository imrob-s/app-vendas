
package dev.imrob.appvendas.request;

import dev.imrob.appvendas.entity.Pedido;
import dev.imrob.appvendas.entity.StatusPedido;
import dev.imrob.appvendas.entity.dto.CriarPedidoDTO;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Rob
 */
public interface PedidoRequest {
    @RequestLine("POST /pedidos/criar")
    @Headers("Content-Type: application/json")
    Long criarPedido(CriarPedidoDTO dto);

    @RequestLine("PUT /pedidos/cancelar/{id}")
    @Headers("Content-Type: application/json")
    String cancelarPedido(@Param("id") Long id);

    @RequestLine("GET /pedidos/total-compras?clienteId={clienteId}&data={data}")
    @Headers("Content-Type: application/json")
    BigDecimal totalComprasDesdeData(@Param("clienteId") Long clienteId, @Param("data") LocalDate data);

    @RequestLine("GET /pedidos/por-cliente")
    @Headers("Content-Type: application/json")
    List<Object[]> getPedidosAgrupadosPorCliente();

    @RequestLine("GET /pedidos/por-produto")
    @Headers("Content-Type: application/json")
    List<Object[]> getPedidosAgrupadosPorProduto();

    @RequestLine("GET /pedidos/filtrar?dataInicio={dataInicio}&dataFim={dataFim}&clienteId={clienteId}&produtoId={produtoId}&status={status}")
    @Headers("Content-Type: application/json")
    List<Pedido> getPedidosFiltrados(@Param("dataInicio") LocalDate dataInicio,
                                        @Param("dataFim") LocalDate dataFim,
                                        @Param("clienteId") Long clienteId,
                                        @Param("produtoId") Long produtoId,
                                        @Param("status") StatusPedido status);
}
