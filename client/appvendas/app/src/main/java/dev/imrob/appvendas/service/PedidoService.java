
package dev.imrob.appvendas.service;

import dev.imrob.appvendas.request.PedidoRequest;
import dev.imrob.appvendas.config.FeignConfig;
import dev.imrob.appvendas.entity.Pedido;
import dev.imrob.appvendas.entity.StatusPedido;
import dev.imrob.appvendas.entity.dto.CriarPedidoDTO;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Rob
 */
public class PedidoService {
    private final PedidoRequest pedidoClient;
    NumberFormat nf = new DecimalFormat("R$ #,##0.00");

    public PedidoService() {
        this.pedidoClient = FeignConfig.iniciar(PedidoRequest.class);
    }

    public Long criarPedido(CriarPedidoDTO dto) {
        return pedidoClient.criarPedido(dto);
    }

    public String cancelarPedido(Long id) {
        return pedidoClient.cancelarPedido(id);
    }

    public BigDecimal totalComprasDesdeData(Long clienteId, LocalDate data) {
        return pedidoClient.totalComprasDesdeData(clienteId, data);
    }

    public List<Object[]> getPedidosAgrupadosPorCliente() {
        
        List<Object[]> resultados = pedidoClient.getPedidosAgrupadosPorCliente();

        for (Object[] resultado : resultados) {
            resultado[2] = nf.format(resultado[2]); 
        }

        return resultados;
    }

    public List<Object[]> getPedidosAgrupadosPorProduto() {
        List<Object[]> resultados = pedidoClient.getPedidosAgrupadosPorProduto();

        for (Object[] resultado : resultados) {
            resultado[3] = nf.format(resultado[3]);
        }

        return resultados;
    }

    public List<Pedido> getPedidosFiltrados(LocalDate dataInicio, LocalDate dataFim, Long clienteId, Long produtoId, StatusPedido status) {
        return pedidoClient.getPedidosFiltrados(dataInicio, dataFim, clienteId, produtoId, status);
    }
}
