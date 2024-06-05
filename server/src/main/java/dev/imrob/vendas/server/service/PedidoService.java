package dev.imrob.vendas.server.service;

import dev.imrob.vendas.server.dto.CriarItemPedidoDTO;
import dev.imrob.vendas.server.dto.CriarPedidoDTO;
import dev.imrob.vendas.server.dto.PedidoDTO;
import dev.imrob.vendas.server.dto.mapper.PedidoMapper;
import dev.imrob.vendas.server.entity.*;
import dev.imrob.vendas.server.exception.*;
import dev.imrob.vendas.server.repository.ClienteRepository;
import dev.imrob.vendas.server.repository.FiltroPedidoRepository;
import dev.imrob.vendas.server.repository.PedidoRepository;
import dev.imrob.vendas.server.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PedidoService implements CrudService<PedidoDTO> {
    private final PedidoRepository repository;
    private final FiltroPedidoRepository filtroRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoMapper mapper;

    @Override
    public JpaRepository<?, Long> getRepository() {
        return repository;
    }

    @Transactional(readOnly = true)
    @Override
    public PedidoDTO findById(Long id) {
        Pedido pedido = idExisteOuException(id, Pedido.class);
        return mapper.toDto(pedido);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PedidoDTO> findAll() {
        return mapper.toDto(repository.findAll());
    }


    /**
     * Cria um novo pedido, validando os dados, os itens, o limite de crédito do cliente e salvando no banco de dados.
     *
     * @param pedidoDTO O DTO com os dados do pedido.
     * @return O ID do pedido criado.
     * @throws ConstraintViolationException Se houver erros de validação nos dados ou nos itens do pedido.
     * @throws LimiteCreditoException Se o cliente não tiver limite de crédito suficiente.
     * @throws EntityNotFoundException Se o cliente ou algum produto não for encontrado.
     */
    @Transactional
    public Long criarPedido(CriarPedidoDTO pedidoDTO) {
        validarCampos(pedidoDTO);
        validarItemPedido(pedidoDTO.getItens());
        if (pedidoDTO.getValorTotal().equals(BigDecimal.ZERO)) {
            pedidoDTO.setValorTotal(valorTotal(pedidoDTO));
        }
        validarLimiteCompra(pedidoDTO);
        Pedido pedido = mapper.toEntity(pedidoDTO, clienteRepository);

        Set<ItemPedido> itens = pedidoDTO.getItens().stream()
                .map(itemDTO -> {
                    ItemPedido itemPedido = mapper.toEntity(itemDTO, produtoRepository);
                    itemPedido.setPedido(pedido);
                    return itemPedido;
                })
                .collect(Collectors.toSet());

        pedido.setItens(itens);

        return repository.save(pedido).getId();
    }

    @Override
    public Long save(PedidoDTO dto) {
        throw new MetodoNaoPermitidoException("Utilize o método save(CriarPedidoDTO) para criar um pedido.");
    }

    @Transactional
    @Override
    public void update(PedidoDTO dto) {
        throw new MetodoNaoPermitidoException("Não é possível atualizar um pedido.");
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Pedido pedido = idExisteOuException(id, Pedido.class);
        repository.delete(pedido);
    }

    /**
     * Cancela um pedido.
     * Define o status do pedido para EXCLUÍDO
     *
     * @param id O ID do pedido a ser cancelado.
     */
    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = idExisteOuException(id, Pedido.class);
        pedido.setStatus(StatusPedido.EXCLUIDO);
    }

    /**
     * Calcula o total de compras de um cliente desde o último fechamento da fatura.
     *
     * @param clienteId O ID do cliente.
     * @return O total de compras do cliente desde o último fechamento da fatura.
     */
    @Transactional(readOnly = true)
    public BigDecimal totalComprasDesdeFechamento(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(
                () -> new EntityNotFoundException("Cliente com id %d não foi encontrado.".formatted(clienteId))
        );
        LocalDate dataFechamento = calcularUltimaDataFechamento(cliente.getDiaFechamentoFatura());
        BigDecimal total = repository.totalComprasDesdeData(clienteId, dataFechamento);
        return Objects.requireNonNullElse(total, BigDecimal.ZERO);
    }

    /**
     * Calcula o total de compras de um cliente desde uma data específica.
     *
     * @param clienteId O ID do cliente.
     * @param data     A data a partir da qual se deseja calcular o total de compras.
     * @return O total de compras do cliente desde a data especificada.
     */
    @Transactional(readOnly = true)
    public BigDecimal totalComprasDesdeData(Long clienteId, LocalDate data){
        idExisteOuException(clienteId, Long.class);
        return repository.totalComprasDesdeData(clienteId, data);
    }

    /**
     * Valida se o cliente possui limite de crédito suficiente para realizar o pedido.
     *
     * @param pedido O DTO do pedido a ser validado.
     * @throws LimiteCreditoException Lançada se o cliente não possuir limite de crédito suficiente.
     */
    private void validarLimiteCompra(CriarPedidoDTO pedido) {
        Cliente cliente = clienteRepository.findById(pedido.getClienteId()).orElseThrow(
                () -> new EntityNotFoundException("Cliente com id %d não foi encontrado.".formatted(pedido.getClienteId()))
        );
        if (cliente.getLimiteCompra() == null) {
            throw new LimiteCreditoException("Cliente não possui limite de crédito.");
        }
        BigDecimal limiteCredito = cliente.getLimiteCompra();
        BigDecimal totalComprasDesdeFechamento = totalComprasDesdeFechamento(cliente.getId());
        BigDecimal limiteDisponivel = limiteCredito.subtract(totalComprasDesdeFechamento);

        if (pedido.getValorTotal().compareTo(limiteDisponivel) > 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate dataFechamentoFatura = calcularProximaDataFechamento(cliente.getDiaFechamentoFatura());
            throw new LimiteCreditoException(
                    "Limite disponível: " + limiteDisponivel +
                    " | Data de fechamento da fatura: " + dataFechamentoFatura.format(formatter));
        }
    }

    /**
     * Calcula o total de compras realizadas por um cliente a partir de uma data específica.
     *
     * @param clienteId O ID do cliente.
     * @param data      A data a partir da qual o total de compras deve ser calculado.
     * @return O valor total das compras realizadas pelo cliente desde a data especificada.
     */
    public BigDecimal getTotalComprasDesdeData(Long clienteId, LocalDate data) {
        return repository.totalComprasDesdeData(clienteId, data);
    }

    /**
     * Retorna uma lista de pedidos agrupados por cliente, juntamente com o total gasto por cada cliente.
     *
     * @return Uma lista de arrays de objetos, onde cada array representa um cliente e seu total gasto:
     *         <ul>
     *         <li>Object[0]: {@link Long} - ID do cliente.</li>
     *         <li>Object[1]: {@link String} - Nome do cliente.</li>
     *         <li>Object[2]: {@link BigDecimal} - Total gasto pelo cliente.</li>
     *         </ul>
     */
    public List<Object[]> getPedidosAgrupadosPorCliente() {
        return repository.pedidosAgrupadosPorCliente();
    }

    /**
     * Retorna uma lista de produtos que foram incluídos em pedidos, juntamente com a
     * quantidade total pedida e o valor total para cada produto.
     *
     * @return Uma lista de arrays de objetos, onde cada array representa um produto e suas estatísticas de pedido:
     *         <ul>
     *         <li>Object[0]: {@link Long} - ID do produto.</li>
     *         <li>Object[1]: {@link String} - Nome do produto.</li>
     *         <li>Object[2]: {@link Long} - Quantidade total pedida do produto.</li>
     *         <li>Object[3]: {@link BigDecimal} - Valor total dos pedidos para o produto.</li>
     *         </ul>
     */
    public List<Object[]> getPedidosAgrupadosPorProduto() {
        return repository.pedidosAgrupadosPorProduto();
    }

    /**
     * Busca pedidos com base em vários critérios de filtro.
     *
     * @param dataInicio Data inicial do intervalo de datas para filtrar pedidos, inclusive (pode ser nulo).
     * @param dataFim   Data final do intervalo de datas para filtrar pedidos, inclusive (pode ser nulo).
     * @param clienteId ID do cliente para filtrar pedidos (pode ser nulo).
     * @param produtoId ID do produto para filtrar pedidos (pode ser nulo).
     * @param status     Indica se deve filtrar por pedidos ATIVO (true), EXCLUIDO (false) ou ambos (nulo).
     * @return Uma lista de pedidos que correspondem aos critérios de filtro fornecidos.
     */
    public List<PedidoDTO> filtrarPedidosPor(LocalDate dataInicio, LocalDate dataFim, Long clienteId,
                                               Long produtoId, StatusPedido status) {
        return mapper.toDto(filtroRepository.filtrarPedidosPor(dataInicio, dataFim, clienteId, produtoId, status));
    }

    /**
     * Calcula o valor total de um pedido.
     *
     * @param pedido O DTO do pedido.
     * @return O valor total do pedido.
     */
    private BigDecimal valorTotal(CriarPedidoDTO pedido) {
        Set<Long> produtoIds = pedido.getItens().stream()
                .map(CriarItemPedidoDTO::getProdutoId)
                .collect(Collectors.toSet());

        Map<Long, Produto> produtos = produtoRepository.findAllById(produtoIds)
                .stream()
                .collect(Collectors.toMap(Produto::getId, p -> p));

        BigDecimal valorTotalPedido = BigDecimal.ZERO;
        for (CriarItemPedidoDTO item : pedido.getItens()) {
            Produto produto = produtos.get(item.getProdutoId());
            if (produto != null) {
                BigDecimal valorTotalItem = produto.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
                valorTotalPedido = valorTotalPedido.add(valorTotalItem);
            }
        }
        return valorTotalPedido;
    }

    /**
     * Calcula a data do último fechamento da fatura.
     *
     * @param diaFechamento O dia do mês em que a fatura é fechada.
     * @return A data do último fechamento da fatura.
     */
    private LocalDate calcularUltimaDataFechamento(int diaFechamento) {
        LocalDate hoje = LocalDate.now();
        int dia = hoje.getDayOfMonth();
        YearMonth anoMes;

        if (dia >= diaFechamento) {
            anoMes = YearMonth.from(hoje);
        } else {
            anoMes = YearMonth.from(hoje).minusMonths(1);
        }

        int ultimoDiaMes = anoMes.lengthOfMonth();
        int diaAjustado = Math.min(diaFechamento, ultimoDiaMes);
        return anoMes.atDay(diaAjustado);
    }

    /**
     * Calcula a data do próximo fechamento da fatura.
     *
     * @param diaFechamento O dia do mês em que a fatura é fechada.
     * @return A data do próximo fechamento da fatura.
     */
    private LocalDate calcularProximaDataFechamento(int diaFechamento) {
        return calcularUltimaDataFechamento(diaFechamento).plusMonths(1);
    }

    /**
     * Valida uma coleção de itens de pedido.
     *
     * <p>Esta função verifica se a coleção de itens de pedido é válida de acordo com as seguintes regras:
     * <ul>
     *     <li>A coleção não pode ser nula ou vazia.</li>
     *     <li>Cada item de pedido na coleção deve ser válido de acordo com as restrições definidas na classe {@link ItemPedido}.</li>
     * </ul>
     * </p>
     *
     * @param itens A coleção de itens de pedido a ser validada.
     * @throws ItensPedidoException Se a coleção de itens de pedido for nula ou vazia.
     * @throws ConstraintViolationException Se algum item de pedido na coleção for inválido.
     */
    private void validarItemPedido(Set<CriarItemPedidoDTO> itens){
        for (CriarItemPedidoDTO item : itens) {
            Set<ConstraintViolation<CriarItemPedidoDTO>> violations = Validation
                    .buildDefaultValidatorFactory()
                    .getValidator().validate(item);
            if (!violations.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (var constraintViolation : violations) {
                    sb.append("[").append(constraintViolation.getPropertyPath().toString())
                            .append(" : ").append(constraintViolation.getMessage()).append("] ");
                }
                throw new ConstraintViolationException("Erro de validação: " + sb, violations);
            }
        }
    }

    /**
     * Valida os campos de um DTO de criação de pedido.
     *
     * <p>Esta função verifica se os campos do DTO {@link CriarPedidoDTO}
     * são válidos de acordo com as restrições definidas na classe.</p>
     *
     * @param dto O DTO de criação de pedido a ser validado.
     * @throws ConstraintViolationException Se algum campo do DTO for inválido.
     */
    private void validarCampos(CriarPedidoDTO dto) {
        Set<ConstraintViolation<CriarPedidoDTO>> violations = Validation
                .buildDefaultValidatorFactory()
                .getValidator().validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<CriarPedidoDTO> constraintViolation : violations) {
                sb.append("[").append(constraintViolation.getPropertyPath().toString())
                        .append(" : ").append(constraintViolation.getMessage()).append("] ");
            }
            throw new ConstraintViolationException("Erro de validação: " + sb, violations);
        }
    }
}
