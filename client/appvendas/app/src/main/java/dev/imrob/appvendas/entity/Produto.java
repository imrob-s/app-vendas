/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.imrob.appvendas.entity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author Rob
 */
public class Produto {
    private Long id;
    private String descricao;
    private BigDecimal preco;

    public Produto() {
    }

    public Produto(Long id, String descricao, BigDecimal preco) {
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
    
    public Object[] toTableRow(int rowNum) {
        NumberFormat nf = new DecimalFormat("R$ #,##0.00");
        return new Object[]{false, id, this, nf.format(preco)};
    }
    
    @Override
    public String toString() {
        return descricao==null ? "": descricao;
    }
}
