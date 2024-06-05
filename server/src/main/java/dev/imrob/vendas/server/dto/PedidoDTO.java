package dev.imrob.vendas.server.dto;

import dev.imrob.vendas.server.entity.StatusPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PedidoDTO {
    private Long id;
    private ClienteDTO cliente;
    private LocalDate data;
    private Set<ItemPedidoDTO> itens = new HashSet<>();
    private BigDecimal valorTotal;
    private StatusPedido status;
}
