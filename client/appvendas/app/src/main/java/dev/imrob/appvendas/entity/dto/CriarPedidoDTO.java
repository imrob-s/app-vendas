
package dev.imrob.appvendas.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Rob
 */
public class CriarPedidoDTO {
    private LocalDate data = LocalDate.now();
    private Long clienteId;
    private Set<CriarItemPedidoDTO> itens = new HashSet<>();
    private BigDecimal valorTotal;

    public CriarPedidoDTO() {
    }

    public CriarPedidoDTO(Long clienteId, BigDecimal valorTotal) {
        this.clienteId = clienteId;
        this.valorTotal = valorTotal;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Set<CriarItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(Set<CriarItemPedidoDTO> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
    
    
}
