package dev.imrob.vendas.server.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PedidoDTO {
    private Long id;

    @NotNull(message = "Cliente é obrigatório")
    private ClienteDTO cliente;

    @NotNull(message = "Data é obrigatória")
    @Column(name = "data", updatable = false)
    private LocalDateTime dataHora;
}
