
package dev.imrob.appvendas.entity;

import dev.imrob.appvendas.entity.dto.ClienteDTO;
import dev.imrob.appvendas.entity.dto.ItemPedidoDTO;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Rob
 */
public class Pedido {
    private Long id;
    private ClienteDTO cliente;
    private LocalDate data;
    private Set<ItemPedidoDTO> itens = new HashSet<>();
    private BigDecimal valorTotal;
    private StatusPedido status;

    public Pedido() {
    }

    public Pedido(Long id, ClienteDTO cliente, LocalDate data, BigDecimal valorTotal, StatusPedido status) {
        this.id = id;
        this.cliente = cliente;
        this.data = data;
        this.valorTotal = valorTotal;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Set<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(Set<ItemPedidoDTO> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }
    
    public Object[] toTableRow(int rowNum) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        NumberFormat nf = new DecimalFormat("R$ #,##0.00");
        return new Object[]{false, id, this, data==null ? "" :
        df.format(data), itens.size(), nf.format(valorTotal), status};
    }
    
    @Override
    public String toString() {
        return cliente==null ? "": cliente.toString();
    }
}
