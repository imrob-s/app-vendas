package dev.imrob.vendas.server.service;

import dev.imrob.vendas.server.dto.ProdutoDTO;
import dev.imrob.vendas.server.dto.mapper.ProdutoMapper;
import dev.imrob.vendas.server.entity.Produto;
import dev.imrob.vendas.server.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProdutoService implements CrudService<ProdutoDTO> {
    private final ProdutoRepository repository;
    private final ProdutoMapper mapper;

    @Transactional
    @Override
    public ProdutoDTO findById(Long id) {
        Produto produto = idExisteOuException(id, Produto.class);
        return mapper.toDto(produto);
    }

    @Transactional
    @Override
    public List<ProdutoDTO> findAll() {
        List<Produto> produtos = repository.findAllByOrderByDescricaoAsc();
        return mapper.toDto(produtos);
    }

    @Transactional
    @Override
    public Long save(ProdutoDTO dto) {
        validarCampos(dto);
        Produto produto = mapper.toEntity(dto);
        return repository.save(produto).getId();
    }

    @Transactional
    @Override
    public void update(ProdutoDTO dto) {
        validarCampos(dto);
        idExisteOuException(dto.getId(), Produto.class);
        repository.save(mapper.toEntity(dto));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Produto produto = idExisteOuException(id, Produto.class);
        repository.delete(produto);
    }

    @Transactional
    @Override
    public JpaRepository<?, Long> getRepository() {
        return repository;
    }
}
