package dev.imrob.vendas.server.service;

import dev.imrob.vendas.server.dto.ClienteDTO;
import dev.imrob.vendas.server.dto.mapper.ClienteMapper;
import dev.imrob.vendas.server.entity.Cliente;
import dev.imrob.vendas.server.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClienteService implements CrudService<ClienteDTO> {
    private final ClienteRepository repository;
    private final ClienteMapper mapper;
    @Override
    public ClienteDTO findById(Long id) {
        Cliente cliente = idExisteOuException(id, Cliente.class);
        return mapper.toDTO(cliente);
    }

    @Override
    public List<ClienteDTO> findAll() {
        List<Cliente> clientes = repository.findAll();
        return mapper.toDTO(clientes);
    }

    @Override
    public Long save(ClienteDTO dto) {
        validarCampos(dto);
        Cliente cliente = mapper.toEntity(dto);
        return repository.save(cliente).getId();
    }

    @Override
    public void update(ClienteDTO dto) {
        validarCampos(dto);
        Cliente cliente = idExisteOuException(dto.getId(), Cliente.class);
        repository.save(cliente);
    }

    @Override
    public void delete(Long id) {
        Cliente cliente = idExisteOuException(id, Cliente.class);
        repository.delete(cliente);
    }

    @Override
    public JpaRepository<?, Long> getRepository() {
        return repository;
    }
}
