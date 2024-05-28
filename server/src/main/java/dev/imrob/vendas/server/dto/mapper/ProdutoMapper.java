package dev.imrob.vendas.server.dto.mapper;

import dev.imrob.vendas.server.dto.ProdutoDTO;
import dev.imrob.vendas.server.entity.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProdutoMapper {
    ProdutoMapper INSTANCE = Mappers.getMapper(ProdutoMapper.class);

    ProdutoDTO toDto(Produto produto);
    Produto toEntity(ProdutoDTO produtoDTO);
    List<ProdutoDTO> toDto(List<Produto> produtos);

}
