package dev.imrob.vendas.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Cliente é obrigatório")
    private Cliente cliente;

    @NotNull(message = "Data é obrigatória")
    @Column(name = "data", updatable = false)
    private LocalDate data = LocalDate.now();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemPedido> itens = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private StatusPedido status = StatusPedido.ATIVO;
}

enum StatusPedido {
    ATIVO,
    EXCLUIDO
}
