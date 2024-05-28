package dev.imrob.vendas.server.dto.mapper;

import dev.imrob.vendas.server.dto.ClienteDTO;
import dev.imrob.vendas.server.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClienteMapper {
    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    ClienteDTO toDTO(Cliente cliente);
    Cliente toEntity(ClienteDTO clienteDTO);
    List<ClienteDTO> toDTO(List<Cliente> clientes);
}
