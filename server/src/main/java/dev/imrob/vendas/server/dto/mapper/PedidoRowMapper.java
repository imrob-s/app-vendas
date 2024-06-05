package dev.imrob.vendas.server.dto.mapper;

import dev.imrob.vendas.server.dto.ItemPedidoDTO;
import dev.imrob.vendas.server.dto.PedidoDTO;
import dev.imrob.vendas.server.entity.Cliente;
import dev.imrob.vendas.server.entity.ItemPedido;
import dev.imrob.vendas.server.entity.Pedido;
import dev.imrob.vendas.server.entity.StatusPedido;
import dev.imrob.vendas.server.repository.ClienteRepository;
import dev.imrob.vendas.server.repository.ItemPedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Component
public class PedidoRowMapper implements RowMapper<Pedido> {
    private ItemPedidoRepository itemPedidoRepository;
    private ClienteRepository clienteRepository;
    private ClienteMapper clienteMapper;

    @Autowired
    public PedidoRowMapper(ItemPedidoRepository itemPedidoRepository, ClienteRepository clienteRepository,
                           ClienteMapper clienteMapper) {
        this.itemPedidoRepository = itemPedidoRepository;
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public PedidoRowMapper() {
    }

    @Override
    public Pedido mapRow(ResultSet rs, int rowNum) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getLong("id"));
        pedido.setData(rs.getDate("data").toLocalDate());
        pedido.setValorTotal(rs.getBigDecimal("valor_total"));
        pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));
        Cliente cliente = clienteRepository.findById(rs.getLong("cliente_id")).orElseThrow(
                () -> new EntityNotFoundException("Cliente com não encontrado no momento da conversão.")
        );
        pedido.setCliente(cliente);
        Set<ItemPedido> itens = itemPedidoRepository.findAllByPedidoId(pedido.getId());
        pedido.setItens(itens);
        return pedido;
    }
}
