package dev.imrob.vendas.server.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ItemPedidoDTO {
    private Long id;

    @NotNull(message = "Pedido é obrigatório")
    private PedidoDTO pedido;

    @NotNull(message = "Produto é obrigatório")
    private ProdutoDTO produto;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    private int quantidade;
}
