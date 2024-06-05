package dev.imrob.vendas.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.imrob.vendas.server.dto.ProdutoDTO;
import dev.imrob.vendas.server.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProdutoController.class)
class ProdutoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProdutoService service;

    private ProdutoDTO produto;
    private List<ProdutoDTO> listaProdutos;

    @BeforeEach
    void setUp() {
        produto = new ProdutoDTO(null, "Coca Cola 2L", BigDecimal.valueOf(10.0));
        ProdutoDTO produto2 = new ProdutoDTO(2L, "Coca Cola ", BigDecimal.valueOf(9.98));
        ProdutoDTO produto3 = new ProdutoDTO(3L, "Arroz Broto Legal 5kg", BigDecimal.valueOf(27.32));
        ProdutoDTO produto4 = new ProdutoDTO(4L, "Feijão Preto 1kg", BigDecimal.valueOf(8.99));
        listaProdutos = List.of(produto, produto2, produto3, produto4);
    }
    @Test
    void save_deveSalvarProduto_quandoCamposValidos() throws Exception {
        when(service.save(any(ProdutoDTO.class))).thenReturn(1L);
        mockMvc.perform(post("/api/v1/produtos").contentType("application/json")
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    void findAll_deveRetornarListaDeProdutos() throws Exception {
        when(service.findAll()).thenReturn(listaProdutos);
        mockMvc.perform(get("/api/v1/produtos"))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(listaProdutos)))
                .andExpect(jsonPath("$[0].id").value(produto.getId()))
                .andExpect(jsonPath("$[0].descricao").value(produto.getDescricao()))
                .andExpect(jsonPath("$[0].preco").value(produto.getPreco()));
    }

    @Test
    void findById_deveRetornarProduto() throws Exception {
        when(service.findById(1L)).thenReturn(produto);
        mockMvc.perform(get("/api/v1/produtos/1"))
               .andExpect(status().isOk())
               .andExpect(content().json(objectMapper.writeValueAsString(produto)))
               .andExpect(jsonPath("$.id").value(produto.getId()))
               .andExpect(jsonPath("$.descricao").value(produto.getDescricao()))
               .andExpect(jsonPath("$.preco").value(produto.getPreco()));
    }

    @Test
    void delete_deveRetornarStatusOk_QuandoPedidoDeletado() throws Exception {
        Long pedidoId = 1L;
        doNothing().when(service).delete(pedidoId);

        mockMvc.perform(delete("/api/v1/produtos/{id}", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void update_deveRetornarStatusOk_QuandoProdutoAtualizado() throws Exception {
        ProdutoDTO produtoDTO = new ProdutoDTO(
                1L,
                "Coca Cola 2L",
                BigDecimal.valueOf(10.0)
        );
        doNothing().when(service).update(produtoDTO);

        mockMvc.perform(put("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDTO)))
                .andExpect(status().is2xxSuccessful());
    }

}