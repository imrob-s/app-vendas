package dev.imrob.vendas.server.service;

import dev.imrob.vendas.server.dto.ClienteDTO;
import dev.imrob.vendas.server.dto.mapper.ClienteMapper;
import dev.imrob.vendas.server.entity.Cliente;
import dev.imrob.vendas.server.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    @InjectMocks
    private ClienteService service;
    @Mock
    private ClienteRepository repository;
    @Spy
    private ClienteMapper mapper = ClienteMapper.INSTANCE;

    private ClienteDTO clienteDTO;
    private Cliente cliente;
    private List<Cliente> listaClientes;

    @BeforeEach
    void setUp() {
        clienteDTO = new ClienteDTO();
        clienteDTO.setNome("João Barbosa");
        clienteDTO.setLimiteCompra(BigDecimal.valueOf(5000.0));
        clienteDTO.setDiaFechamentoFatura(10);

        cliente = mapper.toEntity(clienteDTO);
        cliente.setId(1L);

        Cliente cliente2 = new Cliente(2L, "Lilly Nascimento", BigDecimal.valueOf(10000.0), 15);
        Cliente cliente3 = new Cliente(3L, "Cassio Ramos", BigDecimal.valueOf(20000.0), 20);
        Cliente cliente4 = new Cliente(4L, "Carlos Miguel", BigDecimal.valueOf(30000.0), 30);
        listaClientes = List.of(cliente2, cliente3, cliente4);
    }

    @Test
    void findById_deveRetornarCliente_quandoEncontrado() {
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteDTO resultado = service.findById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(cliente.getId());
        assertThat(resultado.getNome()).isEqualTo(cliente.getNome());
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoEncontrado() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(100L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAll_deveRetornarListaDeClientes() {
        when(repository.findAll()).thenReturn(listaClientes);

        List<ClienteDTO> resultado = service.findAll();

        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(listaClientes.size());
        for (int i = 0; i < listaClientes.size(); i++) {
            assertThat(resultado.get(i).getId()).isEqualTo(listaClientes.get(i).getId());
            assertThat(resultado.get(i).getNome()).isEqualTo(listaClientes.get(i).getNome());
            assertThat(resultado.get(i).getLimiteCompra()).isEqualTo(listaClientes.get(i).getLimiteCompra());
            assertThat(resultado.get(i).getDiaFechamentoFatura()).isEqualTo(listaClientes.get(i).getDiaFechamentoFatura());
        }
    }

    @Test
    void save_deveSalvarCliente_retornaId() {
        when(repository.save(any(Cliente.class))).thenReturn(cliente);

        Long id = service.save(clienteDTO);

        assertEquals(cliente.getId(), id);
    }


    @Test
    void save_deveLancarExcecao_quandoCamposInvalidos() {
        ClienteDTO clienteDTOInvalido = new ClienteDTO();

        assertThatThrownBy(() -> service.save(clienteDTOInvalido)).isInstanceOf(ConstraintViolationException.class);

        clienteDTO.setNome("");
        assertThatThrownBy(() -> service.save(clienteDTO)).isInstanceOf(ConstraintViolationException.class);

        clienteDTO.setNome("   ");
        assertThatThrownBy(() -> service.save(clienteDTO)).isInstanceOf(ConstraintViolationException.class);

        clienteDTO.setNome("a");
        assertThatThrownBy(() -> service.save(clienteDTO)).isInstanceOf(ConstraintViolationException.class);

        clienteDTO.setNome("Paulo");
        clienteDTO.setLimiteCompra(BigDecimal.valueOf(-1000.0));
        assertThatThrownBy(() -> service.save(clienteDTO)).isInstanceOf(ConstraintViolationException.class);

        clienteDTO.setLimiteCompra(null);
        assertThatThrownBy(() -> service.save(clienteDTO)).isInstanceOf(ConstraintViolationException.class);

        clienteDTO.setLimiteCompra(BigDecimal.ZERO);
        assertThatThrownBy(() -> service.save(clienteDTO)).isInstanceOf(ConstraintViolationException.class);

        clienteDTO.setDiaFechamentoFatura(0);
        assertThatThrownBy(() -> service.save(clienteDTO)).isInstanceOf(ConstraintViolationException.class);

        clienteDTO.setDiaFechamentoFatura(null);
        assertThatThrownBy(() -> service.save(clienteDTO)).isInstanceOf(ConstraintViolationException.class);

        clienteDTO.setDiaFechamentoFatura(-1);
        assertThatThrownBy(() -> service.save(clienteDTO)).isInstanceOf(ConstraintViolationException.class);

        clienteDTO.setDiaFechamentoFatura(32);
        assertThatThrownBy(() -> service.save(clienteDTO)).isInstanceOf(ConstraintViolationException.class);
    }
    @Test
    void save_deveLancarExcecao_quandoHouverErroAoSalvarNoRepositorio() {
        when(repository.save(any(Cliente.class))).thenThrow(new RuntimeException("Erro ao salvar"));

        assertThatThrownBy(() -> service.save(clienteDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao salvar");
    }

    @Test
    void update_deveAtualizarCliente() {
        clienteDTO.setId(1L);
        when(repository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(repository.save(any(Cliente.class))).thenReturn(cliente);

        assertDoesNotThrow(() -> service.update(clienteDTO));
    }

    @Test
    void update_deveLancarExcecao_quandoNaoEncontrado() {
        clienteDTO.setId(1L);
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(clienteDTO)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_deveDeletarCliente() {
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
    }

    @Test
    void delete_deveLancarExcecao_quandoNaoEncontrado() {
        clienteDTO.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(EntityNotFoundException.class);
    }
}