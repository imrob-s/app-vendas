package dev.imrob.vendas.server.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ClienteDTO {
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Pattern(regexp = "^[^0-9]*$", message = "Não é permitido inserir números no campo nome")
    private String nome;

    @NotNull(message = "Limite de compra é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Limite de compra deve ser maior que zero")
    private BigDecimal limiteCompra;

    @NotNull(message = "Necessário informar o dia de fechamento da fatura")
    @Min(value = 1, message = "Dia de fechamento deve ser entre 1 e 31")
    @Max(value = 31, message = "Dia de fechamento deve ser entre 1 e 31")
    private Integer diaFechamentoFatura;
}
