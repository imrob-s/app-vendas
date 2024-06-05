package dev.imrob.vendas.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.imrob.vendas.server.dto.CriarItemPedidoDTO;
import dev.imrob.vendas.server.dto.CriarPedidoDTO;
import dev.imrob.vendas.server.dto.PedidoDTO;
import dev.imrob.vendas.server.entity.ItemPedido;
import dev.imrob.vendas.server.entity.Produto;
import dev.imrob.vendas.server.entity.StatusPedido;
import dev.imrob.vendas.server.exception.LimiteCreditoException;
import dev.imrob.vendas.server.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoService service;

    private CriarPedidoDTO pedido;

    @BeforeEach
    void setUp() {
        Produto produto = new Produto(2L, "Coca Cola 2L", BigDecimal.valueOf(9.98));
        Set<CriarItemPedidoDTO> itens = Set.of(new CriarItemPedidoDTO(1L, 5));
        pedido = new CriarPedidoDTO(LocalDate.of(2024, 1, 1), 1L, itens,
                BigDecimal.valueOf(550.0));
    }
    @Test
    void save_deveSalvarPedido_quandoCamposValidos() throws Exception {
        when(service.criarPedido(any(CriarPedidoDTO.class))).thenReturn(1L);
        mockMvc.perform(post("/api/v1/pedidos/criar").contentType("application/json")
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    void cancelarPedido_deveRetornarStatusOk_QuandoPedidoCancelado() throws Exception {
        Long pedidoId = 1L;
        doNothing().when(service).cancelarPedido(anyLong());

        mockMvc.perform(put("/api/v1/pedidos/cancelar/{id}", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void totalComprasDesdeFechamento_deveRetornarTotalCompras_QuandoEncontrado() throws Exception {
        Long clienteId = 1L;
        BigDecimal totalEsperado = BigDecimal.valueOf(100.00);
        when(service.totalComprasDesdeFechamento(clienteId)).thenReturn(totalEsperado);
        mockMvc.perform(get("/api/v1/pedidos/total-compras?clienteId=1", clienteId))
                .andExpect(status().isOk())
                .andExpect(content().string(totalEsperado.toString()));
    }

    @Test
    void totalComprasDesdeData_deveRetornarTotalCompras_QuandoDataInformada() throws Exception {
        Long clienteId = 1L;
        LocalDate data = LocalDate.of(2024, 05, 01);
        BigDecimal totalEsperado = BigDecimal.valueOf(50.00);
        when(service.totalComprasDesdeData(clienteId, data)).thenReturn(totalEsperado);
        mockMvc.perform(get("/api/v1/pedidos/total-compras?clienteId=1&data=2024-05-01")
                        .param("data", "2024-05-01"))
                .andExpect(status().isOk())
                .andExpect(content().string(totalEsperado.toString()));
    }

    @Test
    void criarPedido_deveRetornarStatus422_quandoClienteExcederLimiteCompra() throws Exception {
        when(service.criarPedido(any(CriarPedidoDTO.class))).thenThrow(LimiteCreditoException.class);
        mockMvc.perform(post("/api/v1/pedidos/criar").contentType("application/json")
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getPedidosAgrupadosPorCliente_deveRetornarPedidosAgrupadosPorCliente_quandoExistirPedidos() throws Exception {
        List<Object[]> resultadoEsperado = List.of(
                new Object[]{1L, "Cliente 1", new BigDecimal("100.00")},
                new Object[]{2L, "Cliente 2", new BigDecimal("200.00")}
        );
        when(service.getPedidosAgrupadosPorCliente()).thenReturn(resultadoEsperado);

        mockMvc.perform(get("/api/v1/pedidos/por-cliente"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(resultadoEsperado)));
    }

    @Test
    void getPedidosAgrupadosPorProduto_deveRetornarPedidosAgrupadosPorProduto_quandoExistirPedidos() throws Exception {
        List<Object[]> resultadoEsperado = List.of(
                new Object[]{1L, "Produto 1", 10L, new BigDecimal("100.00")},
                new Object[]{2L, "Produto 2", 20L, new BigDecimal("200.00")}
        );
        when(service.getPedidosAgrupadosPorProduto()).thenReturn(resultadoEsperado);

        mockMvc.perform(get("/api/v1/pedidos/por-produto"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(resultadoEsperado)));
    }

    @Test
    void getPedidosFiltrados_deveRetornarPedidosFiltrados_quandoExistirPedidos() throws Exception {
        LocalDate dataInicio = LocalDate.of(2023, 1, 1);
        LocalDate dataFim = LocalDate.of(2023, 12, 31);
        Long clienteId = 1L;
        Long produtoId = 1L;
        StatusPedido status = StatusPedido.ATIVO;

        List<PedidoDTO> resultadoEsperado = List.of(new PedidoDTO(), new PedidoDTO());
        when(service.filtrarPedidosPor(dataInicio, dataFim, clienteId, produtoId, status))
                .thenReturn(resultadoEsperado);

        mockMvc.perform(get("/api/v1/pedidos/filtrar")
                        .param("dataInicio", "2023-01-01")
                        .param("dataFim", "2023-12-31")
                        .param("clienteId", "1")
                        .param("produtoId", "1")
                        .param("status", "ATIVO"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(resultadoEsperado)));
    }
}