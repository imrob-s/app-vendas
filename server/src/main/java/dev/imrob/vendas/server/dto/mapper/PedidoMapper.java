package dev.imrob.vendas.server.dto.mapper;

import dev.imrob.vendas.server.dto.*;
import dev.imrob.vendas.server.entity.Cliente;
import dev.imrob.vendas.server.entity.Pedido;
import dev.imrob.vendas.server.entity.ItemPedido;
import dev.imrob.vendas.server.entity.Produto;
import dev.imrob.vendas.server.repository.ClienteRepository;
import dev.imrob.vendas.server.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ClienteRepository.class, ProdutoRepository.class, ItemPedidoMapper.class})
public interface PedidoMapper {
    PedidoMapper INSTANCE = Mappers.getMapper(PedidoMapper.class);

    PedidoDTO toDto(Pedido pedido);
    Pedido toEntity(PedidoDTO pedidoDto);
    @Mapping(source = "clienteId", target = "cliente", qualifiedByName = "idToCliente")
    Pedido toEntity(CriarPedidoDTO criarPedidoDTO, @Context ClienteRepository repository);
    @Mapping(target = "produto", source = "produtoId", qualifiedByName = "idToProduto")
    ItemPedido toEntity(CriarItemPedidoDTO dto, @Context ProdutoRepository produtoRepository);
    @Mapping(target = "itens", source = "itens", qualifiedByName = "itensToDto")
    List<PedidoDTO> toDto(List<Pedido> pedidos);

    @Named("idToCliente")
    default Cliente idToCliente(Long clienteId, @Context ClienteRepository repository) {
        return repository.findById(clienteId).orElseThrow(
                () -> new EntityNotFoundException("Cliente com id %d não foi encontrado".formatted(clienteId))
        );
    }

    @Named("idToProduto")
    default Produto idToProduto(Long produtoId, @Context ProdutoRepository produtoRepository) {
        return produtoRepository.findById(produtoId).orElseThrow(
                () -> new EntityNotFoundException("Produto com id %d não foi encontrado".formatted(produtoId)));
    }

    @Named("itensToDto")
    default Set<ItemPedidoDTO> itensToDto(Set<ItemPedido> itens, @Context ItemPedidoMapper mapper) {
        return itens.stream().map(mapper::toDTO).collect(Collectors.toSet());
    }
}
