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

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController extends CrudController<PedidoDTO, PedidoService> {

    @PostMapping("/criar")
    public ResponseEntity<Long> criarPedido(@Valid @RequestBody CriarPedidoDTO dto) {
        Long id = getService().criarPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PutMapping(value = "/cancelar/{id}")
    public ResponseEntity<Void> cancelarPedido(@PathVariable("id") Long id) {
        getService().cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

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

    @GetMapping("/por-cliente")
    public ResponseEntity<List<Object[]>> getPedidosAgrupadosPorCliente() {
        List<Object[]> result = getService().getPedidosAgrupadosPorCliente();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/por-produto")
    public ResponseEntity<List<Object[]>> getPedidosAgrupadosPorProduto() {
        List<Object[]> result = getService().getPedidosAgrupadosPorProduto();
        return ResponseEntity.ok(result);
    }

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
