package dev.imrob.vendas.server.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "tb_pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Cliente é obrigatório")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cliente cliente;

    @NotNull(message = "Data é obrigatória")
    @Column(name = "data", updatable = false)
    private LocalDate data = LocalDate.now();

    @JsonManagedReference
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ItemPedido> itens = new HashSet<>();

    @NotNull(message = "Valor total é obrigatório")
    @Column(name = "valor_total", updatable = true)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private StatusPedido status = StatusPedido.ATIVO;
}

