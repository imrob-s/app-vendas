package dev.imrob.vendas.server.service;

import dev.imrob.vendas.server.dto.PedidoDTO;
import dev.imrob.vendas.server.dto.mapper.PedidoMapper;
import dev.imrob.vendas.server.entity.Pedido;
import dev.imrob.vendas.server.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PedidoService implements CrudService<PedidoDTO> {
    private final PedidoRepository repository;
    private final PedidoMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public PedidoDTO findById(Long id) {
        Pedido pedido = idExisteOuException(id, Pedido.class);
        return mapper.toDto(pedido);
    }

    @Override
    public List<PedidoDTO> findAll() {
        return mapper.toDto(repository.findAll());
    }

    @Override
    public Long save(PedidoDTO dto) {
        validarCampos(dto);
        Pedido pedido = mapper.toEntity(dto);
        return repository.save(pedido).getId();
    }

    @Override
    public void update(PedidoDTO dto) {
        validarCampos(dto);
        Pedido pedido = idExisteOuException(dto.getId(), Pedido.class);
        repository.save(pedido);
    }

    @Override
    public void delete(Long id) {
        Pedido pedido = idExisteOuException(id, Pedido.class);
        repository.delete(pedido);
    }

    @Override
    public JpaRepository<?, Long> getRepository() {
        return repository;
    }
}
