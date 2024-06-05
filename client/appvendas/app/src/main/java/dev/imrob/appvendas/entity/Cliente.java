
package dev.imrob.appvendas.entity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author Rob
 */
public class Cliente {
    private Long id;
    private String nome;
    private BigDecimal limiteCompra;
    private Integer diaFechamentoFatura;

    public Cliente() {
    }

    public Cliente(Long id, String nome, BigDecimal limiteCompra, Integer diaFechamentoFatura) {
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
    
    public Object[] toTableRow(int rowNum) {
        NumberFormat nf = new DecimalFormat("R$ #,##0.00");
        return new Object[]{false, id, this, nf.format(limiteCompra), diaFechamentoFatura};
    }
    
    @Override
    public String toString() {
        return nome==null ? "": nome;
    }
}
