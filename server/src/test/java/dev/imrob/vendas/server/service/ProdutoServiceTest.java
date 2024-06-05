package dev.imrob.vendas.server.service;

import dev.imrob.vendas.server.dto.ProdutoDTO;
import dev.imrob.vendas.server.dto.mapper.ProdutoMapper;
import dev.imrob.vendas.server.entity.Produto;
import dev.imrob.vendas.server.repository.ProdutoRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @Spy
    private ProdutoMapper mapper = ProdutoMapper.INSTANCE;

    @InjectMocks
    private ProdutoService service;

    private Produto produto;
    private ProdutoDTO produtoDTO;
    private List<Produto> listaProdutos;

    @BeforeEach
    void setUp() {
        produtoDTO = new ProdutoDTO(null, "Coca Cola 2L", BigDecimal.valueOf(10.0));
        produto = new Produto(1L, "Coca Cola 2L", BigDecimal.valueOf(10.0));

        Produto produto2 = new Produto(2L, "Coca Cola Lata 350ml", BigDecimal.valueOf(3.47));
        Produto produto3 = new Produto(3L, "Arroz Broto Legal 5kg", BigDecimal.valueOf(27.32));
        Produto produto4 = new Produto(4L, "Feijão Preto 1kg", BigDecimal.valueOf(8.99));
        listaProdutos = List.of(produto2, produto3, produto4);
    }

    @Test
    void findById_deveRetornarProdutoDTO_quandoEncontrado() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoDTO resultado = service.findById(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(produto.getId());
        assertThat(resultado.getDescricao()).isEqualTo(produto.getDescricao());
        assertThat(resultado.getPreco()).isEqualTo(produto.getPreco());
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoEncontrado() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAll_deveRetornarListaDeProdutoDTO() {
        when(repository.findAllByOrderByDescricaoAsc()).thenReturn(listaProdutos);

        List<ProdutoDTO> resultado = service.findAll();

        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(listaProdutos.size());
        for (int i = 0; i < resultado.size(); i++) {
            assertThat(resultado.get(i).getId()).isEqualTo(listaProdutos.get(i).getId());
            assertThat(resultado.get(i).getDescricao()).isEqualTo(listaProdutos.get(i).getDescricao());
            assertThat(resultado.get(i).getPreco()).isEqualTo(listaProdutos.get(i).getPreco());
        }
    }

    @Test
    void save_deveSalvarProduto_eRetornarId() {
        when(repository.save(any(Produto.class))).thenReturn(produto);

        Long resultado = service.save(produtoDTO);

        assertThat(resultado).isEqualTo(produto.getId());
    }

    @Test
    void save_deveLancarExcecao_quandoCamposInvalidos() {
        ProdutoDTO produtoDTOInvalido = new ProdutoDTO();
        assertThatThrownBy(() -> service.save(produtoDTOInvalido)).isInstanceOf(ConstraintViolationException.class);

        produtoDTO.setDescricao("");
        assertThatThrownBy(() -> service.save(produtoDTO)).isInstanceOf(ConstraintViolationException.class);

        produtoDTO.setDescricao(null);
        assertThatThrownBy(() -> service.save(produtoDTO)).isInstanceOf(ConstraintViolationException.class);

        produtoDTO.setDescricao(" ");
        assertThatThrownBy(() -> service.save(produtoDTO)).isInstanceOf(ConstraintViolationException.class);

        produtoDTO.setDescricao("a");
        assertThatThrownBy(() -> service.save(produtoDTO)).isInstanceOf(ConstraintViolationException.class);

        produtoDTO.setDescricao("a".repeat(101));
        assertThatThrownBy(() -> service.save(produtoDTO)).isInstanceOf(ConstraintViolationException.class);

        produtoDTO.setPreco(null);
        assertThatThrownBy(() -> service.save(produtoDTO)).isInstanceOf(ConstraintViolationException.class);

        produtoDTO.setPreco(BigDecimal.ZERO);
        assertThatThrownBy(() -> service.save(produtoDTO)).isInstanceOf(ConstraintViolationException.class);

        produtoDTO.setPreco(BigDecimal.valueOf(-1));
        assertThatThrownBy(() -> service.save(produtoDTO)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void save_deveLancarExcecao_quandoHouverErroAoSalvarNoRepositorio() {
        when(repository.save(any(Produto.class))).thenThrow(new RuntimeException("Erro ao salvar"));

        assertThatThrownBy(() -> service.save(produtoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao salvar");
    }

    @Test
    void update_deveAtualizarProduto() {
        produtoDTO.setId(1L);
        when(repository.findById(produto.getId())).thenReturn(Optional.of(produto));
        when(repository.save(any(Produto.class))).thenReturn(produto);

        assertDoesNotThrow(() -> service.update(produtoDTO));
    }
    @Test
    void update_deveLancarExcecao_quandoErroAoAtualizarNoRepositorio() {
        produtoDTO.setId(1L);
        when(repository.findById(produtoDTO.getId())).thenReturn(Optional.of(produto));
        when(repository.save(any(Produto.class))).thenThrow(new RuntimeException("Erro ao atualizar"));

        assertThatThrownBy(() -> service.update(produtoDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao atualizar");
    }

    @Test
    void delete_deveLancarExcecao_quandoIdNaoEncontrado() {
        Long idInexistente = 999L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(idInexistente))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_deveDeletarProduto() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
    }

    @Test
    void delete_deveLancarExcecao_quandoNaoEncontrado() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(EntityNotFoundException.class);
    }


    @Test
    void getRepository_deveRetornarProdutoRepository() {
        assertThat(service.getRepository()).isEqualTo(repository);
    }

}