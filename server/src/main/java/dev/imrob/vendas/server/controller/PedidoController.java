package dev.imrob.vendas.server.controller;

import dev.imrob.vendas.server.dto.CriarPedidoDTO;
import dev.imrob.vendas.server.dto.PedidoDTO;
import dev.imrob.vendas.server.entity.Pedido;
import dev.imrob.vendas.server.entity.StatusPedido;
import dev.imrob.vendas.server.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gerenciar pedidos.
 *
 * <p>Este controlador fornece endpoints para criar, cancelar, buscar e filtrar pedidos.
 * Ele também fornece endpoints para calcular o total de compras de um cliente desde uma data específica
 * e para obter pedidos agrupados por cliente ou produto.</p>
 *
 * @author Robson Silva
 */
@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController extends CrudController<PedidoDTO, PedidoService> {

    /**
     * Cria um novo pedido.
     *
     * @param dto O DTO contendo os dados do novo pedido.
     * @return O ID do pedido criado, com status HTTP 201 (Created).
     */
    @PostMapping("/criar")
    public ResponseEntity<Long> criarPedido(@Valid @RequestBody CriarPedidoDTO dto) {
        Long id = getService().criarPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    /**
     * Cancela um pedido existente.
     *
     * @param id O ID do pedido a ser cancelado.
     * @return Status HTTP 204 (No Content) se o pedido for cancelado com sucesso.
     */
    @PutMapping(value = "/cancelar/{id}")
    public ResponseEntity<Void> cancelarPedido(@PathVariable("id") Long id) {
        getService().cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Calcula o total de compras de um cliente desde uma data específica ou desde o último fechamento da fatura.
     *
     * @param clienteId O ID do cliente.
     * @param data A data a partir da qual o total de compras deve ser calculado (opcional).
     *             Se não for informada, o total será calculado desde o último fechamento da fatura.
     * @return O total de compras do cliente, com status HTTP 200 (OK).
     */
    @GetMapping("/total-compras")
    public ResponseEntity<BigDecimal> totalComprasDesdeData(
            @RequestParam Long clienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        if (data == null) {
        BigDecimal total = getService().totalComprasDesdeFechamento(clienteId);
        return ResponseEntity.ok(total);
        }
        BigDecimal total = getService().totalComprasDesdeData(clienteId, data);
        return ResponseEntity.ok(total);
    }

    /**
     * Retorna uma lista de pedidos agrupados por cliente, juntamente com o total gasto por cada cliente.
     *
     * @return Uma lista de arrays de objetos, onde cada array representa um cliente e seu total gasto,
     *  com status HTTP 200 (OK).
     */
    @GetMapping("/por-cliente")
    public ResponseEntity<List<Object[]>> getPedidosAgrupadosPorCliente() {
        List<Object[]> result = getService().getPedidosAgrupadosPorCliente();
        return ResponseEntity.ok(result);
    }

    /**
     * Retorna uma lista de pedidos agrupados por produto, juntamente com o total de vendas por cada produto.
     *
     * @return Uma lista de arrays de objetos, onde cada array representa um produto e seu total vendido,
     *  com status HTTP 200 (OK).
     */
    @GetMapping("/por-produto")
    public ResponseEntity<List<Object[]>> getPedidosAgrupadosPorProduto() {
        List<Object[]> result = getService().getPedidosAgrupadosPorProduto();
        return ResponseEntity.ok(result);
    }

    /**
     * Busca pedidos com base em vários critérios de filtro.
     *
     * @param dataInicio Data inicial do intervalo de datas para filtrar pedidos, inclusive (opcional).
     * @param dataFim   Data final do intervalo de datas para filtrar pedidos, inclusive (opcional).
     * @param clienteId ID do cliente para filtrar pedidos (opcional).
     * @param produtoId ID do produto para filtrar pedidos (opcional).
     * @param status     Status do pedido para filtrar (opcional).
     * @return Uma lista de pedidos que correspondem aos critérios de filtro fornecidos,
     *  com status HTTP 200 (OK).
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<PedidoDTO>> getPedidosFiltrados(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long produtoId,
            @RequestParam(required = false) StatusPedido status) {
        List<PedidoDTO> result = getService().filtrarPedidosPor(dataInicio, dataFim, clienteId, produtoId, status);
        return ResponseEntity.ok(result);
    }
}
