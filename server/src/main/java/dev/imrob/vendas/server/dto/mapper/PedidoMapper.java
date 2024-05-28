package dev.imrob.vendas.server.dto.mapper;

import dev.imrob.vendas.server.dto.PedidoDTO;
import dev.imrob.vendas.server.entity.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PedidoMapper {
    PedidoMapper INSTANCE = Mappers.getMapper(PedidoMapper.class);

    PedidoDTO toDto(Pedido pedido);
    Pedido toEntity(PedidoDTO pedidoDto);
    List<PedidoDTO> toDto(List<Pedido> pedidos);
}
