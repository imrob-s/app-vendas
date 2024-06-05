
package dev.imrob.appvendas.entity.dto;

/**
 *
 * @author Rob
 */
public class CriarItemPedidoDTO {
    private Long produtoId;
    private Integer quantidade;

    public CriarItemPedidoDTO(Long produtoId, Integer quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public CriarItemPedidoDTO() {
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
