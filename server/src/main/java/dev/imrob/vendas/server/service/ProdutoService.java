package dev.imrob.vendas.server.service;

import dev.imrob.vendas.server.dto.ProdutoDTO;
import dev.imrob.vendas.server.dto.mapper.ProdutoMapper;
import dev.imrob.vendas.server.entity.Produto;
import dev.imrob.vendas.server.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProdutoService implements CrudService<ProdutoDTO> {
    private final ProdutoRepository repository;
    private final ProdutoMapper mapper;

    @Override
    public ProdutoDTO findById(Long id) {
        Produto produto = idExisteOuException(id, Produto.class);
        return mapper.toDto(produto);
    }

    @Override
    public List<ProdutoDTO> findAll() {
        return mapper.toDto(repository.findAll());
    }

    @Override
    public Long save(ProdutoDTO dto) {
        validarCampos(dto);
        Produto produto = mapper.toEntity(dto);
        return repository.save(produto).getId();
    }

    @Override
    public void update(ProdutoDTO dto) {
        validarCampos(dto);
        Produto produto = idExisteOuException(dto.getId(), Produto.class);
        repository.save(produto);
    }

    @Override
    public void delete(Long id) {
        Produto produto = idExisteOuException(id, Produto.class);
        repository.delete(produto);
    }

    @Override
    public JpaRepository<?, Long> getRepository() {
        return repository;
    }
}
