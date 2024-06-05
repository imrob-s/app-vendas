package dev.imrob.vendas.server.dto;

import dev.imrob.vendas.server.entity.ItemPedido;
import dev.imrob.vendas.server.entity.StatusPedido;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriarPedidoDTO {
    @PastOrPresent(message = "Data não pode ser futura")
    private LocalDate data = LocalDate.now();

    @NotNull(message = "Id do cliente é obrigatório")
    @Positive(message = "O ID do cliente deve ser um valor positivo.")
    private Long clienteId;

    @NotEmpty(message = "Necessário ao menos um item")
    private Set<CriarItemPedidoDTO> itens = new HashSet<>();

    private BigDecimal valorTotal;

    @PostConstruct
    public void inicializar() {
        this.valorTotal = BigDecimal.ZERO;
    }
}
