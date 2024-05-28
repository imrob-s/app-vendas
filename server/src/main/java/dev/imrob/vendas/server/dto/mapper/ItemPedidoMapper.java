package dev.imrob.vendas.server.dto.mapper;

import dev.imrob.vendas.server.dto.ItemPedidoDTO;
import dev.imrob.vendas.server.entity.ItemPedido;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemPedidoMapper {
    ItemPedidoMapper INSTANCE = Mappers.getMapper(ItemPedidoMapper.class);

    ItemPedidoDTO toDTO(ItemPedido itemPedido);
    ItemPedido toEntity(ItemPedidoDTO itemPedidoDTO);
    List<ItemPedidoDTO> toDTO(List<ItemPedido> itemPedido);
}
