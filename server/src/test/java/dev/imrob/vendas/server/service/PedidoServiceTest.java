package dev.imrob.vendas.server.service;

import dev.imrob.vendas.server.dto.*;
import dev.imrob.vendas.server.dto.mapper.ClienteMapper;
import dev.imrob.vendas.server.dto.mapper.ItemPedidoMapper;
import dev.imrob.vendas.server.dto.mapper.PedidoMapper;
import dev.imrob.vendas.server.entity.Cliente;
import dev.imrob.vendas.server.entity.ItemPedido;
import dev.imrob.vendas.server.entity.Pedido;
import dev.imrob.vendas.server.entity.Produto;
import dev.imrob.vendas.server.entity.StatusPedido;
import dev.imrob.vendas.server.exception.LimiteCreditoException;
import dev.imrob.vendas.server.exception.MetodoNaoPermitidoException;
import dev.imrob.vendas.server.repository.ClienteRepository;
import dev.imrob.vendas.server.repository.FiltroPedidoRepository;
import dev.imrob.vendas.server.repository.PedidoRepository;
import dev.imrob.vendas.server.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @InjectMocks
    private PedidoService service;

    @Mock
    private PedidoRepository repository;

    @Mock
    private FiltroPedidoRepository filtroRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoMapper mapper;

    @Mock
    private ItemPedidoMapper itemPedidoMapper;

    @Mock
    private ClienteMapper clienteMapper;

    private PedidoDTO pedidoDTO;
    private Pedido pedido;
    private Cliente cliente;
    private Produto produto;
    private CriarPedidoDTO criarPedidoDTO;
    private CriarItemPedidoDTO criarItemPedidoDTO;

    @BeforeEach
    void setUp() {
        iniciarCliente();
        iniciarProduto();
        iniciarPedido();
        iniciarCriarItemPedidoDTO();
        iniciarCriarPedido();
    }

    @Test
    void findById_deveRetornarPedidoDTO_quandoEncontrado() {
        when(repository.findById(1L)).thenReturn(Optional.of(pedido));
        when(mapper.toDto(pedido)).thenReturn(pedidoDTO);
        PedidoDTO resultado = service.findById(1L);
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoEncontrado() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAll_deveRetornarListaDePedidoDTO_quandoExistirPedidos() {
        List<Pedido> pedidos = List.of(pedido, pedido, pedido, pedido);
        List<PedidoDTO> pedidosDTO = List.of(pedidoDTO, pedidoDTO, pedidoDTO, pedidoDTO);
        when(repository.findAll()).thenReturn(pedidos);
        when(mapper.toDto(pedidos)).thenReturn(pedidosDTO);
        List<PedidoDTO> resultado = service.findAll();
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(4);
    }

    @Test
    void save_deveSalvarPedido_quandoPedidoValido() {
        pedido.getItens().forEach(itemPedido -> itemPedido.setPedido(pedido));
        ItemPedido itemPedido = new ItemPedido(null, pedido, produto, 3);
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(repository.save(any(Pedido.class))).thenReturn(pedido);
        when(mapper.toEntity(any(), eq(clienteRepository))).thenReturn(pedido);
        when(mapper.toEntity(any(), eq(produtoRepository))).thenReturn(itemPedido);

        Long id = service.criarPedido(criarPedidoDTO);
        assertThat(id).isEqualTo(1L);
    }

    @Test
    void save_deveLancarExcecao_quandoClienteNaoEncontrado() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void save_deveCalcularValorTotal_quandoValorTotalIgualZero() {
        ItemPedido itemPedido = new ItemPedido(null, pedido, produto, 3);
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(produtoRepository.findAllById(anyCollection())).thenReturn(List.of(produto));
        when(repository.save(any(Pedido.class))).thenReturn(pedido);
        when(mapper.toEntity(criarPedidoDTO, clienteRepository)).thenReturn(pedido);
        when(mapper.toEntity(any(), eq(produtoRepository))).thenReturn(itemPedido);

        criarPedidoDTO.setValorTotal(BigDecimal.ZERO);
        Long id = service.criarPedido(criarPedidoDTO);

        assertThat(id).isEqualTo(1L);
        assertThat(criarPedidoDTO.getValorTotal()).isNotEqualTo(BigDecimal.ZERO);
    }

    @Test
    void save_deveLancarExcecao_quandoLimiteDeCreditoExcedido() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        cliente.setLimiteCompra(null);
        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO))
                .isInstanceOf(LimiteCreditoException.class);
    }

    @Test
    void save_deveLancarExcecao_quandoCamposInvalidos() {
        // Id do cliente nulo
        criarPedidoDTO.setData(LocalDate.now());
        criarPedidoDTO.setClienteId(null);
        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO)).isInstanceOf(ConstraintViolationException.class);

        // Data nula
        criarPedidoDTO.setData(null);
        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO)).isInstanceOf(ConstraintViolationException.class);

        // Data futura
        criarPedidoDTO.setData(LocalDate.now().plusDays(1));
        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO)).isInstanceOf(ConstraintViolationException.class);

        // Itens nulos
        criarPedidoDTO.setClienteId(null);
        criarPedidoDTO.setItens(null);
        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO)).isInstanceOf(ConstraintViolationException.class);

        // Valor Total nulo
        criarPedidoDTO.setItens(Set.of(new CriarItemPedidoDTO(1L , 2)));
        criarPedidoDTO.setValorTotal(null);
        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO)).isInstanceOf(ConstraintViolationException.class);

        // Valor Total negativo
        criarPedidoDTO.setValorTotal(BigDecimal.valueOf(-1));
        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void save_deveLancarExcecao_quandoItemPedidoInvalido() {
        // Produto nulo
        CriarItemPedidoDTO itemPedidoInvalido = new CriarItemPedidoDTO(null, 2);
        criarPedidoDTO.setItens(Set.of(itemPedidoInvalido));

        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO))
                .isInstanceOf(ConstraintViolationException.class);

        // Quantidade zero
        itemPedidoInvalido = new CriarItemPedidoDTO(1L, 0);
        criarPedidoDTO.setItens(Set.of(itemPedidoInvalido));

        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO))
                .isInstanceOf(ConstraintViolationException.class);

        // Quantidade negativa
        itemPedidoInvalido = new CriarItemPedidoDTO(1L, -1);
        criarPedidoDTO.setItens(Set.of(itemPedidoInvalido));

        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void save_deveLancarExcecao_quandoItensDoPedidoEstiveremVazios() {
        criarPedidoDTO.setItens(Collections.emptySet());

        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void save_deveLancarExcecao_quandoChamarMetodoNaoPermitido() {
        assertThatThrownBy(() -> service.save(pedidoDTO))
                .isInstanceOf(MetodoNaoPermitidoException.class);
    }

    @Test
    void save_deveLancarExcecao_quandoLimiteExcedido() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        cliente.setLimiteCompra(BigDecimal.ZERO);
        assertThatThrownBy(() -> service.criarPedido(criarPedidoDTO))
                .isInstanceOf(LimiteCreditoException.class);
    }

    @Test
    void update_deveLancarExcecao_quandoChamado() {
        assertThatThrownBy(() -> service.update(pedidoDTO))
                .isInstanceOf(MetodoNaoPermitidoException.class);
    }

    @Test
    void delete_deveDeletarPedido_quandoPedidoExistir() {
        when(repository.findById(1L)).thenReturn(Optional.of(pedido));
        service.delete(1L);
    }

    @Test
    void cancelarPedido_deveCancelarPedido_quandoPedidoExistir() {
        when(repository.findById(1L)).thenReturn(Optional.of(pedido));
        service.cancelarPedido(1L);
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.EXCLUIDO);
    }

    @Test
    void totalComprasDesdeFechamento_deveRetornarValorTotalDeCompras_QuandoClienteExistir() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(repository.totalComprasDesdeData(anyLong(), any(LocalDate.class)))
                .thenReturn(BigDecimal.TEN);
        BigDecimal resultado = service.totalComprasDesdeFechamento(1L);
        assertThat(resultado).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void totalComprasDesdeFechamento_deveLancarExcecao_QuandoClienteNaoExistir() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.totalComprasDesdeFechamento(1L));
    }

    @Test
    void totalComprasDesdeFechamento_naoDeveLancarExcecao_quandoDiaFechamentoFor31() {
        cliente.setDiaFechamentoFatura(31);
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(repository.totalComprasDesdeData(anyLong(), any(LocalDate.class)))
                .thenReturn(BigDecimal.TEN);
        assertDoesNotThrow(() -> service.totalComprasDesdeFechamento(1L));
    }

    @Test
    void totalComprasDesdeFechamento_deveCalcularCorretamente_QuandoDiaFechamentoMaiorQueDiaAtual() {
        cliente.setDiaFechamentoFatura(31);
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(repository.totalComprasDesdeData(eq(cliente.getId()), any(LocalDate.class))).thenReturn(BigDecimal.TEN);

        BigDecimal resultado = service.totalComprasDesdeFechamento(cliente.getId());

        assertThat(resultado).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void totalComprasDesdeFechamento_deveCalcularCorretamente_QuandoDiaFechamentoMenorQueDiaAtual() {
        cliente.setDiaFechamentoFatura(1);
        LocalDate dataEsperada = LocalDate.now().withDayOfMonth(cliente.getDiaFechamentoFatura());
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(repository.totalComprasDesdeData(anyLong(), eq(dataEsperada))).thenReturn(BigDecimal.TEN);

        BigDecimal resultado = service.totalComprasDesdeFechamento(1L);

        assertThat(resultado).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void totalComprasDesdeData_deveRetornarValorTotalDeCompras_quandoDataInformadaForValida() {
        LocalDate data = LocalDate.of(2023, 1, 1);
        when(repository.findById(1L)).thenReturn(Optional.of(pedido));
        when(repository.totalComprasDesdeData(1L, data)).thenReturn(BigDecimal.valueOf(50));

        BigDecimal resultado = service.totalComprasDesdeData(1L, data);

        assertThat(resultado).isEqualTo(BigDecimal.valueOf(50));
    }

    @Test
    void totalComprasDesdeData_deveLancarExcecao_quandoIdInformadoNaoExistir() {
        LocalDate data = LocalDate.of(2023, 1, 1);
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.totalComprasDesdeData(999L, data)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void filtrarPedidosPor_deveRetornarListaDePedidos_quandoHouverPedidosCorrespondentes() {
        LocalDate dataInicio = LocalDate.of(2023, 1, 1);
        LocalDate dataFim = LocalDate.of(2023, 12, 31);
        Long clienteId = 1L;
        Long produtoId = 1L;
        StatusPedido status = StatusPedido.ATIVO;

        List<Pedido> pedidos = List.of(pedido, pedido);
        when(filtroRepository.filtrarPedidosPor(dataInicio, dataFim, clienteId, produtoId, status))
                .thenReturn(pedidos);
        when(mapper.toDto(pedidos)).thenReturn(List.of(pedidoDTO, pedidoDTO));

        List<PedidoDTO> resultado = service.filtrarPedidosPor(dataInicio, dataFim, clienteId, produtoId, status);

        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
    }

    @Test
    void getTotalComprasDesdeData_deveRetornarValorTotalDeCompras_quandoDataInformadaForValida() {
        LocalDate data = LocalDate.of(2023, 1, 1);
        when(repository.totalComprasDesdeData(1L, data)).thenReturn(BigDecimal.valueOf(50));

        BigDecimal resultado = service.getTotalComprasDesdeData(1L, data);

        assertThat(resultado).isEqualTo(BigDecimal.valueOf(50));
    }

    @Test
    void getPedidosAgrupadosPorCliente_deveRetornarListaDePedidosAgrupadosPorCliente() {
        List<Object[]> resultadoEsperado = List.of(
                new Object[]{1L, "Cliente 1", BigDecimal.valueOf(100)},
                new Object[]{2L, "Cliente 2", BigDecimal.valueOf(200)}
        );
        when(repository.pedidosAgrupadosPorCliente()).thenReturn(resultadoEsperado);

        List<Object[]> resultado = service.getPedidosAgrupadosPorCliente();

        assertThat(resultado).isEqualTo(resultadoEsperado);
    }

    @Test
    void getPedidosAgrupadosPorProduto_deveRetornarListaDePedidosAgrupadosPorProduto() {
        List<Object[]> resultadoEsperado = List.of(
                new Object[]{1L, "Produto 1", 10L, BigDecimal.valueOf(100)},
                new Object[]{2L, "Produto 2", 20L, BigDecimal.valueOf(200)}
        );
        when(repository.pedidosAgrupadosPorProduto()).thenReturn(resultadoEsperado);

        List<Object[]> resultado = service.getPedidosAgrupadosPorProduto();

        assertThat(resultado).isEqualTo(resultadoEsperado);
    }

    private void iniciarCliente() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Roberto");
        cliente.setLimiteCompra(BigDecimal.valueOf(1000));
        cliente.setDiaFechamentoFatura(5);
    }

    private void iniciarProduto() {
        produto = new Produto();
        produto.setId(1L);
        produto.setDescricao("Produto Teste");
        produto.setPreco(BigDecimal.valueOf(10.0));
    }


    private void iniciarPedido() {
        ItemPedidoDTO itemPedidoDTO = new ItemPedidoDTO();
        itemPedidoDTO.setProduto(new ProdutoDTO(1L, "Produto Teste", BigDecimal.TEN));
        itemPedidoDTO.setQuantidade(2);

        pedidoDTO = new PedidoDTO();
        pedidoDTO.setId(1L);
        pedidoDTO.setCliente(clienteMapper.toDTO(cliente));
        pedidoDTO.setItens(Set.of(itemPedidoDTO));
        pedidoDTO.setData(LocalDate.now());
        pedidoDTO.setStatus(StatusPedido.ATIVO);
        pedidoDTO.setValorTotal(BigDecimal.valueOf(20));

        pedido = new Pedido(
                pedidoDTO.getId(),
                clienteMapper.toEntity(pedidoDTO.getCliente()),
                pedidoDTO.getData(),
                Set.of(new ItemPedido(1L, pedido, produto, 3)),
                pedidoDTO.getValorTotal(),
                pedidoDTO.getStatus()
        );
    }

    private void iniciarCriarPedido() {
        criarPedidoDTO = new CriarPedidoDTO();
        criarPedidoDTO.setClienteId(5L);
        criarPedidoDTO.setData(LocalDate.now());
        criarPedidoDTO.setItens(Set.of(criarItemPedidoDTO));
        criarPedidoDTO.setValorTotal(pedidoDTO.getValorTotal());
    }

    private void iniciarCriarItemPedidoDTO() {
        criarItemPedidoDTO = new CriarItemPedidoDTO();
        criarItemPedidoDTO.setProdutoId(1L);
        criarItemPedidoDTO.setQuantidade(2);
    }
}