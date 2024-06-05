package dev.imrob.vendas.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.imrob.vendas.server.dto.ClienteDTO;
import dev.imrob.vendas.server.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClienteController.class)
class ClienteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService service;

    private ClienteDTO cliente;

    @BeforeEach
    void setUp() {
        cliente = new ClienteDTO();
        cliente.setNome("João Barbosa");
        cliente.setLimiteCompra(BigDecimal.valueOf(5000.0));
        cliente.setDiaFechamentoFatura(10);
    }

    @Test
    void save_deveRetornarStatus422_quandoCamposInvalidos() throws Exception {
        cliente.setLimiteCompra(BigDecimal.ZERO);
        mockMvc.perform(post("/api/v1/clientes")
               .contentType("application/json")
               .content(objectMapper.writeValueAsString(cliente)))
               .andExpect(status().isUnprocessableEntity());

        cliente.setLimiteCompra(BigDecimal.valueOf(5000.0));
        cliente.setLimiteCompra(null);
        mockMvc.perform(post("/api/v1/clientes")
               .contentType("application/json")
               .content(objectMapper.writeValueAsString(cliente)))
               .andExpect(status().isUnprocessableEntity());

        cliente.setLimiteCompra(BigDecimal.valueOf(5000.0));
        cliente.setDiaFechamentoFatura(0);
        mockMvc.perform(post("/api/v1/clientes")
               .contentType("application/json")
               .content(objectMapper.writeValueAsString(cliente)))
               .andExpect(status().isUnprocessableEntity());

        cliente.setDiaFechamentoFatura(null);
        mockMvc.perform(post("/api/v1/clientes")
               .contentType("application/json")
               .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isUnprocessableEntity());

        cliente.setDiaFechamentoFatura(-1);
        mockMvc.perform(post("/api/v1/clientes")
               .contentType("application/json")
               .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isUnprocessableEntity());

        cliente.setDiaFechamentoFatura(32);
        mockMvc.perform(post("/api/v1/clientes")
               .contentType("application/json")
               .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isUnprocessableEntity());
    }
}