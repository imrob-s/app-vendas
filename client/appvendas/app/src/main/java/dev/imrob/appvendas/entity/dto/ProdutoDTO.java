
package dev.imrob.appvendas.entity.dto;

import java.math.BigDecimal;

/**
 *
 * @author Rob
 */
class ProdutoDTO {
    private Long id;

    private String descricao;

    private BigDecimal preco;

    public ProdutoDTO() {
    }

    public ProdutoDTO(Long id, String descricao, BigDecimal preco) {
        this.id = id;
        this.descricao = descricao;
        this.preco = preco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }
    
    
}
