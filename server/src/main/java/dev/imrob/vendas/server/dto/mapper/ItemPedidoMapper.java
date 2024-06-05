package dev.imrob.vendas.server.dto.mapper;

import dev.imrob.vendas.server.dto.ItemPedidoDTO;
import dev.imrob.vendas.server.entity.ItemPedido;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemPedidoMapper {
    ItemPedidoMapper INSTANCE = Mappers.getMapper(ItemPedidoMapper.class);

    ItemPedidoDTO toDTO(ItemPedido itemPedido);
    ItemPedido toEntity(ItemPedidoDTO itemPedidoDTO);
    Set<ItemPedidoDTO> toDTO(Set<ItemPedido> itemPedido);
}
