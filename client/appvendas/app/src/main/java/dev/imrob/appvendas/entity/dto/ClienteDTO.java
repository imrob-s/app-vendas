
package dev.imrob.appvendas.entity.dto;

import java.math.BigDecimal;

/**
 *
 * @author Rob
 */
public class ClienteDTO {
    private Long id;
    private String nome;
    private BigDecimal limiteCompra;
    private Integer diaFechamentoFatura;

    public ClienteDTO() {
    }

    public ClienteDTO(Long id, String nome, BigDecimal limiteCompra, Integer diaFechamentoFatura) {
        this.id = id;
        this.nome = nome;
        this.limiteCompra = limiteCompra;
        this.diaFechamentoFatura = diaFechamentoFatura;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getLimiteCompra() {
        return limiteCompra;
    }

    public void setLimiteCompra(BigDecimal limiteCompra) {
        this.limiteCompra = limiteCompra;
    }

    public Integer getDiaFechamentoFatura() {
        return diaFechamentoFatura;
    }

    public void setDiaFechamentoFatura(Integer diaFechamentoFatura) {
        this.diaFechamentoFatura = diaFechamentoFatura;
    }

    @Override
    public String toString() {
        return nome;
    }
    
}
