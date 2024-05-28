package dev.imrob.vendas.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "tb_cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Pattern(regexp = "^[^0-9]*$", message = "Não é permitido inserir números neste campo")
    private String nome;

    @NotNull(message = "Limite de compra é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Limite de compra deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Limite de compra deve ser um valor decimal válido")
    private BigDecimal limiteCompra;

    @NotNull(message = "Necessário informar o dia de fechamento da fatura")
    @Min(value = 1, message = "Dia de fechamento deve ser entre 1 e 31")
    @Max(value = 31, message = "Dia de fechamento deve ser entre 1 e 31")
    private Integer diaFechamentoFatura;
}
